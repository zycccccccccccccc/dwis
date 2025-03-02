package tech.hciot.dwis.business.application.certificate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.ExcelParameter;
import tech.hciot.dwis.business.domain.model.Certificate;
import tech.hciot.dwis.business.infrastructure.ChineseNumberUtil;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;

@Service
@Slf4j
public abstract class CertificateExportService implements ApplicationListener<ContextRefreshedEvent>  {

  private final Map<String, CertficateExporter> certficateMap = new HashMap<>();

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    event.getApplicationContext().getBeansOfType(CertficateExporter.class).forEach((name, instance) ->
      certficateMap.put(instance.certficateName(), instance));
  }

  public void export(String certficateCode, CertificateInfo certificateInfo, HttpServletResponse response) {
    CertficateExporter service = certficateMap.get(certficateCode);
    service.export(certificateInfo, response);
  }

  public void export(CertificateInfo certificateInfo, HttpServletResponse response) {
    ClassPathResource resource = new ClassPathResource("static/certificate/" + certificateFileName());
    try (InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream); ) {
      generateWorkSheet(workbook, certificateInfo);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName());
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 合格证模板名
  protected abstract String certificateFileName();

  // 内容部分分成几列
  protected int certContentColumnSum(int sheetId) {
    return 1;
  }

  // 导出文件名
  private String exportFileName() {
    String exportFileName = certificateFileName();
    String datePostfix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    return  exportFileName.replace(".xlsx", "-" + datePostfix + ".xlsx");
  }

  private void generateWorkSheet(Workbook workbook, CertificateInfo certificateInfo) {
//    certificateInfo = testData(); // TODO 测试数据
    for (int i = 0; i < workbook.getNumberOfSheets(); i ++) {
      log.info("generate sheet: {}", workbook.getSheetName(i));
      ExcelParameter excelParameter = ExcelUtil.extractExcelParameter(workbook.getSheetAt(i));
      generateCertificate(workbook.getSheetAt(i), certificateInfo, excelParameter, i);
    }
  }

  // 产品质量证明书
  protected void generateCertificate(Sheet sheet, CertificateInfo certificateInfo, ExcelParameter excelParameter, int sheetId) {
    log.info("start generateCertificate");
    CerticatePageGenerator certicatePageGenerator = excelParameter.getPagingType() == ExcelParameter.PAGING_TYPE_ALL_SAME
      ? new CerticateAllSamePageGenerator() : new CerticateFirstPageGenerator();

    int pageSum = certicatePageGenerator.computeCertPageSum(certificateInfo.getCertificateList().size(),
      excelParameter, certContentColumnSum(sheetId));
    log.info("total page: {}", pageSum);
    certicatePageGenerator.copySheetModel(sheet, pageSum, excelParameter);
    try {
      replaceSheetParameter(sheet, certificateInfo, excelParameter, pageSum);
      List<String> certLineList = generateAllCertLine(certificateInfo.getCertificateList(), sheetId);
      List<String> excelTextList = certicatePageGenerator.generateCertTextList(certLineList, excelParameter,
        pageSum, certContentColumnSum(sheetId));
      certicatePageGenerator.replaceSheetData(sheet, excelParameter, sheetId, pageSum, excelTextList);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    sheet.protectSheet("dwis");
    log.info("finish generateCertificate");
  }

  // 替换sheet里的宏变量
  private void replaceSheetParameter(Sheet sheet,
                                     CertificateInfo certificateInfo,
                                     ExcelParameter excelParameter,
                                     int pageSum) {
    for (int r = 0, rowNum = sheet.getLastRowNum(); r <= rowNum; r++) {
      Row row = sheet.getRow(r);
      if (row == null) {
        continue;
      }
      for (int c = 0, columnNum = row.getLastCellNum(); c <= columnNum; c++) {
        Cell cell = row.getCell(c);
        if (cell == null) {
          continue;
        }
        updateCellValue(cell, certificateInfo, pageSum, excelParameter);
      }
    }
  }

  // 更新单元格的宏变量
  protected void updateCellValue(Cell cell, CertificateInfo certificateInfo, int pageSum, ExcelParameter excelParameter) {
    String cellValue = ExcelUtil.getCellStringValue(cell);
    if (cellValue == null || !cellValue.startsWith("$")) {
      return;
    }
    if ("$titleCH".equals(cellValue)) { // 中文标题
      cell.setCellValue(certificateInfo.getDesign() + "整体铸钢车轮质量合格证");

    } else if ("$titleEN".equals(cellValue)) { // 英文标题
      cell.setCellValue("Quality Certificate of " + certificateInfo.getDesign() + " Cast steel wheel");

    } else if ("$customerName".equals(cellValue)) { // 收货单位
      cell.setCellValue(certificateInfo.getCustomerName());

    } else if ("$shippedNo".equals(cellValue)) { // 证书编号
      cell.setCellValue(certificateInfo.getShippedNo());

    } else if ("$shippedNo1".equals(cellValue)) { // 证书编号1
      String shippedNo1 = certificateInfo.getShippedNo().substring(0, 2)
        + "-0" + certificateInfo.getShippedNo().substring(2);
      cell.setCellValue(shippedNo1);

    } else if ("$shippedDate".equals(cellValue)) { // yyyy/M/d格式的发运日期
      cell.setCellValue(StandardTimeUtil.shippedDate(certificateInfo.getShippedDate()));

    } else if ("$shippedDateCH".equals(cellValue)) { // yyyy年M月d日格式的发运日期
      cell.setCellValue(StandardTimeUtil.shippedDateCH(certificateInfo.getShippedDate()));

    } else if ("$design".equals(cellValue)) { // 规格型号
      cell.setCellValue(certificateInfo.getDesign());

    } else if ("$sum".equals(cellValue)) { // 数量
      cell.setCellValue(certificateInfo.getCertificateList().size());

    } else if ("$sumCH".equals(cellValue)) { // 大写数量
      cell.setCellValue(ChineseNumberUtil.cvt(certificateInfo.getCertificateList().size()) + "片");

    } else if ("$trainNo".equals(cellValue)) { // 装运车号
      cell.setCellValue(certificateInfo.getTrainNo());

    } else if ("$c".equals(cellValue)) { // 车轮钢级别
      cell.setCellValue(certificateInfo.getSteelClass());

    } else if ("$spec".equals(cellValue)) { // 执行标准
      cell.setCellValue(certificateInfo.getSpec());

    } else if ("$completionNo".equals(cellValue)) { // 竣工移交记录编号
      String completionNo = certificateInfo.getTransferRecordNo() + certificateInfo.getShippedNo().substring(0, 2)
        + "-0" + certificateInfo.getShippedNo().substring(2);
      cell.setCellValue(completionNo);

    } else if ("$conNo".equals(cellValue)) { // 执行合同号
      cell.setCellValue(certificateInfo.getContractNo());

    } else if ("$certNo".equals(cellValue)) { // 合格证编号
      String certNo = certificateInfo.getTransferRecordNo() + certificateInfo.getShippedNo().substring(0, 2)
        + "-0" + certificateInfo.getShippedNo().substring(2);
      cell.setCellValue(certNo);

    } else if ("$titleAndCertNo".equals(cellValue)) { // 带标题的合格证编号（编号：xxxxx）
      String certNo = certificateInfo.getTransferRecordNo() + certificateInfo.getShippedNo().substring(0, 2)
        + "-0" + certificateInfo.getShippedNo().substring(2);
      cell.setCellValue("编号：" + certNo);

    } else if ("$drawingNo".equals(cellValue)) { // 图号
      cell.setCellValue(certificateInfo.getDrawingNo());

    } else if ("$approbationNo".equals(cellValue)) { // 生产资质证书号
      cell.setCellValue(certificateInfo.getApprobationNo());

    } else if ("$page".equals(cellValue)) { // 页数
      int page = computeCurrentPage(cell.getAddress().getRow(), excelParameter);
      cell.setCellValue("Page " + page + " of " + pageSum);
    }
  }

  private int computeCurrentPage(int row, ExcelParameter excelParameter) {
    if (excelParameter.getPagingType() == ExcelParameter.PAGING_TYPE_ALL_SAME) {
      return row / excelParameter.getTotalRowSum() + 1;
    }
    if (row + 1 <= excelParameter.getTotalRowSum()) {
      return 1;
    } else {
      return (row - excelParameter.getTotalRowSum()) / excelParameter.getOtherPageTotalRowSum() + 2;
    }
  }

  private List<String> generateAllCertLine(List<Certificate> printDataList, int sheetId) {
    List<String> certLineList = new ArrayList<>();
    printDataList.forEach(certificate -> {
      certLineList.add(generateCertLine(certificate, sheetId));
    });
    return certLineList;
  }

  // 根据sheet编号生成合格证每行数据
  protected String generateCertLine(Certificate data, int sheetId) {
    return generateCertLine(data);
  }

  protected abstract String generateCertLine(Certificate data);

  // BigDecimal的double数值转换成字符串格式，并在后面添加空格
  public String getDoubleValue(BigDecimal value, int length, int space) {
    double doubleValue = value == null ? 0 : value.doubleValue();
    String strValue = String.format("%3." + length + "f", doubleValue);
    return String.format("%-" + (strValue.length() + space)  + "s", strValue);
  }

  protected BigDecimal computeCrMoNi(Certificate certificate) {
    return (certificate.getCr()).add(certificate.getMo()).add(certificate.getNi());
  }

  protected CertificateInfo testData() {
    CertificateInfo certificateInfo = CertificateInfo.builder().build();
    List<Certificate> certificateList = new ArrayList<>();
    for (int i = 0; i < 9; i ++) {
      for (int j = 0; j < 40; j ++) {
        Certificate certificate = Certificate.builder()
          .customerName("中国铁路上海局集团有限公司杭州南车辆段上海轮轴车间")
          .transferRecordNo("042JLJ-004-002-02")
          .checkCode("21028")
          .shippedNo("210035")
          .design("840HEZD-C")
          .steelClass("B")
          .spec("IRS=R=19/93PART3")
          .trainNo("1587630")
          .shippedDate("2021-06-18")
          .ladleRecordKey("4/26/2020_1_603_4")
          .wheelSerial("210800" + i + String.format("%03d", j))
          .tapeSize(new BigDecimal(843.0))
          .brinnelReading(333)
          .batchNo("23")
          .approbationNo("CRCC10216P10655R1M")
          .drawingNo("JV33-11A")
          .balanceS("E3")
          .shelfNumber("1133445")
          .wheelW(133)
          .c(new BigDecimal(0.088))
          .mn(new BigDecimal(0.088))
          .p(new BigDecimal(0.088))
          .s(new BigDecimal(0.088))
          .si(new BigDecimal(0.088))
          .cr(new BigDecimal(0.088))
          .ni(new BigDecimal(0.088))
          .mo(new BigDecimal(0.088))
          .cu(new BigDecimal(0.088))
          .nb(new BigDecimal(0.088))
          .v(new BigDecimal(0.088))
          .ti(new BigDecimal(0.088))
          .al(new BigDecimal(0.088))
          .h(new BigDecimal(0.088))
          .build();
        certificateList.add(certificate);
      }
    }
    BeanUtil.copyProperties(certificateList.get(0), certificateInfo,
      CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    certificateInfo.setCertificateList(certificateList);
    certificateInfo.setContractName("9999999 840HDZD-C 中国铁路上海局集团有限公司杭州南车辆段上海轮轴车间 500");
    return certificateInfo;
  }

  public static void main(String[] args) {
    BigDecimal value = new BigDecimal(0.1);
    int length = 3;
    int space = 3;
    String strValue = String.format("%3." + length + "f", value.doubleValue());
    String finalValue = String.format("%-" + (strValue.length() + space) + "s", strValue);
    System.out.println("[" + finalValue + "]");

    System.out.println("str:" + String.format("%-10s", "xxx") + ".");

  }
}
