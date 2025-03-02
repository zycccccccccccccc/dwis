package tech.hciot.dwis.business.application;

import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;
import static tech.hciot.dwis.base.util.StandardTimeUtil.dateTimeStr;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCell;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.SCRAP_REASON_EXPORT_FAILED;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.ScrapReasonCodeRepository;
import tech.hciot.dwis.business.domain.ScrapReasonRecordRepository;
import tech.hciot.dwis.business.domain.model.ScrapReasonRecord;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.ScrapWheelDetail;

@Service
@Slf4j
public class ScrapAnalysisService {

  private static final int PAGE_SIZE = 50;
  private static final int TOTAL_ROW = 54;
  private static final int TOTAL_COLUMN = 10;

  @Autowired
  private ScrapReasonRecordRepository scrapReasonRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private ScrapReasonCodeRepository scrapReasonCodeRepository;

  @Autowired
  private EntityManager entityManager;

  public void addScrapReasonRecord(ScrapReasonRecord scrapReasonRecord) {
    scrapReasonRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    scrapReasonRecord.setCreateTime(new Date());
    scrapReasonRecordRepository.save(scrapReasonRecord);
  }

  public Page<ScrapReasonRecord> find(String inspectorId, String wheelSerial, String scrapCode, String scrapReasonCode,
      Integer currentPage, Integer pageSize) {
    return find(inspectorId, wheelSerial, scrapCode, scrapReasonCode, null, currentPage, pageSize);
  }

  public Page<ScrapReasonRecord> find(String inspectorId, String wheelSerial, String scrapCode, String scrapReasonCode,
      String createDate, Integer currentPage, Integer pageSize) {
    Specification<ScrapReasonRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotBlank(scrapCode)) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (StringUtils.isNotBlank(scrapReasonCode)) {
        list.add(criteriaBuilder.equal(root.get("scrapReasonCode"), scrapReasonCode));
      }
      if (StringUtils.isNotBlank(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }

      if (StringUtils.isAllBlank(wheelSerial, scrapCode, scrapReasonCode)) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      }

      if (StringUtils.isNotBlank(createDate)) {
        Date date = parseDate(createDate);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), date));
      }

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return scrapReasonRecordRepository.findAll(specification, pageable).map(scrapReasonRecord -> {
      scrapReasonRecord
          .setScrapReason(
              scrapReasonCodeRepository.findByScrapReasonCode(scrapReasonRecord.getScrapReasonCode())
                  .map(scrapReasonCode1 -> scrapReasonCode1.getExplain())
                  .orElse(null));
      return scrapReasonRecord;
    });
  }

  public void export(HttpServletResponse response) {
    List<ScrapReasonRecord> scrapReasonRecords =
        find(null, null, null, null, ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()), null, null).getContent();
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet();

      String fileName = "废品分析" + ".xlsx";

      String[] headers = {
          "车轮序列号",
          "轮型",
          "废品代码",
          "原因",
          "操作日期"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      ExcelUtil.createTitleRow(sheet, headers);

      // 内容
      int rowNum = 1;
      for (ScrapReasonRecord scrapReasonRecord : scrapReasonRecords) {
        Row row1 = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row1, columnNum++, scrapReasonRecord.getWheelSerial(), lengthArray);
        createCell(row1, columnNum++, scrapReasonRecord.getDesign(), lengthArray);
        createCell(row1, columnNum++, scrapReasonRecord.getScrapCode(), lengthArray);
        createCell(row1, columnNum++, scrapReasonRecord.getScrapReason(), lengthArray);
        createCell(row1, columnNum++, scrapReasonRecord.getCreateTime(), lengthArray);
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
      throw SCRAP_REASON_EXPORT_FAILED.getPlatformException();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public void exportDetail(HttpServletResponse response) {
    List<ScrapWheelDetail> scrapWheelDetailList = entityManager.createNamedQuery("ScrapWheelDetail").getResultList();
    try {
      ClassPathResource resource = new ClassPathResource("static/scrap-wheel-detail.xlsx");
      InputStream inputStream = resource.getInputStream();

      Workbook workbook = new XSSFWorkbook(inputStream);
      generateWorkSheet(workbook, scrapWheelDetailList);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=scrap-wheel-detail.xlsx");
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void generateWorkSheet(Workbook workbook, List<ScrapWheelDetail> scrapWheelDetailList) {
    Date now = new Date();
    int pageSum = (scrapWheelDetailList.size() + PAGE_SIZE - 1) / PAGE_SIZE;
    Sheet sheet = workbook.getSheetAt(0);

    Row row = sheet.getRow(1);
    row.getCell(0).setCellValue("查询日期:" + dateTimeStr(now));

    for (int page = 1; page < pageSum; page++) {
      log.info("copy page {}", page);
      ExcelUtil.copyCell(sheet, 0, 0,
          sheet, TOTAL_ROW * page, 0,
          TOTAL_ROW, TOTAL_COLUMN);
    }

    for (int k = 0; k < pageSum; k++) {
      int currentPageFirstRow = k * TOTAL_ROW;
      for (int i = 0; i < PAGE_SIZE; i++) {
        int index = k * PAGE_SIZE + i;
        if (index < scrapWheelDetailList.size()) {
          ScrapWheelDetail scrapWheelDetail = scrapWheelDetailList.get(index);
          row = sheet.getRow(currentPageFirstRow + i + 3);
          row.getCell(0).setCellValue(index + 1);
          row.getCell(1).setCellValue(scrapWheelDetail.getWheelSerial());
          row.getCell(2).setCellValue(scrapWheelDetail.getDesign());
          row.getCell(3).setCellValue(scrapWheelDetail.getScrapCode());
          row.getCell(5).setCellValue(scrapWheelDetail.getXrayReq() == 1 ? "√" : "");
          row.getCell(6).setCellValue(scrapWheelDetail.getReworkCode());
          row.getCell(7).setCellValue(scrapWheelDetail.getTestCode());
          row.getCell(8).setCellValue(scrapWheelDetail.getMecSerial());
          row.getCell(9).setCellValue(scrapWheelDetail.getHeatKey());
        } else {
          break;
        }
      }

      row = sheet.getRow(currentPageFirstRow + TOTAL_ROW - 1);
      row.getCell(9).setCellValue(StringUtils.join("Page ", k + 1, " of ", pageSum));
    }

    sheet.protectSheet("dwis");
  }
}
