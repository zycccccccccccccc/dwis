package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseTime;
import static tech.hciot.dwis.business.infrastructure.AccessUtil.connectAccessDB;
import static tech.hciot.dwis.business.infrastructure.AccessUtil.copyBlankMdbFile;
import static tech.hciot.dwis.business.infrastructure.DownloadUtil.download;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CHECK_DATA_EXPORT_NO_DATA;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.interfaces.dto.CheckWheelRequest;
import tech.hciot.dwis.business.interfaces.dto.CheckWheelResponse;
import tech.hciot.dwis.business.interfaces.dto.CheckMecserialRequest;
import tech.hciot.dwis.business.interfaces.dto.CheckMecserialResponse;
import tech.hciot.dwis.business.interfaces.dto.StockData;
import tech.hciot.dwis.business.interfaces.dto.StockProduct;
import tech.hciot.dwis.business.interfaces.dto.WheelStockData;

@Service
@Slf4j
public class StockService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public StockData stock(String startDate, String endDate, String listNo, String listDate, String balanceFlag, String design,
      Integer boreSize, String tapeSize, Integer wheelW) {
    StockData stockData = getStockData(listNo, listDate, design);
    getStockProducts(stockData, design, boreSize, tapeSize == null ? null : new BigDecimal(tapeSize), wheelW,
        parseTime(startDate), parseTime(endDate), balanceFlag);
    int sum = stockData.getProducts().stream().mapToInt(StockProduct::getSum).sum();
    stockData.setAmount(sum);
    stockData.setDate(parseDate(listDate));
    return stockData;
  }

  public StockData bigTape(String startDate, String endDate, String listNo, String listDate, String balanceFlag, String design,
      Integer boreSize, Integer wheelW) {
    StockData stockData = getStockData(listNo, listDate, design);
    getBigTapeStockProducts(stockData, design, boreSize, wheelW, parseTime(startDate), parseTime(endDate), balanceFlag);
    int sum = stockData.getProducts().stream().mapToInt(StockProduct::getSum).sum();
    stockData.setAmount(sum);
    stockData.setDate(parseDate(listDate));
    return stockData;
  }


  private StockData getStockData(String listNo, String listDate, String design) {
    StockData stockData = new StockData();
    Date listD = parseDate(listDate);
    stockData.setIndex("CO-" + DateUtils.toCalendar(listD).get(Calendar.YEAR) + "-" + listNo);
    stockData.setSpec(StringUtils.defaultString(design, "*"));
    return stockData;
  }

  private void getStockProducts(StockData stockData, String design, Integer boreSize, BigDecimal tapeSize, Integer wheelW,
      Date start, Date end, String balanceS) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT ISNULL(wheel_record.bore_size, 0) AS bore_size, wheel_record.wheel_serial AS wheel_serial,"
            + "ISNULL(wheel_record.tape_size, 0) as tape_size,"
            + "ISNULL(wheel_record.wheel_w, 0) AS wheel_w,CASE WHEN balance_s IS NULL THEN 'Not' ELSE balance_s END AS e3,design.drawing_no"
            + " AS drawing_no FROM wheel_record INNER JOIN design ON wheel_record.design = design.design WHERE"
            + " wheel_record.finished = 1 AND wheel_record.stock_date IS NULL AND wheel_record.shipped_no IS NULL AND"
            + " wheel_record.confirmed_scrap = 0";
    if (StringUtils.isNotBlank(design)) {
      sql += " AND wheel_record.design = :design";
      params.put("design", design);
    }
    if (boreSize != null) {
      sql += " AND wheel_record.bore_size = :boreSize";
      params.put("boreSize", boreSize);
    }
    if (tapeSize != null) {
      sql += " AND wheel_record.tape_size = :tapeSize";
      params.put("tapeSize", tapeSize);
    }
    if (wheelW != null) {
      sql += " AND wheel_record.wheel_w = :wheelW";
      params.put("wheelW", wheelW);
    }
    if (start != null) {
      sql += " AND wheel_record.last_barcode >= :start";
      params.put("start", start);
    }
    if (end != null) {
      sql += " AND wheel_record.last_barcode <= :end";
      params.put("end", end);
    }
    if (balanceS != null) {
      sql += " AND CASE WHEN balance_s IS NULL THEN 'Not' ELSE balance_s END = :balanceS";
      params.put("balanceS", balanceS);
    }
    sql += " ORDER BY bore_size,tape_size,wheel_w,e3,wheel_record.wheel_serial";

    List<Map<String, Object>> list = template.queryForList(sql, params);
    List<StockProduct> stockProducts = new ArrayList<>();
    list.forEach(l -> {
          if (StringUtils.isBlank(stockData.getDrawingNo())) {
            stockData.setDrawingNo(l.get("drawing_no") == null ? "" : l.get("drawing_no").toString());
          }
          if (stockProducts.size() == 0) {
            newStockProduct(stockProducts, l);
          } else {
            StockProduct stockProduct = stockProducts.get(stockProducts.size() - 1);
            int boreSizeNow = ((Number) l.get("bore_size")).intValue();
            BigDecimal tapeSizeNow = (BigDecimal) l.get("tape_size");
            int wheelWNow = ((Number) l.get("wheel_w")).intValue();
            String e3Now = l.get("e3").toString();
            if (stockProduct.getBoreSize() == boreSizeNow && stockProduct.getTapeSize().equals(tapeSizeNow)
                && stockProduct.getWheelW() == wheelWNow && stockProduct.getE3().equals(e3Now)) {
              List<String> code = stockProduct.getProductCodeList();
              code.add(l.get("wheel_serial").toString());
              stockProduct.setSum(stockProduct.getSum() == null ? 1 : stockProduct.getSum() + 1);
            } else {
              newStockProduct(stockProducts, l);
            }
          }
        }
    );
    stockData.setProducts(stockProducts);
  }

  private void newStockProduct(List<StockProduct> stockProducts, Map<String, Object> row) {
    StockProduct stockProduct = new StockProduct();
    stockProduct.setBoreSize(((Number) row.get("bore_size")).intValue());
    stockProduct.setTapeSize((BigDecimal) row.get("tape_size"));
    stockProduct.setWheelW(((Number) row.get("wheel_w")).intValue());
    stockProduct.setE3(row.get("e3").toString());
    List<String> code = new ArrayList<>();
    code.add(row.get("wheel_serial").toString());
    stockProduct.setProductCodeList(code);
    stockProduct.setSum(stockProduct.getSum() == null ? 1 : stockProduct.getSum() + 1);
    stockProducts.add(stockProduct);
  }

  private void getBigTapeStockProducts(StockData stockData, String design, Integer boreSize, Integer wheelW,
      Date start, Date end, String balanceS) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT wheel_record.bore_size AS bore_size,wheel_record.wheel_serial AS wheel_serial,wheel_record.tape_size as tape_size,"
            + "wheel_record.wheel_w AS wheel_w,CASE WHEN balance_s is NULL THEN 'Not' ELSE balance_s END AS e3,design.drawing_no"
            + " AS drawing_no FROM wheel_record INNER JOIN design ON wheel_record.design = design.design WHERE"
            + " wheel_record.finished = 1 AND wheel_record.stock_date IS NULL AND wheel_record.shipped_no IS NULL AND"
            + " wheel_record.confirmed_scrap = 0 AND wheel_record.tape_size >= 844.0";
    if (StringUtils.isNotBlank(design)) {
      sql += " AND wheel_record.design = :design";
      params.put("design", design);
    }
    if (boreSize != null) {
      sql += " AND wheel_record.bore_size = :boreSize";
      params.put("boreSize", boreSize);
    }
    if (wheelW != null) {
      sql += " AND wheel_record.wheel_w = :wheelW";
      params.put("wheelW", wheelW);
    }
    if (start != null) {
      sql += " AND wheel_record.last_barcode >= :start";
      params.put("start", start);
    }
    if (end != null) {
      sql += " AND wheel_record.last_barcode <= :end";
      params.put("end", end);
    }
    if (balanceS != null) {
      sql += " AND CASE WHEN balance_s IS NULL THEN 'Not' ELSE balance_s END = :balanceS";
      params.put("balanceS", balanceS);
    }
    sql += " ORDER BY bore_size,tape_size,wheel_w,e3,wheel_record.wheel_serial";

    List<Map<String, Object>> list = template.queryForList(sql, params);
    List<StockProduct> stockProducts = new ArrayList<>();
    list.forEach(l -> {
          if (StringUtils.isBlank(stockData.getDrawingNo())) {
            stockData.setDrawingNo(l.get("drawing_no") == null ? "" : l.get("drawing_no").toString());
          }
          if (stockProducts.size() == 0) {
            newStockProduct(stockProducts, l);
          } else {
            StockProduct stockProduct = stockProducts.get(stockProducts.size() - 1);
            int boreSizeNow = ((Number) l.get("bore_size")).intValue();
            BigDecimal tapeSizeNow = (BigDecimal) l.get("tape_size");
            int wheelWNow = ((Number) l.get("wheel_w")).intValue();
            String e3Now = l.get("e3").toString();
            if (stockProduct.getBoreSize() == boreSizeNow && stockProduct.getTapeSize().equals(tapeSizeNow)
                && stockProduct.getWheelW() == wheelWNow && stockProduct.getE3().equals(e3Now)) {
              List<String> code = stockProduct.getProductCodeList();
              code.add(l.get("wheel_serial").toString());
              stockProduct.setSum(stockProduct.getSum() == null ? 1 : stockProduct.getSum() + 1);
            } else {
              newStockProduct(stockProducts, l);
            }
          }
        }
    );
    stockData.setProducts(stockProducts);
  }

  public WheelStockData wheelList(String startDate, String endDate, String acceptanceNo, String stockDate, String balanceFlag, String design,
                                  Integer boreSize, String tapeSize, Integer wheelW) {
    WheelStockData wheelStockData = new WheelStockData();
    Date date = parseDate(stockDate);
    wheelStockData.setIndex(DateUtils.toCalendar(date).get(Calendar.YEAR) + "-" + acceptanceNo);
    wheelStockData.setBalanceFlag(StringUtils.defaultString(balanceFlag, "*"));
    wheelStockData.setDesign(StringUtils.defaultString(design, "*"));
    wheelStockData.setBoreSize(boreSize != null ? boreSize.toString() : "*");
    wheelStockData.setTapeSize(StringUtils.defaultString(tapeSize, "*"));
    wheelStockData.setWheelW(wheelW != null ? wheelW.toString() : "*");
    wheelStockData.setReportDate(new Date());
    wheelStockData.setDate(date);
    List<String> wheelSerialList = findWheelSerial(balanceFlag, design, boreSize, tapeSize, wheelW, parseTime(startDate), parseTime(endDate));
    wheelStockData.setWheels(wheelSerialList);
    wheelStockData.setAmount(wheelSerialList.size());
    return wheelStockData;
  }

  private List<String> findWheelSerial(String balanceFlag, String design, Integer boreSize, String tapeSize, Integer wheelW, Date start, Date end) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT design,wheel_serial FROM wheel_record WHERE finished = 1 AND stock_date IS NULL AND shipped_no IS NULL AND"
            + " confirmed_scrap = 0";
    if (StringUtils.isNotBlank(balanceFlag)) {
      sql += " AND CASE WHEN balance_s IS NULL THEN 'Not' ELSE balance_s END = :balanceFlag";
      params.put("balanceFlag", balanceFlag);
    }
    if (StringUtils.isNotBlank(design)) {
      sql += " AND design = :design";
      params.put("design", design);
    }
    if (boreSize != null) {
      sql += " AND bore_size = :boreSize";
      params.put("boreSize", boreSize);
    }
    if (StringUtils.isNotBlank(tapeSize)) {
      BigDecimal ts = new BigDecimal(tapeSize);
      sql += " AND tape_size = :tapeSize";
      params.put("tapeSize", ts);
    }
    if (wheelW != null) {
      sql += " AND wheel_w = :wheelW";
      params.put("wheelW", wheelW);
    }
    if (start != null) {
      sql += " AND last_barcode >= :start";
      params.put("start", start);
    }
    if (end != null) {
      sql += " AND last_barcode <= :end";
      params.put("end", end);
    }
    sql += " ORDER BY wheel_serial";

    List<Map<String, Object>> list = template.queryForList(sql, params);
    List<String> wheelSerialList = new ArrayList<>();
    list.forEach(l ->
        wheelSerialList.add(l.get("wheel_serial").toString()));
    return wheelSerialList;
  }

  public int in(String startDate, String endDate, String acceptanceNo, String stockDate, String balanceFlag, String design,
                Integer boreSize, String tapeSize, Integer wheelW) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "UPDATE wheel_record SET stock_date = :stockDate,check_code = :checkCode WHERE finished = 1 AND stock_date IS NULL AND "
            + "shipped_no IS NULL";
    params.put("stockDate", stockDate);
    params.put("checkCode", acceptanceNo);
    if (StringUtils.isNotBlank(balanceFlag)) {
      sql += " AND CASE WHEN balance_s IS NULL THEN 'Not' ELSE balance_s END = :balanceFlag";
      params.put("balanceFlag", balanceFlag);
    }
    if (StringUtils.isNotBlank(design)) {
      sql += " AND design = :design";
      params.put("design", design);
    }
    if (boreSize != null) {
      sql += " AND bore_size = :boreSize";
      params.put("boreSize", boreSize);
    }
    if (StringUtils.isNotBlank(tapeSize)) {
      BigDecimal ts = new BigDecimal(tapeSize);
      sql += " AND tape_size = :tapeSize";
      params.put("tapeSize", ts);
    }
    if (wheelW != null) {
      sql += " AND wheel_w = :wheelW";
      params.put("wheelW", wheelW);
    }
    Date start = parseTime(startDate);
    if (start != null) {
      sql += " AND last_barcode >= :start";
      params.put("start", start);
    }
    Date end = parseTime(endDate);
    if (end != null) {
      sql += " AND last_barcode <= :end";
      params.put("end", end);
    }
    return template.update(sql, params);
  }

  public int bigTapeStockIn(String startDate, String endDate, String acceptanceNo, String stockDate, String balanceFlag, String design,
                            Integer boreSize, Integer wheelW) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "UPDATE wheel_record SET stock_date = :stockDate,check_code = :checkCode WHERE finished = 1 AND stock_date IS NULL AND "
            + "shipped_no IS NULL AND tape_size >= 844.0";
    params.put("stockDate", stockDate);
    params.put("checkCode", acceptanceNo);
    if (StringUtils.isNotBlank(balanceFlag)) {
      sql += " AND CASE WHEN balance_s IS NULL THEN 'Not' ELSE balance_s END = :balanceFlag";
      params.put("balanceFlag", balanceFlag);
    }
    if (StringUtils.isNotBlank(design)) {
      sql += " AND design = :design";
      params.put("design", design);
    }
    if (boreSize != null) {
      sql += " AND bore_size = :boreSize";
      params.put("boreSize", boreSize);
    }
    if (wheelW != null) {
      sql += " AND wheel_w = :wheelW";
      params.put("wheelW", wheelW);
    }
    Date start = parseTime(startDate);
    if (start != null) {
      sql += " AND last_barcode >= :start";
      params.put("start", start);
    }
    Date end = parseTime(endDate);
    if (end != null) {
      sql += " AND last_barcode <= :end";
      params.put("end", end);
    }
    return template.update(sql, params);
  }

  public StockData checklist(String acceptanceNo, String listNo, String listDate,
      String design) {
    StockData stockData = getStockData(listNo, listDate, design);
    getCheckListProducts(stockData, acceptanceNo);
    int sum = stockData.getProducts().stream().mapToInt(StockProduct::getSum).sum();
    stockData.setAmount(sum);
    stockData.setDate(parseDate(listDate));
    return stockData;
  }

  private void getCheckListProducts(StockData stockData, String acceptanceNo) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT wheel_record.bore_size, wheel_record.wheel_serial, wheel_record.tape_size, wheel_record.wheel_w, "
        + "       CASE WHEN balance_s is NULL OR balance_s = '' THEN 'Not' ELSE balance_s END AS e3,design.drawing_no "
        + "  FROM wheel_record INNER JOIN design ON wheel_record.design = design.design "
        + " WHERE wheel_record.finished = 1 ";
    if (acceptanceNo != null) {
      sql += " AND wheel_record.check_code = :checkCode";
      params.put("checkCode", acceptanceNo);
    }
    sql += " ORDER BY bore_size,tape_size,wheel_w,e3,wheel_record.wheel_serial";

    List<Map<String, Object>> list = template.queryForList(sql, params);
    List<StockProduct> stockProducts = new ArrayList<>();
    list.forEach(l -> {
          if (StringUtils.isBlank(stockData.getDrawingNo())) {
            stockData.setDrawingNo(l.get("drawing_no") == null ? "" : l.get("drawing_no").toString());
          }
          if (stockProducts.size() == 0) {
            newStockProduct(stockProducts, l);
          } else {
            StockProduct stockProduct = stockProducts.get(stockProducts.size() - 1);
            int boreSizeNow = ((Number) l.get("bore_size")).intValue();
            BigDecimal tapeSizeNow = (BigDecimal) l.get("tape_size");
            int wheelWNow = ((Number) l.get("wheel_w")).intValue();
            String e3Now = l.get("e3").toString();
            if (stockProduct.getBoreSize() == boreSizeNow && stockProduct.getTapeSize().equals(tapeSizeNow)
                && stockProduct.getWheelW() == wheelWNow && stockProduct.getE3().equals(e3Now)) {
              List<String> code = stockProduct.getProductCodeList();
              code.add(l.get("wheel_serial").toString());
              stockProduct.setSum(stockProduct.getSum() == null ? 1 : stockProduct.getSum() + 1);
            } else {
              newStockProduct(stockProducts, l);
            }
          }
        }
    );
    stockData.setProducts(stockProducts);
  }

  @SneakyThrows
  public void check(String startDate, String endDate, String acceptanceNo, HttpServletResponse response) {
    String tempFileName = RandomStringUtils.randomAlphabetic(16) + ".mdb";
    copyBlankMdbFile(tempFileName);
    File file = new File(tempFileName);
    @Cleanup Connection connection = connectAccessDB(file.getAbsolutePath());
    String tableName = createTable(connection, acceptanceNo);
    insertTable(connection, findCheckData(parseTime(startDate), parseTime(endDate)), tableName);
    download(response, file);
    file.delete();
  }

  @SneakyThrows(value = SQLException.class)
  private void insertTable(Connection connection, List<Map<String, Object>> checkData, String tableName) {
    if (checkData.isEmpty()) {
      throw CHECK_DATA_EXPORT_NO_DATA.getPlatformException();
    }
    connection.setAutoCommit(false);
    @Cleanup PreparedStatement statement = connection
        .prepareStatement(
            "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    checkData.forEach(row -> {
      try {
        statement.setBigDecimal(1, (BigDecimal) row.get("c"));
        statement.setBigDecimal(2, (BigDecimal) row.get("si"));
        statement.setBigDecimal(3, (BigDecimal) row.get("mn"));
        statement.setBigDecimal(4, (BigDecimal) row.get("p"));
        statement.setBigDecimal(5, (BigDecimal) row.get("s"));
        statement.setBigDecimal(6, (BigDecimal) row.get("al"));
        statement.setBigDecimal(7, (BigDecimal) row.get("cr"));
        statement.setBigDecimal(8, (BigDecimal) row.get("cu"));
        statement.setBigDecimal(9, (BigDecimal) row.get("mo"));
        statement.setBigDecimal(10, (BigDecimal) row.get("ni"));
        statement.setBigDecimal(11, (BigDecimal) row.get("sn"));
        statement.setBigDecimal(12, (BigDecimal) row.get("v"));
        statement.setBigDecimal(13, (BigDecimal) row.get("nb"));
        statement.setBigDecimal(14, (BigDecimal) row.get("ti"));
        statement.setString(15, (String) row.get("design"));
        statement.setInt(16, getIntValue(row.get("internal")));
        statement.setString(17, (String) row.get("balance_s"));
        statement.setInt(18, getIntValue(row.get("brinnel_reading")));
        statement.setInt(19, getIntValue(row.get("bore_size")));
        statement.setString(20, (String) row.get("wheel_serial"));
        statement.setString(21, (String) row.get("mec_serial"));
        statement.setBigDecimal(22, (BigDecimal) row.get("tape_size"));
        statement.setInt(23, getIntValue(row.get("wheel_w")));
        statement.setString(24, DateFormatUtils.format((Date) row.get("last_barcode"), "yyyy-MM-dd HH:mm:ss"));
        statement.setString(25, (String) row.get("sample_wheel"));
        statement.setString(26, "合格");
        statement.setString(27, "合格");
        statement.setString(28, (String) row.get("ladle_record_key"));
        statement.setString(29, (String) row.get("pour_d_t"));
        statement.setInt(30, getIntValue(row.get("heat_line")));
        statement.setString(31, DateFormatUtils.format((Date) row.get("hi_heat_in_date"), "yyyy-MM-dd"));
        statement.setString(32, (String) row.get("hi_heat_in_time"));
        statement.setString(33, DateFormatUtils.format((Date) row.get("hi_heat_out_date"), "yyyy-MM-dd"));
        statement.setString(34, (String) row.get("hi_heat_out_time"));
        statement.setString(35, DateFormatUtils.format((Date) row.get("low_heat_in_date"), "yyyy-MM-dd"));
        statement.setString(36, (String) row.get("low_heat_in_time"));
        statement.setString(37, DateFormatUtils.format((Date) row.get("low_heat_out_date"), "yyyy-MM-dd"));
        statement.setString(38, (String) row.get("low_heat_out_time"));
        statement.addBatch();
      } catch (SQLException e) {
        log.error(e.getMessage());
      }
    });
    if (!checkData.isEmpty()) {
      statement.executeBatch();
    }
    connection.commit();
  }

  private Integer getIntValue(Object value) {
    return value == null ? 0 : ((Number) value).intValue();
  }

  private List<Map<String, Object>> findCheckData(Date start, Date end) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT chemistry_detail.c,chemistry_detail.si,chemistry_detail.mn,chemistry_detail.p, chemistry_detail.s,"
            + "chemistry_detail.al,chemistry_detail.cr,chemistry_detail.cu,chemistry_detail.mo,chemistry_detail.ni,"
            + "chemistry_detail.sn, chemistry_detail.v, chemistry_detail.nb,chemistry_detail.ti, wheel_record.design,"
            + "design.internal,wheel_record.balance_s,wheel_record.brinnel_reading,wheel_record.bore_size,wheel_record.wheel_serial,"
            + "SUBSTRING(wheel_record.mec_serial,1,LEN(wheel_record.mec_serial)-CHARINDEX('-',REVERSE(wheel_record.mec_serial))) "
            + "AS mec_serial,wheel_record.tape_size,wheel_record.wheel_w,wheel_record.last_barcode,"
            + "CASE WHEN sample_wheel_record.wheel_serial IS NOT NULL THEN '1' ELSE '' END AS sample_wheel,"
            + "ladle_record.ladle_record_key,CONVERT(VARCHAR(20),pour_record.pour_d_t,120) AS pour_d_t,heat.heat_line,"
            + "heat.hi_heat_in_date,CONVERT(VARCHAR(8),heat.hi_heat_in_time,114) AS hi_heat_in_time,heat.hi_heat_out_date,"
            + "CONVERT(VARCHAR(8),heat.hi_heat_out_time,114) AS hi_heat_out_time,heat.low_heat_in_date,"
            + "CONVERT(VARCHAR(8),heat.low_heat_in_time,114) AS low_heat_in_time,"
            + "heat.low_heat_out_date,CONVERT(VARCHAR(8),heat.low_heat_out_time,114) AS low_heat_out_time "
            + "FROM chemistry_detail INNER JOIN wheel_record INNER JOIN design ON wheel_record.design = design.design "
            + "ON chemistry_detail.ladle_id = wheel_record.ladle_id "
            + "INNER JOIN ladle_record ON ladle_record.id = wheel_record.ladle_id "
            + "INNER JOIN pour_record ON wheel_record.wheel_serial = pour_record.wheel_serial "
            + "INNER JOIN heat ON heat.id = wheel_record.heat_id "
            + "LEFT JOIN sample_wheel_record ON wheel_record.wheel_serial = sample_wheel_record.wheel_serial "
            + "WHERE design.internal = 1 AND wheel_record.finished = 1 AND wheel_record.confirmed_scrap = 0 "
            + "AND wheel_record.last_barcode >= :start AND wheel_record.last_barcode <= :end "
            + "ORDER BY wheel_record.wheel_serial";

    params.put("start", start);
    params.put("end", end);

    List<Map<String, Object>> list = template.queryForList(sql, params);
    return list;
  }

  private String createTable(Connection connection, String checkNo) throws Exception {
    String tableName = "wei" + checkNo;
    String sql =
        "create table " + tableName
            + " (C Decimal(10,3),Si Decimal(10,3),Mn Decimal(10,3),P Decimal(10,3),S Decimal(10,3),Al Decimal(10,3),Cr Decimal"
            + "(10,3),Cu Decimal(10,3),Mo Decimal(10,3),Ni Decimal(10,3),Sn Decimal(10,3),V Decimal(10,3),Nb Decimal(10,3),"
            + "Ti Decimal(10,3),Design Text,Internal Integer,Balance_S Text,Brinnel_Reading Integer,Bore_Size Integer,"
            + "Wheel_Serial Text,Mec_Serial Text,Tape_Size Decimal(6,1),Wheel_W Integer,Last_Barcode DateTime,SampleWheel Text,"
            + "磁探 Text,超探 Text,Ladle_Record_Key Text,Pour_D_T Text,Heat_Line Integer,Hi_Heat_In_Date DateTime,"
            + "Hi_Heat_In_Time Text,Hi_Heat_Out_Date DateTime,Hi_Heat_Out_Time Text,Low_Heat_In_Date DateTime,"
            + "Low_Heat_In_Time Text,Low_Heat_Out_Date DateTime,Low_Heat_Out_Time Text);";
    @Cleanup Statement statement = connection.createStatement();
    statement.execute(sql);
    return tableName;
  }

  @Transactional
  public List<CheckMecserialResponse> checkMecserial(CheckMecserialRequest checkMecserialRequest) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT t1.Mec_serial, t2.report_date, t1.Amount FROM ( SELECT CASE WHEN CHARINDEX( '-', Mec_serial, 10 ) - 1 > 0 THEN LEFT( Mec_serial, CHARINDEX( '-', Mec_serial, 10 ) - 1 ) ELSE Mec_serial END AS Mec_serial, "
            + "COUNT(wheel_record.wheel_serial) AS Amount FROM wheel_record WHERE check_code = :check_code";

    params.put ("check_code", checkMecserialRequest.getAcceptanceNo());
    sql += " GROUP BY CASE WHEN CHARINDEX( '-', Mec_serial, 10 ) - 1 > 0 THEN LEFT( Mec_serial, CHARINDEX( '-', Mec_serial, 10 ) - 1 ) ELSE Mec_serial END) AS t1 LEFT JOIN (SELECT CASE WHEN CHARINDEX( '-', test_no, 10 ) - 1 > 0 THEN LEFT( test_no, CHARINDEX( '-', test_no, 10 ) - 1 ) ELSE test_no END AS Mec_serial, "
            + "MAX (report_date) AS report_date FROM mec_property GROUP BY CASE WHEN CHARINDEX( '-', test_no, 10 ) - 1 > 0 THEN LEFT( test_no, CHARINDEX( '-', test_no, 10 ) - 1 ) ELSE test_no END) AS t2 ON t1.Mec_serial = t2.Mec_serial "
            + "ORDER BY t2.report_date DESC, t1.Amount DESC";
    List<Map<String, Object>> list = template.queryForList(sql, params);
    List<CheckMecserialResponse> checkMecserialResponseList = new ArrayList<>();
    list.forEach(l ->
            checkMecserialResponseList.add(
                    CheckMecserialResponse.builder().mecSerial(getStringValue(l.get("Mec_serial")))
                            .reportDate(l.get("report_date") == null ? null : (Date) l.get("report_date"))
                            .quantity(((Number) l.get("Amount")).intValue()).build()));

    return checkMecserialResponseList;
  }

  @Transactional
  public List<CheckWheelResponse> checkAbc(CheckWheelRequest checkWheelRequest) {
    createTempTable();
    if (checkWheelRequest.getAbc() != null) {
      insertAbcData(checkWheelRequest.getAbc());
    }
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT #abc.column2,t1.wheel_serial,t1.last_barcode,t1.finished,t1.design,t1.bore_size,"
        + "t1.wheel_w,t1.tape_size FROM #abc LEFT JOIN (SELECT wheel_record.wheel_serial, wheel_record.last_barcode, wheel_record.finished, "
        + "wheel_record.design,wheel_record.bore_size,wheel_record.wheel_w,wheel_record.tape_size FROM wheel_record WHERE wheel_record.finished = 1";
    Date start = parseTime(checkWheelRequest.getStartDate());
    Date end = parseTime(checkWheelRequest.getEndDate());
    if (start != null) {
      sql += " AND (wheel_record.last_barcode >= :start)";
      params.put("start", start);
    }
    if (end != null) {
      sql += " AND (wheel_record.last_barcode <= :end)) AS t1 ON #abc.column2 = t1.wheel_serial";
      params.put("end", end);
    }

    sql += " ORDER BY t1.last_barcode";
    List<Map<String, Object>> list = template.queryForList(sql, params);
    deleteTempTable();
    List<CheckWheelResponse> checkWheelResponseList = new ArrayList<>();
    list.forEach(l ->
        checkWheelResponseList.add(
            CheckWheelResponse.builder().column2(getStringValue(l.get("column2")))
                .wheelSerial(getStringValue(l.get("wheel_serial")))
                .lastBarcode(l.get("last_barcode") == null ? null : (Date) l.get("last_barcode"))
                .finished(l.get("finished") == null ? null : ((Number) l.get("finished")).intValue())
                .design(getStringValue(l.get("design")))
                .boreSize(l.get("bore_size") == null ? null : ((Number) l.get("bore_size")).intValue())
                .wheelW(l.get("wheel_w") == null ? null : ((Number) l.get("wheel_w")).intValue())
                .tapeSize(l.get("tape_size") == null ? null : (BigDecimal) l.get("tape_size")).build()));

    return checkWheelResponseList;
  }

  private void deleteTempTable() {
    String sql = "DROP TABLE #abc;";
    jdbcTemplate.execute(sql);
  }

  private void insertAbcData(List<String> abc) {
    String sql = "insert into #abc values (?)";
    List<Object[]> params = abc.stream().map(s -> {
      if (s.startsWith("CO")) {
        s = StringUtils.join(s.substring(10, 12), s.substring(8, 10), s.substring(2, 8));
      }
      return new String[]{s};
    }).collect(Collectors.toList());
    jdbcTemplate.batchUpdate(sql, params);
  }

  private void createTempTable() {
    String sql = "CREATE TABLE #abc(column2 varchar(50) COLLATE Chinese_PRC_CI_AS);";
    jdbcTemplate.execute(sql);
  }

  @Transactional
  public List<CheckWheelResponse> checkWheel(CheckWheelRequest checkWheelRequest) {
    createTempTable();
    if (checkWheelRequest.getAbc() != null) {
      insertAbcData(checkWheelRequest.getAbc());
    }
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT #abc.column2,t1.wheel_serial,t1.last_barcode,t1.finished,t1.design,t1.bore_size,"
        + "t1.wheel_w,t1.tape_size FROM #abc RIGHT JOIN (SELECT wheel_record.wheel_serial, wheel_record.last_barcode, wheel_record.finished, "
        + "wheel_record.design,wheel_record.bore_size,wheel_record.wheel_w,wheel_record.tape_size FROM wheel_record WHERE wheel_record.finished = 1";
    Date start = parseTime(checkWheelRequest.getStartDate());
    Date end = parseTime(checkWheelRequest.getEndDate());
    if (start != null) {
      sql += " AND (wheel_record.last_barcode >= :start)";
      params.put("start", start);
    }
    if (end != null) {
      sql += " AND (wheel_record.last_barcode <= :end)) AS t1 ON #abc.column2 = t1.wheel_serial";
      params.put("end", end);
    }
    sql += " ORDER BY t1.last_barcode";
    List<Map<String, Object>> list = template.queryForList(sql, params);
    deleteTempTable();
    List<CheckWheelResponse> checkWheelResponseList = new ArrayList<>();
    list.forEach(l ->
        checkWheelResponseList.add(
            CheckWheelResponse.builder().column2(getStringValue(l.get("column2")))
                .wheelSerial(getStringValue(l.get("wheel_serial")))
                .lastBarcode(l.get("last_barcode") == null ? null : (Date) l.get("last_barcode"))
                .finished(l.get("finished") == null ? null : ((Number) l.get("finished")).intValue())
                .design(getStringValue(l.get("design")))
                .boreSize(l.get("bore_size") == null ? null : ((Number) l.get("bore_size")).intValue())
                .wheelW(l.get("wheel_w") == null ? null : ((Number) l.get("wheel_w")).intValue())
                .tapeSize(l.get("tape_size") == null ? null : (BigDecimal) l.get("tape_size")).build()));

    return checkWheelResponseList;
  }
}
