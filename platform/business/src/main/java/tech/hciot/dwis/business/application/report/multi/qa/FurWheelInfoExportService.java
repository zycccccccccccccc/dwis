package tech.hciot.dwis.business.application.report.multi.qa;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.application.report.ReportSqlService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.FurWheelInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FurWheelInfoExportService {

  private static final String REPORT_NAME = "composite-report/qa-stat";
  private static final String TEMPLATE_NAME = "3.11-fur-wheel-info.xlsx";

  @Autowired
  private ReportSqlService reportSqlService;

  // 3.11 综合查询业务-质量统计-整炉次车轮信息
  public void export(String wheelSerial,
                     String castDate,
                     Integer tapSeq,
                     HttpServletResponse response) {
    Map<String, Object> params = new HashMap<>();
    String sqlName;
    if (wheelSerial != null) {  //根据某个轮号查询
      params.put("wheelSerial", wheelSerial);
      sqlName = "3.11-fur-wheel-info-serial";
    } else {  //根据浇注日期&&出钢号查询
      params.put("castDate", castDate);
      params.put("tapSeq", tapSeq);
      sqlName = "3.11-fur-wheel-info-datetap";
    }
    List<FurWheelInfo> furWheelInfoList = reportSqlService.queryResultList(REPORT_NAME, sqlName, params, FurWheelInfo.class);

    if (furWheelInfoList.isEmpty()) {
      throw PlatformException.badRequestException("查询结果为空，请仔细查看参数是否正确！");
    } else {
      ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
      try (InputStream inputStream = resource.getInputStream();
           Workbook workbook = new XSSFWorkbook(inputStream); ) {

        Sheet sheet = workbook.getSheetAt(0);

        //标题
        String title = furWheelInfoList.get(0).getHeatRecordKey() + "（第" + furWheelInfoList.get(0).getTapSeq() + "炉）" + "车轮信息统计";
        sheet.getRow(0).getCell(0).setCellValue(title);

        // 日期
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String currentDate = "查询日期时间：" + format.format(new Date());
        sheet.getRow(1).getCell(0).setCellValue(currentDate);

        List<Map.Entry<Integer, List<FurWheelInfo>>> ladRes = furWheelInfoList.stream().collect(Collectors.groupingBy(FurWheelInfo::getLadleSeq,Collectors.toList()))
                .entrySet().stream().collect(Collectors.toList());

        // 数据
        int currentRow = 4;
        for (int i = 0; i < ladRes.size(); i++) {
          int startRow = currentRow;
          for (int j = 0; j < ladRes.get(i).getValue().size(); j++) {
            FurWheelInfo result = ladRes.get(i).getValue().get(j);
            ExcelUtil.copyCell(sheet, 3, 0,
                    sheet, currentRow, 0,
                    1, 15);
            sheet.getRow(currentRow).getCell(0).setCellValue(String.valueOf(currentRow - 3));
            sheet.getRow(currentRow).getCell(1).setCellValue(String.valueOf(result.getLadleSeq()));
            sheet.getRow(currentRow).getCell(2).setCellValue(result.getWheelSerial());
            sheet.getRow(currentRow).getCell(3).setCellValue(result.getHeatCode());
            sheet.getRow(currentRow).getCell(4).setCellValue(result.getTestCode());
            sheet.getRow(currentRow).getCell(5).setCellValue(result.getReworkCode());
            sheet.getRow(currentRow).getCell(6).setCellValue(result.getScrapCode());
            sheet.getRow(currentRow).getCell(7).setCellValue(result.getIsConfirmScrap());
            sheet.getRow(currentRow).getCell(8).setCellValue(result.getScrapDate() == null ? "" : result.getScrapDate().toString());
            sheet.getRow(currentRow).getCell(9).setCellValue(result.getIsFinished());
            sheet.getRow(currentRow).getCell(10).setCellValue(result.getCheckCode());
            sheet.getRow(currentRow).getCell(11).setCellValue(result.getStockDate() == null ? "" : result.getStockDate().toString());
            sheet.getRow(currentRow).getCell(12).setCellValue(result.getShippedNo());
            sheet.getRow(currentRow).getCell(13).setCellValue(result.getShippedDate() == null ? "" : result.getShippedDate().toString());
            sheet.getRow(currentRow).getCell(14).setCellValue(result.getCustomerName());
            currentRow++;
          }
          CellRangeAddress region = new CellRangeAddress(startRow, startRow + ladRes.get(i).getValue().size() - 1, 1, 1);
          sheet.addMergedRegion(region);
        }

        sheet.shiftRows(4, sheet.getLastRowNum(), -1);

        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
        response.flushBuffer();

        workbook.write(response.getOutputStream());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
