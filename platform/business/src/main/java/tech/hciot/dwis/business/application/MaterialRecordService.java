package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.business.domain.model.MaterialRecord.STATUS_NEW;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCell;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.domain.MaterialRecordDetailRepository;
import tech.hciot.dwis.business.domain.MaterialRecordRepository;
import tech.hciot.dwis.business.domain.model.MaterialRecord;
import tech.hciot.dwis.business.domain.model.MaterialRecordDetail;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;

@Service
@Slf4j
public class MaterialRecordService {

  @Autowired
  private MaterialRecordRepository materialRecordRepository;

  @Autowired
  private MaterialRecordDetailRepository materialRecordDetailRepository;

  public Page<MaterialRecordDetail> find(String materialName,
                                         String batchNo,
                                         Integer status,
                                         String startTimeStr,
                                         String suspendTimeStr,
                                         String stopTimeStr,
                                         Integer currentPage,
                                         Integer pageSize) {
    Specification<MaterialRecordDetail> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (materialName != null) {
        list.add(criteriaBuilder.equal(root.get("materialName"), materialName));
      }
      if (batchNo != null) {
        list.add(criteriaBuilder.equal(root.get("batchNo"), batchNo));
      }
      if (status != null) {
        list.add(criteriaBuilder.equal(root.get("status"), status));
      }
      if (startTimeStr != null) {
        Date startTime = StandardTimeUtil.parseDate(startTimeStr);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startTime));
      }
      if (suspendTimeStr != null) {
        Date suspendTime = StandardTimeUtil.parseDate(suspendTimeStr);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("suspendTime"), suspendTime));
      }
      if (stopTimeStr != null) {
        Date stopTime = StandardTimeUtil.parseDate(stopTimeStr);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stopTime"), stopTime));
      }
      if (ObjectUtil.isAllEmpty(materialName, batchNo, status, startTimeStr, suspendTimeStr, stopTimeStr)) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return materialRecordDetailRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<String> findMaterialNameList(Integer depId) {
    return materialRecordRepository.findMaterialNameList(depId);
  }

  public Integer add(MaterialRecord materialRecord, String operatorId) {
    if(StringUtils.isBlank(materialRecord.getBatchNo())) {
      String batchNo = StringUtils.joinWith("_",
        materialRecord.getDept(),
        materialRecord.getManufacturerId(),
        materialRecord.getMaterialId(),
        StandardTimeUtil.simpleDateStr(new Date())
        );
      materialRecord.setBatchNo(batchNo);
//      materialRecord.setBatchNo(materialRecord.getDept() + "_" + materialRecord.getManufacturerId() + "_" + materialRecord.getProductTypeId());
    }
    materialRecord.setOperator(operatorId);
    materialRecord.setStatus(STATUS_NEW);
    materialRecord.setCreateTime(new Date());
    materialRecordRepository.save(materialRecord);
    return materialRecord.getId();
  }

  public void modify(Integer id, MaterialRecord newMaterialRecord) {
    MaterialRecord materialRecord = materialRecordRepository.findById(id).get();
    BeanUtil.copyProperties(newMaterialRecord, materialRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    materialRecordRepository.save(materialRecord);
  }

  public void start(List<Integer> idList, String operatorId) {
    List<MaterialRecord> materialRecordList = materialRecordRepository.findByIdIn(idList);
    materialRecordList.forEach(materialRecord -> {
      if (materialRecord.getStatus() != STATUS_NEW
          && materialRecord.getStatus() != MaterialRecord.STATUS_SUSPENDED) {
        throw PlatformException.badRequestException("只能选择新建或暂停状态的原材料");
      }
      materialRecord.setOperator(operatorId);
      materialRecord.setStatus(MaterialRecord.STATUS_STARTED);
      materialRecord.setStartTime(new Date());
    });
    materialRecordRepository.saveAll(materialRecordList);
  }

  public void suspend(List<Integer> idList, String operatorId) {
    List<MaterialRecord> materialRecordList = materialRecordRepository.findByIdIn(idList);
    materialRecordList.forEach(materialRecord -> {
      if (materialRecord.getStatus() != MaterialRecord.STATUS_STARTED) {
        throw PlatformException.badRequestException("只能选择开始状态的原材料");
      }
      materialRecord.setOperator(operatorId);
      materialRecord.setStatus(MaterialRecord.STATUS_SUSPENDED);
      materialRecord.setSuspendTime(new Date());
    });
    materialRecordRepository.saveAll(materialRecordList);
  }

  public void stop(List<Integer> idList, String operatorId) {
    List<MaterialRecord> materialRecordList = materialRecordRepository.findByIdIn(idList);
    materialRecordList.forEach(materialRecord -> {
      if (materialRecord.getStatus() != MaterialRecord.STATUS_STARTED
        && materialRecord.getStatus() != MaterialRecord.STATUS_SUSPENDED) {
        throw PlatformException.badRequestException("只能选择开始或暂停状态的原材料");
      }
      materialRecord.setOperator(operatorId);
      materialRecord.setStatus(MaterialRecord.STATUS_STOPPED);
      materialRecord.setStopTime(new Date());
    });
    materialRecordRepository.saveAll(materialRecordList);
  }

  public void export(List<MaterialRecordDetail> materialList, HttpServletResponse response) {
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet();

      String datePostfix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
      String fileName = "原材料-" + datePostfix + ".xlsx";

      String[] headers = {
        "原材料名称",
        "供应商名称",
        "批次号",
        "创建时间",
        "开始使用时间",
        "结束使用时间",
        "暂停时间",
        "状态"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      ExcelUtil.createTitleRow(sheet, headers);

      // 内容
      int rowNum = 1;
      for (MaterialRecordDetail material : materialList) {
        Row row1 = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row1, columnNum++, material.getMaterialName(), lengthArray);
        createCell(row1, columnNum++, material.getManufacturerName(), lengthArray);
        createCell(row1, columnNum++, material.getBatchNo(), lengthArray);
        createCell(row1, columnNum++, StandardTimeUtil.dateTimeStr(material.getCreateTime()), lengthArray);
        createCell(row1, columnNum++, StandardTimeUtil.dateTimeStr(material.getStartTime()), lengthArray);
        createCell(row1, columnNum++, StandardTimeUtil.dateTimeStr(material.getStopTime()), lengthArray);
        createCell(row1, columnNum++, StandardTimeUtil.dateTimeStr(material.getSuspendTime()), lengthArray);
        createCell(row1, columnNum++, parseStatus(material.getStatus()), lengthArray);
        rowNum++;
      }

      // 自动调整列宽
      ExcelUtil.autoSizeColumnWidth(sheet, lengthArray);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + fileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw PlatformException.badRequestException("原材料导出失败");
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }

  private String parseStatus(int status) {
    String statusStr;
    switch (status) {
      case MaterialRecord.STATUS_NEW:
        statusStr = "新创建";
        break;
      case MaterialRecord.STATUS_STARTED:
        statusStr = "在用";
        break;
      case MaterialRecord.STATUS_SUSPENDED:
        statusStr = "暂停";
        break;
      case MaterialRecord.STATUS_STOPPED:
        statusStr = "结束";
        break;
      default:
        statusStr = "";
    }
    return statusStr;
  }
}
