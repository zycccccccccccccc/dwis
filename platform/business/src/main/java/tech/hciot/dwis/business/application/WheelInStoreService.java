package tech.hciot.dwis.business.application;

import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseTime;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCell;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CHECK_DATA_EXPORT_FAILED;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.SHIPPING_DATA_EXPORT_FAILED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.ChangeCheckCodeRequest;
import tech.hciot.dwis.business.interfaces.dto.CheckData;
import tech.hciot.dwis.business.interfaces.dto.OutfitCheckResponse;
import tech.hciot.dwis.business.interfaces.dto.PreInStoreRequest;
import tech.hciot.dwis.business.interfaces.dto.ShippingData;
import tech.hciot.dwis.business.interfaces.dto.WheelInStoreRequest;
import tech.hciot.dwis.business.interfaces.dto.WheelInStoreResponse;

@Service
@Slf4j
public class WheelInStoreService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  private void deleteTempTable() {
    String sql = "DROP TABLE #abc1;";
    jdbcTemplate.execute(sql);
  }

  private void insertAbcData(List<String> abc) {
    String sql = "insert into #abc1 values (?)";
    List<Object[]> params = abc.stream().filter(s -> s.length() == 12).map(s ->
        StringUtils.join(s.substring(10, 12), s.substring(8, 10), s.substring(2, 8))).map(s -> new String[]{s})
        .collect(Collectors.toList());
    jdbcTemplate.batchUpdate(sql, params);
  }

  private void createTempTable() {
    String sql = "CREATE TABLE #abc1(wheel varchar(50) COLLATE Chinese_PRC_CI_AS);";
    jdbcTemplate.execute(sql);
  }

  @Transactional
  public Page<WheelInStoreResponse> checkWheelW(WheelInStoreRequest wheelInStoreRequest, Integer currentPage, Integer pageSize) {
    createTempTable();
    if (wheelInStoreRequest.getAbc1() != null) {
      insertAbcData(wheelInStoreRequest.getAbc1());
    }
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT #abc1.wheel,wheel_record.wheel_serial,wheel_record.last_barcode,wheel_record.finished,"
        + "wheel_record.design "
        + "FROM wheel_record LEFT JOIN #abc1 ON wheel_record.wheel_serial = #abc1.wheel WHERE 1 = 1";
    Date start = parseTime(wheelInStoreRequest.getStartDate());
    Date end = parseTime(wheelInStoreRequest.getEndDate());
    if (start != null) {
      sql += " AND wheel_record.last_barcode >= :start";
      params.put("start", start);
    }
    if (end != null) {
      sql += " AND wheel_record.last_barcode <= :end";
      params.put("end", end);
    }
    if (StringUtils.isNotBlank(wheelInStoreRequest.getDesign())) {
      sql += " AND wheel_record.design = :design";
      params.put("design", wheelInStoreRequest.getDesign());
    }
    if (wheelInStoreRequest.getWheelAsc() == 1) {
      sql += " ORDER BY #abc1.wheel";
    } else if (wheelInStoreRequest.getWheelSerialAsc() == 1) {
      sql += " ORDER BY wheel_record.wheel_serial";
    } else {
      sql += " ORDER BY wheel_record.last_barcode";
    }
    List<Map<String, Object>> list = template.queryForList(sql, params);
    deleteTempTable();
    List<WheelInStoreResponse> checkWheelResponseList = new ArrayList<>();
    list.forEach(l ->
        checkWheelResponseList.add(
            WheelInStoreResponse.builder().wheel(l.get("wheel") == null ? "" : l.get("wheel").toString())
                .wheelSerial(l.get("wheel_serial").toString())
                .lastBalance((Date) l.get("last_barcode")).finished(((Number) l.get("finished")).intValue())
                .design(l.get("design").toString()).build()));
    PagedListHolder pagedListHolder = new PagedListHolder(checkWheelResponseList);
    pagedListHolder.setPage(currentPage);
    pagedListHolder.setPageSize(pageSize);
    return new PageImpl<>(pagedListHolder.getPageList(), PageRequest.of(currentPage, pageSize), checkWheelResponseList.size());
  }

  @Transactional
  public Page<WheelInStoreResponse> checkWheelABC(WheelInStoreRequest wheelInStoreRequest, Integer currentPage,
      Integer pageSize) {
    createTempTable();
    if (wheelInStoreRequest.getAbc1() != null) {
      insertAbcData(wheelInStoreRequest.getAbc1());
    }
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT #abc1.wheel,wheel_record.wheel_serial,wheel_record.last_barcode,wheel_record.finished,"
        + "wheel_record.design "
        + "FROM #abc1 LEFT JOIN wheel_record ON wheel_record.wheel_serial = #abc1.wheel WHERE 1 = 1";
    Date start = parseTime(wheelInStoreRequest.getStartDate());
    Date end = parseTime(wheelInStoreRequest.getEndDate());
    if (start != null) {
      sql += " AND wheel_record.last_barcode >= :start";
      params.put("start", start);
    }
    if (end != null) {
      sql += " AND wheel_record.last_barcode <= :end";
      params.put("end", end);
    }
    if (StringUtils.isNotBlank(wheelInStoreRequest.getDesign())) {
      sql += " AND wheel_record.design = :design";
      params.put("design", wheelInStoreRequest.getDesign());
    }
    if (wheelInStoreRequest.getWheelAsc() == 1) {
      sql += " ORDER BY #abc1.wheel";
    } else if (wheelInStoreRequest.getWheelSerialAsc() == 1) {
      sql += " ORDER BY wheel_record.wheel_serial";
    } else {
      sql += " ORDER BY wheel_record.last_barcode";
    }
    List<Map<String, Object>> list = template.queryForList(sql, params);
    deleteTempTable();
    List<WheelInStoreResponse> checkWheelResponseList = new ArrayList<>();
    list.forEach(l ->
        checkWheelResponseList.add(
            WheelInStoreResponse.builder().wheel(l.get("wheel").toString()).wheelSerial(l.get("wheel_serial").toString())
                .lastBalance((Date) l.get("last_barcode")).finished(((Number) l.get("finished")).intValue())
                .design(l.get("design").toString()).build()));
    PagedListHolder pagedListHolder = new PagedListHolder(checkWheelResponseList);
    pagedListHolder.setPage(currentPage);
    pagedListHolder.setPageSize(pageSize);
    return new PageImpl<>(pagedListHolder.getPageList(), PageRequest.of(currentPage, pageSize), checkWheelResponseList.size());
  }

  public List<OutfitCheckResponse> checkOutfit(String startDate, String endDate, String design) {
    return wheelRecordRepository.getOutfit(startDate, endDate, design);
  }

  public Integer preInStore(PreInStoreRequest preInStoreRequest) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "UPDATE wheel_record SET stock_date = :stockDate,check_code = :checkCode "
        + "WHERE stock_date IS NULL AND last_barcode >= :startTime AND last_barcode <= :endTime AND Finished = 1 "
        + "AND design  = :design";
    params.put("stockDate", parseDate(preInStoreRequest.getInStoreDate()));
    params.put("checkCode", preInStoreRequest.getCheckCode());
    params.put("startTime", parseTime(preInStoreRequest.getStartTime()));
    params.put("endTime", parseTime(preInStoreRequest.getEndTime()));
    params.put("design", preInStoreRequest.getDesign());
    return template.update(sql, params);
  }

  public void changeCode(ChangeCheckCodeRequest changeCheckCodeRequest) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "UPDATE wheel_record SET stock_date = :stockDate,check_code = :newCheckCode "
        + "WHERE LEFT(check_code,1) = 'A' AND check_code = :checkCode";
    params.put("stockDate", ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()));
    params.put("newCheckCode", changeCheckCodeRequest.getCheckCode().substring(2, 8));
    params.put("checkCode", changeCheckCodeRequest.getCheckCode());

    template.update(sql, params);
  }

  public void checkDataExport(String checkCode, String design, HttpServletResponse response) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT wheel_record.check_code,heat_record.cast_date,wheel_record.wheel_serial,wheel_record.design,"
        + "CAST(heat_record.furnace_no AS VARCHAR) +'_'+ CAST(heat_record.heat_seq AS VARCHAR) + '_' + "
        + "CAST(ladle_record.ladle_seq AS VARCHAR) AS heat_no,wheel_record.wheel_w,FLOOR(wheel_record.tape_size) AS tape,"
        + "wheel_record.bore_size,wheel_record.brinnel_reading,wheel_record.last_barcode,wheel_record.stock_date,"
        + "chemistry_detail.C,chemistry_detail.Si,chemistry_detail.Mn,chemistry_detail.S,chemistry_detail.P,chemistry_detail.Al,"
        + "chemistry_detail.Ni,chemistry_detail.Cr,chemistry_detail.Mo,chemistry_detail.V,chemistry_detail.Cu,chemistry_detail.Ti,"
        + "chemistry_detail.Nb "
        + "FROM heat_record INNER JOIN ladle_record ON heat_record.id = ladle_record.Heat_record_id "
        + "INNER JOIN wheel_record ON wheel_record.ladle_id = ladle_record.id "
        + "INNER JOIN chemistry_detail ON chemistry_detail.ladle_id = ladle_record.id "
        + "WHERE wheel_record.check_code= :checkCode AND wheel_record.design = :design AND wheel_record.shipped_no IS NULL "
        + "AND wheel_record.finished = 1 AND wheel_record.confirmed_scrap = 0";
    params.put("design", design);
    params.put("checkCode", checkCode);
    List<CheckData> list = template.query(sql, params, BeanPropertyRowMapper.newInstance(CheckData.class));

    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet();

      String fileName = "DCACC_DWIS_Data_Audit_" + checkCode + ".xlsx";

      String[] headers = {
          "Check_Code",
          "Cast_Date",
          "Wheel_Serial",
          "Design",
          "Heat NO",
          "Wheel_W",
          "Tape",
          "Bore_Size",
          "Brinnel_Reading",
          "Last_Barcode",
          "Stock_Date",
          "C",
          "Si",
          "Mn",
          "S",
          "P",
          "Al",
          "Ni",
          "Cr",
          "Mo",
          "V",
          "Cu",
          "Ti",
          "Nb"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      ExcelUtil.createTitleRow(sheet, headers);

      // 内容
      int rowNum = 1;
      for (CheckData checkData : list) {
        Row row1 = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row1, columnNum++, checkData.getCheckCode(), lengthArray);
        createCell(row1, columnNum++, checkData.getCastDate(), lengthArray);
        createCell(row1, columnNum++, checkData.getWheelSerial(), lengthArray);
        createCell(row1, columnNum++, checkData.getDesign(), lengthArray);
        createCell(row1, columnNum++, checkData.getHeatNo(), lengthArray);
        createCell(row1, columnNum++, checkData.getWheelW(), lengthArray);
        createCell(row1, columnNum++, checkData.getTape(), lengthArray);
        createCell(row1, columnNum++, checkData.getBoreSize(), lengthArray);
        createCell(row1, columnNum++, checkData.getBrinnelReading(), lengthArray);
        createCell(row1, columnNum++, checkData.getLastBalance(), lengthArray);
        createCell(row1, columnNum++, checkData.getStockDate(), lengthArray);
        createCell(row1, columnNum++, checkData.getC(), lengthArray);
        createCell(row1, columnNum++, checkData.getSi(), lengthArray);
        createCell(row1, columnNum++, checkData.getMn(), lengthArray);
        createCell(row1, columnNum++, checkData.getS(), lengthArray);
        createCell(row1, columnNum++, checkData.getP(), lengthArray);
        createCell(row1, columnNum++, checkData.getAl(), lengthArray);
        createCell(row1, columnNum++, checkData.getNi(), lengthArray);
        createCell(row1, columnNum++, checkData.getCr(), lengthArray);
        createCell(row1, columnNum++, checkData.getMo(), lengthArray);
        createCell(row1, columnNum++, checkData.getV(), lengthArray);
        createCell(row1, columnNum++, checkData.getCu(), lengthArray);
        createCell(row1, columnNum++, checkData.getTi(), lengthArray);
        createCell(row1, columnNum++, checkData.getNb(), lengthArray);
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
      throw CHECK_DATA_EXPORT_FAILED.getPlatformException();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public void shippingDataExport(String checkCode, HttpServletResponse response) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT wheel_record.check_code,wheel_record.wheel_serial,wheel_record.design,"
        + "wheel_record.shipped_no,train_no.shipped_date,wheel_record.shelf_number "
        + "FROM train_no INNER JOIN wheel_record ON train_no.shipped_no = wheel_record.shipped_no "
        + "WHERE wheel_record.shipped_no= :checkCode";
    params.put("checkCode", checkCode);
    List<ShippingData> list = template.query(sql, params, BeanPropertyRowMapper.newInstance(ShippingData.class));

    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet();

      String fileName = "DCACC_DWIS_Data_Shipping_" + checkCode + ".xlsx";

      String[] headers = {
          "AR Audit No校验单编码",
          "Wheel No 轮号",
          "Wheel Type 轮型",
          "Shipping Invoice No 合格证号",
          "Shipping Date 发运日期",
          "Shelf_Number"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      ExcelUtil.createTitleRow(sheet, headers);

      // 内容
      int rowNum = 1;
      for (ShippingData shippingData : list) {
        Row row1 = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row1, columnNum++, shippingData.getCheckCode(), lengthArray);
        createCell(row1, columnNum++, shippingData.getWheelSerial(), lengthArray);
        createCell(row1, columnNum++, shippingData.getDesign(), lengthArray);
        createCell(row1, columnNum++, shippingData.getShippedNo(), lengthArray);
        createCell(row1, columnNum++, shippingData.getShippedDate(), lengthArray);
        createCell(row1, columnNum++, shippingData.getShelfNumber(), lengthArray);
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
      throw SHIPPING_DATA_EXPORT_FAILED.getPlatformException();
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
