package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.business.infrastructure.AccessUtil.connectAccessDB;
import static tech.hciot.dwis.business.infrastructure.AccessUtil.copyBlankMdbFile;
import static tech.hciot.dwis.business.infrastructure.DownloadUtil.download;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCell;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCellWithScale;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.FILE_EXPORT_FAILED;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.NO_CRC_RECORD;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;

@Service
@Slf4j
public class PrintFileService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void export(String shippedNo, String code, HttpServletResponse response) {
    switch (code) {
      case "EF1":
        exportAcceptance(shippedNo, response);
        break;
      case "EF2":
        exportHudong(shippedNo, response);
        break;
      case "EF3":
        exportInternal(shippedNo, response);
        break;
      case "EF4":
        exportCrc(shippedNo, response);
        break;
      case "EF5":
        exportXining(shippedNo, response);
        break;
    }
  }

  private void exportCrc(String shippedNo, HttpServletResponse response) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT train_no.shipped_no AS 合格证号, CONVERT(VARCHAR(100), shipped_date, 112) AS 发运日期,"
        + "customer.train_code AS 铁路代号, Count(wheel_record.wheel_serial) AS 轮数 "
        + "FROM wheel_record INNER JOIN train_no ON wheel_record.shipped_no = train_no.shipped_no "
        + "INNER JOIN customer ON train_no.customer_id = customer.customer_id "
        + "GROUP BY train_no.shipped_no, CONVERT(VARCHAR(100), shipped_date, 112),customer.train_code "
        + "HAVING train_no.shipped_no = :shippedNo";
    params.put("shippedNo", shippedNo);
    List<Map<String, Object>> list = template.queryForList(sql, params);
    if (list.size() == 1) {
      Map<String, Object> data = list.get(0);
      String trainCode = data.get("铁路代号") == null ? null : data.get("铁路代号").toString();
      String date = data.get("发运日期").toString();
      String count = data.get("轮数").toString();
      sql = "SELECT wheel_serial AS idNumber,'DCACC' AS C103,format(last_barcode, 'yyyy-MM-dd\\Thh:mm:ss') AS C104,"
          + "LEFT(wheel_serial,2) AS C105,SUBSTRING(wheel_serial,3,2) AS C106,'CO' AS C107,design.steel_class AS C108,"
          + "wheel_record.wheel_w AS C109,SUBSTRING(wheel_serial,5,LEN(wheel_serial)) AS C111,"
          + "CONCAT(SUBSTRING(CONVERT(varchar(100), heat_record.cast_date, 23),3,2),heat_record.furnace_no,RIGHT('0000'+ CONVERT(VARCHAR(50),heat_record.heat_seq),4) ,ladle_record.ladle_seq) AS C112,"
          + "'CP' AS C115,wheel_record.tape_size AS C116,LEFT(CONVERT(varchar(100), heat_record.cast_date, 23), 4) AS C121,'0' AS C122,"
          + "SUBSTRING(wheel_record.design,4,LEN(wheel_record.design)) AS C123,LEFT(CONVERT(varchar(100), heat_record.cast_date, 23), 10) AS C126,"
          + "'01' AS C130,'M' AS C131,wheel_record.bore_size AS C133,'' AS C134,'' AS C135 "
          + "FROM wheel_record JOIN design ON wheel_record.design = design.design JOIN ladle_record ON wheel_record"
          + ".ladle_id = ladle_record.id JOIN heat_record ON ladle_record.heat_record_id = Heat_Record.id "
          + "WHERE wheel_record.shipped_no = :shippedNo";
      List<Map<String, Object>> dateList = template.queryForList(sql, params);
      Workbook workbook = null;
      try {
        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("国内");

        String fileName = "CO_" + trainCode + "_" + date + "_" + shippedNo + "_" + count + ".xlsx";

        String[] headers = {
            "idNumber",
            "C103",
            "C104",
            "C105",
            "C106",
            "C107",
            "C108",
            "C109",
            "C111",
            "C112",
            "C115",
            "C116",
            "C122",
            "C123",
            "C126",
            "C130",
            "C131",
            "C133",
            "C134",
            "C135"
        };

        // 列宽数组
        Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

        // 标题
        ExcelUtil.createTitleRow(sheet, headers);

        // 内容
        int rowNum = 1;
        for (Map<String, Object> rowData : dateList) {
          Row row = sheet.createRow(rowNum);
          int columnNum = 0;
          createCell(row, columnNum++, rowData.get("idNumber"), lengthArray);
          createCell(row, columnNum++, rowData.get("C103"), lengthArray);
          createCell(row, columnNum++, rowData.get("C104"), lengthArray);
          createCell(row, columnNum++, rowData.get("C105"), lengthArray);
          createCell(row, columnNum++, rowData.get("C106"), lengthArray);
          createCell(row, columnNum++, rowData.get("C107"), lengthArray);
          createCell(row, columnNum++, rowData.get("C108"), lengthArray);
          createCell(row, columnNum++, rowData.get("C109"), lengthArray);
          createCell(row, columnNum++, rowData.get("C111"), lengthArray);
          createCell(row, columnNum++, rowData.get("C112"), lengthArray);
          createCell(row, columnNum++, rowData.get("C115"), lengthArray);
          createCellWithScale(row, columnNum++, rowData.get("C116"), lengthArray, 1);
          createCell(row, columnNum++, rowData.get("C122"), lengthArray);
          createCell(row, columnNum++, rowData.get("C123"), lengthArray);
          createCell(row, columnNum++, rowData.get("C126"), lengthArray);
          createCell(row, columnNum++, rowData.get("C130"), lengthArray);
          createCell(row, columnNum++, rowData.get("C131"), lengthArray);
          createCell(row, columnNum++, rowData.get("C133"), lengthArray);
          createCell(row, columnNum++, rowData.get("C134"), lengthArray);
          createCell(row, columnNum++, rowData.get("C135"), lengthArray);
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
        throw FILE_EXPORT_FAILED.getPlatformException();
      } finally {
        if (workbook != null) {
          try {
            workbook.close();
          } catch (IOException e) {
          }
        }
      }
    } else {
      throw NO_CRC_RECORD.getPlatformException();
    }
  }

  private void exportInternal(String shippedNo, HttpServletResponse response) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT wheel_record.wheel_serial AS 轮号,wheel_record.design AS 轮型, pour_record.cast_date AS 铸造日期,"
            + "ladle_record.ladle_record_key AS 炉次,wheel_record.shipped_no AS 合格证编号,wheel_record.wheel_w AS 轮辋,"
            + "wheel_record.tape_size AS 带尺,wheel_record.bore_size AS 轴孔 "
            + "FROM wheel_record JOIN pour_record ON wheel_record.wheel_serial = pour_record.wheel_serial "
            + "JOIN ladle_record on ladle_record.id = wheel_record.ladle_id "
            + "WHERE wheel_record.shipped_no= :shippedNo "
            + "ORDER BY wheel_record.wheel_serial";
    params.put("shippedNo", shippedNo);
    List<Map<String, Object>> list = template.queryForList(sql, params);
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet("国内");

      String fileName = "DCACC_HGZ_" + shippedNo + ".xlsx";

      String[] headers = {
          "轮号",
          "轮型",
          "铸造日期",
          "炉次",
          "合格证编号",
          "轮辋",
          "带尺",
          "轴孔"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      Row titleRow = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) {
        Cell cell = titleRow.createCell(i);
        XSSFRichTextString text = new XSSFRichTextString(headers[i]);
        cell.setCellValue(text);
      }

      // 内容
      int rowNum = 1;
      for (Map<String, Object> rowData : list) {
        Row row = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row, columnNum++, rowData.get("轮号"), lengthArray);
        createCell(row, columnNum++, rowData.get("轮型"), lengthArray);
        createCell(row, columnNum++, rowData.get("铸造日期"), lengthArray);
        createCell(row, columnNum++, rowData.get("炉次"), lengthArray);
        createCell(row, columnNum++, rowData.get("合格证编号"), lengthArray);
        createCell(row, columnNum++, rowData.get("轮辋"), lengthArray);
        createCellWithScale(row, columnNum++, rowData.get("带尺"), lengthArray, 1);
        createCell(row, columnNum++, rowData.get("轴孔"), lengthArray);
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
      throw FILE_EXPORT_FAILED.getPlatformException();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }

  private void exportHudong(String shippedNo, HttpServletResponse response) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT LEFT(wheel_record.wheel_serial,4) AS 制造年月,'CO' AS 工厂代号,design.steel_class AS 车轮等级,'Z' AS 钢种代号,"
            + "SUBSTRING(wheel_record.design,4,LEN(wheel_record.Design)) AS 轮型,"
            + "CONCAT(SUBSTRING(CONVERT(varchar(100), heat_record.cast_date, 23),3,4),heat_record.furnace_no, heat_record.heat_seq ,ladle_record.ladle_seq) AS 炉炼号,"
            + "'铸钢' AS 材质,wheel_record.tape_size AS 车轮直径,'' AS 轴型 "
            + "FROM wheel_record JOIN design ON wheel_record.design = design.design JOIN ladle_record ON wheel_record.ladle_id "
            + " = ladle_record.id JOIN heat_record ON ladle_record.heat_record_id = heat_record.id WHERE "
            + "wheel_record.shipped_no= :shippedNo ";
    params.put("shippedNo", shippedNo);
    List<Map<String, Object>> list = template.queryForList(sql, params);
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet("湖东");

      String fileName = "DCACC_HGZ_" + shippedNo + ".xlsx";

      String[] headers = {
          "制造年月",
          "工厂代号",
          "车轮等级",
          "钢种代号",
          "轮型",
          "炉炼号",
          "材质",
          "车轮直径",
          "轴型"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      Row titleRow = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) {
        Cell cell = titleRow.createCell(i);
        XSSFRichTextString text = new XSSFRichTextString(headers[i]);
        cell.setCellValue(text);
      }

      // 内容
      int rowNum = 1;
      for (Map<String, Object> rowData : list) {
        Row row = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row, columnNum++, rowData.get("制造年月"), lengthArray);
        createCell(row, columnNum++, rowData.get("工厂代号"), lengthArray);
        createCell(row, columnNum++, rowData.get("车轮等级"), lengthArray);
        createCell(row, columnNum++, rowData.get("钢种代号"), lengthArray);
        createCell(row, columnNum++, rowData.get("轮型"), lengthArray);
        createCell(row, columnNum++, rowData.get("炉炼号"), lengthArray);
        createCell(row, columnNum++, rowData.get("材质"), lengthArray);
        createCellWithScale(row, columnNum++, rowData.get("车轮直径"), lengthArray, 1);
        createCell(row, columnNum++, rowData.get("轴型"), lengthArray);
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
      throw FILE_EXPORT_FAILED.getPlatformException();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }

  private void exportXining(String shippedNo, HttpServletResponse response) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
            "SELECT LEFT(wheel_record.wheel_serial,4) AS 制造年月,'CO' AS 工厂代号,design.steel_class AS 车轮等级,'Z' AS 钢种代号,"
                    + "SUBSTRING(wheel_record.design,4,4) AS 轮型,"
                    + "SUBSTRING(wheel_record.wheel_serial,5,6) AS 轮号 "
                    + "FROM wheel_record INNER JOIN design ON wheel_record.design = design.design "
                    + "WHERE wheel_record.shipped_no= :shippedNo ";
    params.put("shippedNo", shippedNo);
    List<Map<String, Object>> list = template.queryForList(sql, params);
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet("西宁");

      String fileName = "DCACC_HGZ_" + shippedNo + ".xlsx";

      String[] headers = {
              "制造年月",
              "工厂代号",
              "车轮等级",
              "钢种代号",
              "轮型",
              "轮号"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      Row titleRow = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) {
        Cell cell = titleRow.createCell(i);
        XSSFRichTextString text = new XSSFRichTextString(headers[i]);
        cell.setCellValue(text);
      }

      // 内容
      int rowNum = 1;
      for (Map<String, Object> rowData : list) {
        Row row = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row, columnNum++, rowData.get("制造年月"), lengthArray);
        createCell(row, columnNum++, rowData.get("工厂代号"), lengthArray);
        createCell(row, columnNum++, rowData.get("车轮等级"), lengthArray);
        createCell(row, columnNum++, rowData.get("钢种代号"), lengthArray);
        createCell(row, columnNum++, rowData.get("轮型"), lengthArray);
        createCell(row, columnNum++, rowData.get("轮号"), lengthArray);
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
      throw FILE_EXPORT_FAILED.getPlatformException();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }

  @SneakyThrows
  private void exportAcceptance(String shippedNo, HttpServletResponse response) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT wheel_serial,SUBSTRING(mec_serial,1,LEN(mec_serial)-CHARINDEX('-',REVERSE(mec_serial))) AS mec_serial FROM wheel_record WHERE shipped_no = :shippedNo";
    params.put("shippedNo", shippedNo);
    List<Map<String, Object>> list = template.queryForList(sql, params);
    String tempFileName = RandomStringUtils.randomAlphabetic(16) + ".mdb";
    copyBlankMdbFile(tempFileName);
    File file = new File(tempFileName);
    @Cleanup Connection connection = connectAccessDB(file.getAbsolutePath());
    String tableName = createTable(connection, shippedNo);
    insertTable(connection, list, tableName);
    download(response, file);
    file.delete();
  }

  private String createTable(Connection connection, String shippedNo) throws Exception {
    String tableName = "HGZ" + shippedNo;
    String sql = "create table " + tableName + " (Wheel_Serial Text,Mec_Serial Text);";
    @Cleanup Statement statement = connection.createStatement();
    statement.execute(sql);
    return tableName;
  }

  @SneakyThrows
  private void insertTable(Connection connection, List<Map<String, Object>> wheelSerialList, String tableName) {
    @Cleanup Statement statement = connection.createStatement();
    wheelSerialList.forEach(row -> {
      StringBuffer sb = new StringBuffer();
      sb.append("insert into ").append(tableName).append(" values ('").append(row.get("wheel_serial")).append("','")
          .append(row.get("mec_serial")).append("');");
      log.info("insert sql is {}", sb);
      try {
        statement.addBatch(sb.toString());
      } catch (SQLException e) {
        log.error(e.getMessage());
      }
    });
    statement.executeBatch();
  }
}
