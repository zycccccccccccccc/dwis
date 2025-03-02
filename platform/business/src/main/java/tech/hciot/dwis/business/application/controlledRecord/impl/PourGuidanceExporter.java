package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;
import static tech.hciot.dwis.base.util.StandardTimeUtil.timeStr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExporter;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.PourGuidanceChemistryInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.PourGuidanceData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.PourGuidanceHeader;

@Service
@Slf4j
public class PourGuidanceExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public PourGuidanceHeader getPourGuidanceHeader(String date, Integer tapNo) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 heat_record.cast_date,heat_record.furnace_no,heat_record.heat_seq,heat_record.tap_seq,"
        + "heat_record.out_steel_temp,heat_record.bottom_temp,FLOOR(RAND()*6)+26 AS plant_temp,"
        + "a1.nickname as pour_id,"
        + "a2.nickname as furnace_id,"
        + "a3.nickname as model_id,"
        + "a4.nickname as modi_id,"
        + "heat_record.bag_no,"
        + "heat_record.ladle_no,"
        + "a5.nickname as core_setter_id,"
        + "a6.nickname as pour_leader_id,"
        + "a7.nickname as pour_direct_id,"
        + "a8.nickname as open_id,"
        + "a9.nickname as crane_in_id,"
        + "l1.ladle_interval as l1,"
        + "l2.ladle_interval as l2,"
        + "l3.ladle_interval as l3,"
        + "l4.ladle_interval as l4,"
        + "heat_record.cycletime_conveyor "
        + "FROM "
        + "pit_records "
        + "INNER JOIN pour_record ON pit_records.pit_seq = pour_record.pit_seq "
        + "INNER JOIN ladle_record ON ladle_record.id = pour_record.ladle_id "
        + "INNER JOIN heat_record ON heat_record.id = ladle_record.heat_record_id "
        + "LEFT JOIN account a1 ON heat_record.pour_id = a1.username "
        + "LEFT JOIN account a2 ON heat_record.furnace_id = a2.username "
        + "LEFT JOIN account a3 ON heat_record.model_id = a3.username "
        + "LEFT JOIN account a4 ON heat_record.modi_id = a4.username "
        + "LEFT JOIN account a5 ON pour_record.core_setter_id1 = a5.username "
        + "LEFT JOIN account a6 ON heat_record.pourleader_id = a6.username "
        + "LEFT JOIN account a7 ON heat_record.pourdirect_id = a7.username "
        + "LEFT JOIN account a8 ON pit_records.open_id = a8.username "
        + "LEFT JOIN account a9 ON pit_records.crane_in_id = a9.username "
        + "LEFT JOIN ladle_record l1 ON l1.heat_record_id = heat_record.id AND l1.ladle_seq=1 "
        + "LEFT JOIN ladle_record l2 ON l2.heat_record_id = heat_record.id AND l2.ladle_seq=2 "
        + "LEFT JOIN ladle_record l3 ON l3.heat_record_id = heat_record.id AND l3.ladle_seq=3 "
        + "LEFT JOIN ladle_record l4 ON l4.heat_record_id = heat_record.id AND l4.ladle_seq=4 "
        + "WHERE "
        + "heat_record.cast_date = :date AND heat_record.tap_seq = :tapNo "
        + "ORDER BY pit_records.record_created ASC";

    params.put("tapNo", tapNo);
    params.put("date", date.substring(0, 10));
    PourGuidanceHeader pourGuidanceHeader = null;
    try {
      pourGuidanceHeader = template
          .queryForObject(sql, params, BeanPropertyRowMapper.newInstance(PourGuidanceHeader.class));
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
    return pourGuidanceHeader;
  }

  @Override
  public String type() {
    return "pourguidance";
  }

  @Override
  public String fileName() {
    return "Melt-04-F-01-00.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    PourGuidanceHeader pourGuidanceHeader = getPourGuidanceHeader(date, tapNo);
    if (pourGuidanceHeader != null) {
      Sheet sheet = workbook.getSheetAt(0);
      int currentRowNum = 4;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValueOfDate(pourGuidanceHeader.getCastDate(), "yyyy/M/d"));
      row.getCell(2).setCellValue(getStringValue(pourGuidanceHeader.getFurnaceNo()));
      row.getCell(4).setCellValue(getStringValue(pourGuidanceHeader.getHeatSeq()));
      row.getCell(7).setCellValue(getStringValue(pourGuidanceHeader.getTapSeq()));
      row.getCell(9).setCellValue(getStringValueWithScale(pourGuidanceHeader.getOutSteelTemp(), 0));
      row.getCell(11).setCellValue(getStringValueWithScale(pourGuidanceHeader.getBottomTemp(), 0));
      row.getCell(13).setCellValue(getStringValue(pourGuidanceHeader.getPlantTemp()));
      currentRowNum = currentRowNum + 3;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(pourGuidanceHeader.getPourId());
      row.getCell(2).setCellValue(pourGuidanceHeader.getFurnaceId());
      row.getCell(4).setCellValue(pourGuidanceHeader.getModelId());
      row.getCell(6).setCellValue(pourGuidanceHeader.getModiId());
      row.getCell(9).setCellValue(pourGuidanceHeader.getBagNo());
      row.getCell(12).setCellValue(pourGuidanceHeader.getLadleNo());

      currentRowNum = currentRowNum + 4;
      currentRowNum = insertData(date, tapNo, sheet, currentRowNum);

      row = sheet.getRow(currentRowNum);
      Font font = getFont(workbook);
      String msg = getNameMsg("下芯工", pourGuidanceHeader.getCoreSetterId());
      row.getCell(0).setCellValue(underLineIndex(msg, 3, msg.length(), font));
      msg = getNameMsg("浇注工长", pourGuidanceHeader.getPourLeaderId());
      row.getCell(3).setCellValue(underLineIndex(msg, 4, msg.length(), font));
      msg = getNameMsg("浇注指导", pourGuidanceHeader.getPourDirectId());
      row.getCell(6).setCellValue(underLineIndex(msg, 4, msg.length(), font));
      msg = getNameMsg("开箱工", pourGuidanceHeader.getOpenId());
      row.getCell(9).setCellValue(underLineIndex(msg, 3, msg.length(), font));
      msg = getNameMsg("入桶工", pourGuidanceHeader.getCraneInId());
      row.getCell(12).setCellValue(underLineIndex(msg, 3, msg.length(), font));
      currentRowNum = currentRowNum + 3;
      row = sheet.getRow(currentRowNum);
      msg = getNameMsg("L1倒包时间", getStringValueOfDate(pourGuidanceHeader.getL1(), "mm:ss"));
      row.getCell(0).setCellValue(underLineIndex(msg, 6, msg.length(), font));
      msg = getNameMsg("L2倒包时间", getStringValueOfDate(pourGuidanceHeader.getL2(), "mm:ss"));
      row.getCell(3).setCellValue(underLineIndex(msg, 6, msg.length(), font));
      msg = getNameMsg("L3倒包时间", getStringValueOfDate(pourGuidanceHeader.getL3(), "mm:ss"));
      row.getCell(6).setCellValue(underLineIndex(msg, 6, msg.length(), font));
      msg = getNameMsg("L4倒包时间", getStringValueOfDate(pourGuidanceHeader.getL4(), "mm:ss"));
      row.getCell(9).setCellValue(underLineIndex(msg, 6, msg.length(), font));
      msg = getNameMsg("驱动梁节拍", getStringValueWithScale(pourGuidanceHeader.getCycletimeConveyor(), 2));
      row.getCell(12).setCellValue(underLineIndex(msg, 5, msg.length(), font));

      currentRowNum = currentRowNum + 3;
      insertChemistry(date, tapNo, sheet, currentRowNum);
    }
  }

  private void insertChemistry(String date, Integer tapNo, Sheet sheet, int currentRowNum) {
    String sql =
        "SELECT chemistry_detail.sample_no,chemistry_detail.c,chemistry_detail.si,chemistry_detail.mn,chemistry_detail.p,"
            + "chemistry_detail.s,chemistry_detail.al,chemistry_detail.cr,chemistry_detail.cu,chemistry_detail.sn,"
            + "(chemistry_detail.Cr+chemistry_detail.Ni+chemistry_detail.Mo+chemistry_detail.Cu) AS aar1,"
            + "(chemistry_detail.Cr+chemistry_detail.Ni+chemistry_detail.Mo) AS tb1,"
            + "(chemistry_detail.Cr+chemistry_detail.Ni+chemistry_detail.Mo+chemistry_detail.Cu+chemistry_detail.Sn+"
            + "chemistry_detail.V+chemistry_detail.Ti+ chemistry_detail.Nb) AS tb2, "
            + "chemistry_detail.create_date "
            /*+ "(930-570*chemistry_detail.c-80*chemistry_detail.mn-20*chemistry_detail.si-50*chemistry_detail.cr-"
            + "30*chemistry_detail.ni-20*chemistry_detail.mo-20*chemistry_detail.v) AS aar2 "*/
            + "FROM heat_record "
            + "INNER JOIN chemistry_detail ON heat_record.id = chemistry_detail.heat_record_id "
            + "WHERE heat_record.cast_date= :date AND heat_record.tap_seq= :tapNo "
            + "ORDER BY chemistry_detail.id";
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    params.put("tapNo", tapNo);
    params.put("date", date.substring(0, 10));
    List<PourGuidanceChemistryInfo> dataList = template
        .query(sql, params, BeanPropertyRowMapper.newInstance(PourGuidanceChemistryInfo.class));
    addRow(dataList, sheet, currentRowNum, 3);
    for (int i = 0; i < dataList.size(); i++) {
      PourGuidanceChemistryInfo pourGuidanceChemistryInfo = dataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(pourGuidanceChemistryInfo.getSampleNo());
      row.getCell(1).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getC(),3));
      row.getCell(2).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getSi(),3));
      row.getCell(3).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getMn(),3));
      row.getCell(4).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getP(),3));
      row.getCell(5).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getS(),3));
      row.getCell(6).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getAl(),3));
      row.getCell(7).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getCr(),3));
      row.getCell(8).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getCu(),3));
      row.getCell(9).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getSn(),3));
      row.getCell(10).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getAar1(),3));
      row.getCell(11).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getTb1(),3));
      row.getCell(12).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getTb2(),3));
      row.getCell(13).setCellValue(getStringValue(pourGuidanceChemistryInfo.getCreateDate()).substring(0, 19));
      // row.getCell(13).setCellValue(getStringValueWithScale(pourGuidanceChemistryInfo.getAar2(),3));
    }
  }

  private String getNameMsg(String title, String name) {
    if (name == null) {
      name = "______";
    }
    return StringUtils.join(title, "    ", name, "    ");
  }

  private int insertData(String date, Integer tapNo, Sheet sheet, int currentRowNum) {
    String sql = "SELECT a.*,b.pit_count FROM (SELECT pour_record.pour_id,pour_record.wheel_serial,pour_record.drag_no,"
        + "pour_record.cope_no,ladle_record.ladle_seq AS bag_no,pour_record.pour_time,ladle_record.ladle_temp,"
        + "pour_record.open_time_cal,pour_record.open_time_act,pit_records.pit_no,pour_record.in_pit_date_time,"
        + "pour_record.scrap_code,pour_record.design "
        + "FROM "
        + "pit_records "
        + "INNER JOIN pour_record on pit_records.pit_seq = pour_record.pit_seq "
        + "INNER JOIN ladle_record on ladle_record.id = pour_record.ladle_id "
        + "INNER JOIN heat_record on heat_record.id = ladle_record.heat_record_id "
        + "WHERE "
        + "heat_record.cast_date = :date "
        + "AND "
        + "heat_record.tap_seq = :tapNo ) a JOIN ( SELECT pit_records.pit_no,COUNT(1) AS pit_count "
        + "FROM "
        + "pit_records "
        + "INNER JOIN pour_record on pit_records.pit_seq = pour_record.pit_seq "
        + "INNER JOIN ladle_record ON ladle_record.id = pour_record.ladle_id "
        + "INNER JOIN heat_record on heat_record.id = ladle_record.heat_record_id "
        + "WHERE "
        + "heat_record.cast_date = :date "
        + "AND "
        + "heat_record.tap_seq = :tapNo "
        + "GROUP BY pit_records.pit_no ) b on a.pit_no = b.pit_no ORDER BY a.pour_id";
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    params.put("tapNo", tapNo);
    params.put("date", date.substring(0, 10));
    List<PourGuidanceData> dataList = template
        .query(sql, params, BeanPropertyRowMapper.newInstance(PourGuidanceData.class));
    addRow(dataList, sheet, currentRowNum, 14);
    for (int i = 0; i < dataList.size(); i++) {
      PourGuidanceData pourGuidanceData = dataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(String.valueOf(i + 1));
      row.getCell(1).setCellValue(pourGuidanceData.getWheelSerial());
      row.getCell(2).setCellValue(pourGuidanceData.getDragNo());
      row.getCell(3).setCellValue(pourGuidanceData.getCopeNo());
      row.getCell(4).setCellValue(getStringValue(pourGuidanceData.getBagNo()));
      row.getCell(5).setCellValue(timeStr(pourGuidanceData.getPourTime()));
      row.getCell(6).setCellValue(pourGuidanceData.getLadleTemp().intValue());
      row.getCell(7).setCellValue(timeStr(pourGuidanceData.getOpenTimeCal()));
      row.getCell(8).setCellValue(timeStr(pourGuidanceData.getOpenTimeAct()));
      row.getCell(9).setCellValue(String.valueOf(pourGuidanceData.getPitNo()));
      row.getCell(10).setCellValue(String.valueOf(pourGuidanceData.getPitCount()));
      row.getCell(11).setCellValue(timeStr(pourGuidanceData.getInPitDateTime()));
      row.getCell(12).setCellValue(pourGuidanceData.getScrapCode());
    }
    return dataList.size() <= 14 ? currentRowNum + 15 : currentRowNum + dataList.size() + 1;
  }

  private Font getFont(Workbook workbook) {
    Font font = workbook.createFont();
    font.setUnderline(Font.U_SINGLE); //下划线
    return font;
  }

  private XSSFRichTextString underLineIndex(String msg, int start, int end, Font font) {
    XSSFRichTextString richString = null;
    richString = new XSSFRichTextString(msg);
    richString.applyFont(start, end, font);  //下划线的起始位置，结束位置
    return richString;
  }
}

