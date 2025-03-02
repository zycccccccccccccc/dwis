package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExporter;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.BalanceData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.PourChemistryInfo;

@Service
@Slf4j
public class ChemistryExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "chemistry";
  }

  @Override
  public String fileName() {
    return "chemistry.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT "
        + "heat_record.tap_seq,heat_record.furnace_no,heat_record.heat_seq,ladle_record.ladle_seq,"
        + "chemistry_detail.c,chemistry_detail.si,chemistry_detail.mn,chemistry_detail.p,chemistry_detail.s,"
        + "chemistry_detail.cr,chemistry_detail.ni,chemistry_detail.v,chemistry_detail.mo,chemistry_detail.ti,"
        + "chemistry_detail.cu,chemistry_detail.al,chemistry_detail.sn,chemistry_detail.nb,chemistry_detail.b as h,"
        + "(chemistry_detail.Cr+chemistry_detail.Ni+chemistry_detail.Mo) AS tb1,"
        + "(chemistry_detail.Cr+chemistry_detail.Ni+chemistry_detail.Mo+chemistry_detail.Cu+chemistry_detail.Sn"
        + "+chemistry_detail.V+chemistry_detail.Ti+chemistry_detail.Nb) AS tb2,"
        + "(chemistry_detail.Cr+chemistry_detail.Ni+chemistry_detail.Mo+chemistry_detail.Cu) AS aar1,"
        + "(930-570*chemistry_detail.c-80*chemistry_detail.mn-20*chemistry_detail.si-50*chemistry_detail.cr"
        + "-30*chemistry_detail.ni-20*chemistry_detail.mo-20*chemistry_detail.v) AS aar2 "
        + "FROM heat_record "
        + "INNER JOIN (ladle_record INNER JOIN chemistry_detail "
        + "ON ladle_record.id = chemistry_detail.ladle_id) ON heat_record.id = ladle_record.heat_record_id "
        + "WHERE "
        + "CONVERT(VARCHAR(10),heat_record.cast_date,120) = :date "
        + "ORDER BY heat_record.tap_seq,ladle_record.ladle_seq";

    params.put("date", date.substring(0, 10));

    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 1;
    Row row = sheet.getRow(currentRowNum);
    row.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK)
        .setCellValue(getStringValueOfDate(parseDate(date.substring(0, 10)), "yyyy/M/d"));
    List<PourChemistryInfo> pourChemistryInfoList = template.query(sql, params, BeanPropertyRowMapper.newInstance(PourChemistryInfo.class));
    currentRowNum = 3;
    insertChemistryInfo(pourChemistryInfoList, sheet, currentRowNum);
  }

  private void insertChemistryInfo(List<PourChemistryInfo> pourChemistryInfoList, Sheet sheet, int currentRowNum) {
    addRow(pourChemistryInfoList, sheet, currentRowNum, 5);
    for (int i = 0; i < pourChemistryInfoList.size(); i++) {
      PourChemistryInfo pourChemistryInfo = pourChemistryInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValue(pourChemistryInfo.getTapSeq()));
      row.getCell(1).setCellValue(getStringValue(pourChemistryInfo.getFurnaceNo()));
      row.getCell(2).setCellValue(getStringValue(pourChemistryInfo.getHeatSeq()));
      row.getCell(3).setCellValue(getStringValue(pourChemistryInfo.getLadleSeq()));
      row.getCell(4).setCellValue(getStringValueWithScale(pourChemistryInfo.getC(), 3));
      row.getCell(5).setCellValue(getStringValueWithScale(pourChemistryInfo.getSi(), 3));
      row.getCell(6).setCellValue(getStringValueWithScale(pourChemistryInfo.getMn(), 3));
      row.getCell(7).setCellValue(getStringValueWithScale(pourChemistryInfo.getP(), 3));
      row.getCell(8).setCellValue(getStringValueWithScale(pourChemistryInfo.getS(), 3));
      row.getCell(9).setCellValue(getStringValueWithScale(pourChemistryInfo.getCr(), 3));
      row.getCell(10).setCellValue(getStringValueWithScale(pourChemistryInfo.getNi(), 3));
      row.getCell(11).setCellValue(getStringValueWithScale(pourChemistryInfo.getV(), 3));
      row.getCell(12).setCellValue(getStringValueWithScale(pourChemistryInfo.getMo(), 3));
      row.getCell(13).setCellValue(getStringValueWithScale(pourChemistryInfo.getTi(), 3));
      row.getCell(14).setCellValue(getStringValueWithScale(pourChemistryInfo.getCu(), 3));
      row.getCell(15).setCellValue(getStringValueWithScale(pourChemistryInfo.getAl(), 3));
      row.getCell(16).setCellValue(getStringValueWithScale(pourChemistryInfo.getSn(), 3));
      row.getCell(17).setCellValue(getStringValueWithScale(pourChemistryInfo.getNb(), 3));
      row.getCell(18).setCellValue(getStringValueWithScale(pourChemistryInfo.getH(), 3));
      row.getCell(19).setCellValue(getStringValueWithScale(pourChemistryInfo.getTb1(), 3));
      row.getCell(20).setCellValue(getStringValueWithScale(pourChemistryInfo.getTb2(), 3));
      row.getCell(21).setCellValue(getStringValueWithScale(pourChemistryInfo.getAar1(), 3));
      row.getCell(22).setCellValue(getStringValueWithScale(pourChemistryInfo.getAar2(), 3));
    }
  }
}

