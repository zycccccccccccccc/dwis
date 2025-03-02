package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCell;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CHEMISTRY_EXPORT_FAILED;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
import tech.hciot.dwis.business.domain.AuditDetailRepository;
import tech.hciot.dwis.business.domain.AuditResultRepository;
import tech.hciot.dwis.business.domain.model.AuditDetail;
import tech.hciot.dwis.business.domain.model.AuditResult;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;

@Service
@Slf4j
public class AuditDetailService {

  @Autowired
  private AuditDetailRepository auditDetailRepository;

  @Autowired
  private AuditResultRepository auditResultRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<AuditDetail> find(String operator, Integer currentPage, Integer pageSize) {
    Specification<AuditDetail> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (operator != null) {
        list.add(criteriaBuilder.equal(root.get("operator"), operator));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("opeDT")));
      return query.getRestriction();
    };
    return auditDetailRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public void add(List<String> auditDetailStrList, String auditBatch, String operator) {
    List<AuditDetail> auditDetailList = parseAuditDetail(auditDetailStrList);
    if (auditDetailList.size() == 0) {
      return;
    }
    Date opeDT = operatingTimeCtrService.getQAOperatingTime();
    Date createTime = new Date();
    auditDetailList.forEach(auditDetail -> {
      auditDetail.setAuditBatch(auditBatch);
      auditDetail.setOperator(operator);
      auditDetail.setWheelSerial(auditDetail.getC105() + auditDetail.getC106() + auditDetail.getC111());
      auditDetail.setOpeDT(opeDT);
      auditDetail.setCreateTime(createTime);
    });
    auditDetailRepository.saveAll(auditDetailList);
  }

  private List<AuditDetail> parseAuditDetail(List<String> auditDetailStrList) {
    List<AuditDetail> auditDetailList = new ArrayList<>();
    if (auditDetailStrList.size() <= 1) {
      throw PlatformException.badRequestException("文件格式不正确");
    }
    String[] titles = auditDetailStrList.get(0).split(" ");
    if (titles.length < 1) {
      throw PlatformException.badRequestException("文件格式不正确");
    }
    Map<Integer, String> titleMap = new HashMap<>();
    for (int i = 0; i < titles.length; i++) {
      titleMap.put(i, titles[i]);
    }
    for (int i = 1; i < auditDetailStrList.size(); i++) {
      String[] fields = auditDetailStrList.get(i).split(" ", -1);
      if (fields.length < 1) {
        throw PlatformException.badRequestException("文件格式不正确");
      }

      Map<String, Object> auditDetailMap = new HashMap<>();
      for (int j = 0; j < fields.length; j++) {
        auditDetailMap.put(titleMap.get(j), fields[j]);
      }
      JSONObject json = new JSONObject(auditDetailMap);
      AuditDetail auditDetail = json.toJavaObject(AuditDetail.class);
      auditDetailList.add(auditDetail);
    }
    return auditDetailList;
  }

  public void modify(Integer id, AuditDetail newAuditDetail) {
    AuditDetail auditDetail = auditDetailRepository.findById(id).get();
    BeanUtil.copyProperties(newAuditDetail, auditDetail, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    auditDetailRepository.save(auditDetail);
  }

  public void delete(Integer id) {
    auditDetailRepository.findById(id).ifPresent(auditDetail -> {
      auditDetailRepository.deleteById(id);
    });
  }

  public List<AuditResult> audit(String auditBatch) {
    return auditResultRepository.findByAuditBatch(auditBatch);
  }

  public static void main(String[] args) {
    String a = "";
    String[] aa = a.split(" ");
    int x = aa.length;
  }

  public void export(String auditBatch, HttpServletResponse response) {
    List<AuditResult> auditResultList = audit(auditBatch);
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet("二维码校验");

      String fileName = "audit-" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xlsx";

      String[] headers = {
        "批次号",
        "车轮序列号",
        "带尺",
        "国内轮轮型",
        "国内轮轴孔",
        "国外轮轮型",
        "国外轮轴孔",
        "车轮轮型",
        "车轮带尺",
        "车轮轴孔",
        "车轮成品状态"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      ExcelUtil.createTitleRow(sheet, headers);

      // 内容
      int rowNum = 1;
      for (AuditResult auditResult : auditResultList) {
        Row row = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row, columnNum++, auditResult.getAuditBatch(), lengthArray);
        createCell(row, columnNum++, auditResult.getWheelSerial(), lengthArray);
        createCell(row, columnNum++, auditResult.getAuditTapeSize(), lengthArray);
        createCell(row, columnNum++, auditResult.getInternalDesign(), lengthArray);
        createCell(row, columnNum++, auditResult.getInternalBoreSize(), lengthArray);
        createCell(row, columnNum++, auditResult.getExternalDesign(), lengthArray);
        createCell(row, columnNum++, auditResult.getExternalBoreSize(), lengthArray);
        createCell(row, columnNum++, auditResult.getDesign(), lengthArray);
        createCell(row, columnNum++, auditResult.getTapeSize(), lengthArray);
        createCell(row, columnNum++, auditResult.getBoreSize(), lengthArray);
        createCell(row, columnNum++, auditResult.getFinished() == 0 ? "否" : "是", lengthArray);
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
      throw CHEMISTRY_EXPORT_FAILED.getPlatformException();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }
}
