package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCell;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.PERFORMANCE_EXPORT_FAILED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.MecPropertyRepository;
import tech.hciot.dwis.business.domain.OperatingTimeCtrRepository;
import tech.hciot.dwis.business.domain.model.MecProperty;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.PerformanceReportResponse;

@Service
@Slf4j
public class PerformanceService {

  @Autowired
  private MecPropertyRepository mecPropertyRepository;

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  @Autowired
  private ChemistryDetailService chemistryDetailService;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private PourRecordService pourRecordService;

  public void addPerformance(MecProperty mecProperty) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      mecProperty.setOpeDT(ope);
    });
    mecProperty.setCreateDate(new Date());
    mecPropertyRepository.save(mecProperty);
  }

  public void deletePerformance(Integer id) {
    mecPropertyRepository.deleteById(id);
  }

  public void modifyPerformance(Integer id, MecProperty editMecProperty) {
    mecPropertyRepository.findById(id).ifPresent(mecProperty -> {
      BeanUtils.copyProperties(editMecProperty, mecProperty, "id", "opeDT", "createDate");
      mecPropertyRepository.save(mecProperty);
    });
  }

  public Page<MecProperty> find(String wheelSerial, String mecSerial, String inspectorId, Integer currentPage,
      Integer pageSize) {
    Specification<MecProperty> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(mecSerial)) {
        list.add(criteriaBuilder.like(root.get("mecSerial"), "%" + mecSerial + "%"));
      }
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }

      if (StringUtils.isAllEmpty(wheelSerial, mecSerial)) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return mecPropertyRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public PerformanceReportResponse report(Integer id) {
    PerformanceReportResponse performanceReportResponse = new PerformanceReportResponse();
    mecPropertyRepository.findById(id).ifPresent(mecProperty -> {
      BeanUtils.copyProperties(mecProperty, performanceReportResponse);
      Integer ladleId =
          wheelRecordService.findByWheelSerial(performanceReportResponse.getWheelSerial())
              .map(wheelRecord -> wheelRecord.getLadleId()).orElse(null);
      chemistryDetailService.findByLadleId(ladleId).ifPresent(chemistryDetail -> {
        performanceReportResponse.setC(chemistryDetail.getC());
        performanceReportResponse.setSi(chemistryDetail.getSi());
        performanceReportResponse.setMn(chemistryDetail.getMn());
        performanceReportResponse.setP(chemistryDetail.getP());
        performanceReportResponse.setS(chemistryDetail.getS());
        performanceReportResponse.setCr(chemistryDetail.getCr());
        performanceReportResponse.setCu(chemistryDetail.getCu());
        performanceReportResponse.setMo(chemistryDetail.getMo());
        performanceReportResponse.setNi(chemistryDetail.getNi());
        performanceReportResponse.setV(chemistryDetail.getV());
        performanceReportResponse.setAl(chemistryDetail.getAl());
        performanceReportResponse.setSn(chemistryDetail.getSn());
        performanceReportResponse.setTi(chemistryDetail.getTi());
        performanceReportResponse.setNb(chemistryDetail.getNb());
      });
    });
    String mecSerial = performanceReportResponse.getMecSerial();
    if (StringUtils.contains(mecSerial, "-")) {
      performanceReportResponse.setMecSerial(mecSerial.substring(0, mecSerial.lastIndexOf("-")));
    }
    return performanceReportResponse;
  }

  public Page<MecProperty> summary(String startDate, String endDate, Integer currentPage, Integer pageSize) {
    Date start = parseDate(startDate);
    Date end = parseDate(endDate);
    Pageable pageable = pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return mecPropertyRepository.findSummary(start, end, pageable);
  }

  public void exportSummary(String startDate, String endDate, HttpServletResponse response) {
    List<MecProperty> mecPropertyList = summary(startDate, endDate, null, null).getContent();
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet("性能汇总");

      String fileName = "性能汇总" + ".xlsx";

      String[] headers = {
          "车轮序列号",
          "机械性能批次号",
          "浇注批次1",
          "浇注批次2",
          "残余应力",
          "抗拉强度",
          "延伸率",
          "断面硬度1",
          "断面硬度2",
          "断面硬度3",
          "平均冲击功",
          "最低冲击功",
          "冲击功1",
          "冲击功2",
          "冲击功3",
          "性能复试"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      Row title = sheet.createRow(0);
      Cell cellTitle = title.createCell(0);
      XSSFRichTextString text = new XSSFRichTextString("性能汇总");
      cellTitle.setCellValue(text);

      cellTitle = title.createCell(1);
      text = new XSSFRichTextString("开始日期：");
      cellTitle.setCellValue(text);

      cellTitle = title.createCell(2);
      text = new XSSFRichTextString(startDate);
      cellTitle.setCellValue(text);

      cellTitle = title.createCell(3);
      text = new XSSFRichTextString("结束日期：");
      cellTitle.setCellValue(text);

      cellTitle = title.createCell(4);
      text = new XSSFRichTextString(endDate);
      cellTitle.setCellValue(text);

      Row row = sheet.createRow(1);
      for (int i = 0; i < headers.length; i++) {
        Cell cell = row.createCell(i);
        XSSFRichTextString textHeader = new XSSFRichTextString(headers[i]);
        cell.setCellValue(textHeader);
      }

      // 内容
      int rowNum = 2;
      for (MecProperty mecProperty : mecPropertyList) {
        Row row1 = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row1, columnNum++, mecProperty.getWheelSerial(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getMecSerial(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getPourBatch1(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getPourBatch2(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getResidualStress(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getTensile(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getElongation(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getHardnessLocation1(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getHardnessLocation2(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getHardnessLocation3(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getImpactAvg(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getImpactMin(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getImpactLocation1(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getImpactLocation2(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getImpactLocation3(), lengthArray);
        createCell(row1, columnNum++, mecProperty.getRetest() == 1 ? "是" : "否", lengthArray);
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
      throw PERFORMANCE_EXPORT_FAILED.getPlatformException();
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
