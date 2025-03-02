package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;

import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExporter;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.AdditionMaterialInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.ChargeMaterialInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.ChemistryInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.DipElectrodeInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.FurnaceTapInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.O2BlowingInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.TempMeasureInfo;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.VoltChangeInfo;

@Service
@Slf4j
public class TapSheetExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public ReportData getData(String date, Integer tapNo) {
    ReportData reportData = new ReportData();
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 tap_no,tap_time FROM furnace_tap_table f,(SELECT furnace_seq,furnace_no FROM furnace_tap_table "
        + "WHERE furnace_tap_table.tap_no = :tapNo AND CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE f.furnace_seq = t1.furnace_seq -1 AND f.furnace_no = t1.furnace_no ORDER BY f.cast_date DESC";

    params.put("tapNo", tapNo);
    params.put("date", date.substring(0, 10));
    List<Map<String, Object>> list = template.queryForList(sql, params);
    if (!list.isEmpty()) {
      Map<String, Object> row = list.get(0);
      reportData.setLastTapNo((Integer) row.get("tap_no"));
      reportData.setLastTapTime(getStringValueOfDate((Date) row.get("tap_time"), "yyyy/M/d HH:mm"));
    }

    sql = "SELECT cast_date,CONCAT(furnace_no,'-',furnace_seq,'-',tap_no) AS tap_no,charge_tank_no,"
        + "first_poweron_time,tap_time,tap_time-first_poweron_time AS use_time,mtotal_weight,"
        + "emeter_reading,emeter_reading-thistime_econsumption AS emeter_reading_start,"
        + "thistime_econsumption,delayed_code,o2_flow,"
        + "o2_flow-thistime_o2_use_quantity AS o2_start,thistime_o2_use_quantity,fbottom_contion,"
        + "fwall_contion,froof_contion,tapping_spout_contion,fbottom_usage,"
        + "fwall_usage,froof_usage,tapping_spout_usage,patching_position,"
        + "ramming_position,patching_amount,ramming_amount,"
        + "electrode_use_quantity,electrode_broken_quantity,plug_use_quantity,"
        + "plug_broken_quantity,furnace_tap_table.memo,"
        + "a1.nickname AS leader,a2.nickname AS furnace_leader,a3.nickname AS material_staff,"
        + "tap_duration,tap_temp "
        + "FROM furnace_tap_table "
        + "INNER JOIN account a1 ON a1.username = Furnace_Tap_Table.gaffer_id "
        + "INNER JOIN account a2 ON a2.username = Furnace_Tap_Table.fs_id "
        + "INNER JOIN account a3 ON a3.username = Furnace_Tap_Table.gmr_id "
        + "WHERE "
        + "furnace_tap_table.tap_no = :tapNo AND CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date";
    FurnaceTapInfo furnaceTapInfo = null;
    try {
      furnaceTapInfo = template
          .queryForObject(sql, params, BeanPropertyRowMapper.newInstance(FurnaceTapInfo.class));
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
    reportData.setFurnaceTapInfo(furnaceTapInfo);

    sql = "SELECT sample_no,c,si,mn,p,s,cr,cr+ni+mo AS cr_ni_mo "
        + "FROM "
        + "chemistry_detail,(SELECT id FROM heat_record,(SELECT CONVERT(varchar(100),furnace_tap_table.cast_date, 121) AS year,"
        + "furnace_no,furnace_seq FROM furnace_tap_table WHERE furnace_tap_table.tap_no = :tapNo AND "
        + "CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE heat_record.furnace_no = t1.furnace_no AND heat_record.heat_seq = t1.furnace_seq "
        + "AND CONVERT(varchar(100),heat_record.cast_date, 121) = t1.year) t2 "
        + "WHERE chemistry_detail.Heat_record_id = t2.id AND (chemistry_detail.sample_no LIKE '%P%' "
        + "OR chemistry_detail.sample_no LIKE '%T') "
        + "ORDER BY chemistry_detail.create_date ASC";
    reportData.setChemistryInfoList(template
        .query(sql, params, BeanPropertyRowMapper.newInstance(ChemistryInfo.class)));

    sql = "SELECT times,charge_time,poweron_time,purchase_wheel_weight,wheel_returns_weight,rail_weight,"
        + "bobs_and_heads_weight,rough_returns_weight,wheel_tyre_weight,turnning_weight,hammer_weight,"
        + "guard_rail_buckle_weight,steel_board_weight,coupler_weight,hboard_weight,mtotal_weight "
        + "FROM charge_material_table,(SELECT id FROM furnace_tap_table WHERE furnace_tap_table.tap_no = :tapNo AND "
        + "CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE charge_material_table.furnace_tap_id = t1.id "
        + "ORDER BY charge_material_table.create_time ASC";
    reportData.setChargeMaterialInfoList(template
        .query(sql, params, BeanPropertyRowMapper.newInstance(ChargeMaterialInfo.class)));

    sql = "SELECT times,lime_weight,lronore_weight,coke_weight,lance_quantity,carbon_weight,simn_weight,fesi_weight,"
        + "fluorite_weight,hottop_weight,thermo_quantity,fe_weight,hold1_weight,hold2_weight,hold3_weight "
        + "FROM addition_material_table,(SELECT id FROM furnace_tap_table WHERE furnace_tap_table.tap_no = :tapNo AND "
        + "CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE addition_material_table.furnace_tap_id = t1.id "
        + "ORDER BY addition_material_table.create_time ASC";
    reportData.setAdditionMaterialInfoList(template
        .query(sql, params, BeanPropertyRowMapper.newInstance(AdditionMaterialInfo.class)));

    sql = "SELECT times,pressure,time_begin,time_end "
        + "FROM o2blowing_table,(SELECT id FROM furnace_tap_table WHERE furnace_tap_table.tap_no = :tapNo AND "
        + "CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE o2blowing_table.furnace_tap_id = t1.id "
        + "ORDER BY o2blowing_table.create_time ASC";
    reportData.setO2BlowingInfoList(template
        .query(sql, params, BeanPropertyRowMapper.newInstance(O2BlowingInfo.class)));

    sql = "SELECT times,volt,time_begin,time_end "
        + "FROM volt_change_table,(SELECT id FROM furnace_tap_table WHERE furnace_tap_table.tap_no = :tapNo AND "
        + "CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE volt_change_table.furnace_tap_id = t1.id "
        + "ORDER BY volt_change_table.create_time ASC";
    reportData.setVoltChangeInfoList(template
        .query(sql, params, BeanPropertyRowMapper.newInstance(VoltChangeInfo.class)));

    sql = "SELECT times,time_begin,time_end "
        + "FROM dipelectrode_table,(SELECT id FROM furnace_tap_table WHERE furnace_tap_table.tap_no = :tapNo AND "
        + "CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE dipelectrode_table.furnace_tap_id = t1.id "
        + "ORDER BY dipelectrode_table.create_time ASC";
    reportData.setDipElectrodeInfoList(template
        .query(sql, params, BeanPropertyRowMapper.newInstance(DipElectrodeInfo.class)));

    sql = "SELECT times,temperature "
        + "FROM tempmeasure_table,(SELECT id FROM furnace_tap_table WHERE furnace_tap_table.tap_no = :tapNo AND "
        + "CONVERT(varchar(100),furnace_tap_table.cast_date, 23) = :date) t1 "
        + "WHERE tempmeasure_table.furnace_tap_id = t1.id "
        + "ORDER BY tempmeasure_table.create_time ASC";
    reportData.setTempMeasureInfoList(template
        .query(sql, params, BeanPropertyRowMapper.newInstance(TempMeasureInfo.class)));

    return reportData;
  }

  @Override
  public String type() {
    return "tapsheet";
  }

  @Override
  public String fileName() {
    return "Melt-02-F-01-02.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    ReportData reportData = getData(date, tapNo);
    log.info("tapsheet data is :{}", reportData);
    if (reportData.getFurnaceTapInfo() != null) {
      Sheet sheet = workbook.getSheetAt(0);
      int currentRowNum = 3;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValueOfDate(reportData.getFurnaceTapInfo().getCastDate(), "yyyy/M/d"));
      row.getCell(3).setCellValue(reportData.getFurnaceTapInfo().getTapNo());
      row.getCell(5).setCellValue(getStringValue(reportData.getLastTapNo()));
      row.getCell(7).setCellValue(reportData.getFurnaceTapInfo().getChargeTankNo());
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValueOfDate(reportData.getFurnaceTapInfo().getFirstPoweronTime(), "yyyy/M/d HH:mm"));
      row.getCell(3).setCellValue(getStringValueOfDate(reportData.getFurnaceTapInfo().getTapTime(), "yyyy/M/d HH:mm"));
      row.getCell(5).setCellValue(reportData.getLastTapTime());
      row.getCell(7).setCellValue(getStringValueOfDate(reportData.getFurnaceTapInfo().getUseTime(), "HH:mm"));
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getMtotalWeight()));
      row.getCell(3).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getEmeterReading()));
      row.getCell(5).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getEmeterReadingStart()));
      row.getCell(7).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getThistimeEconsumption()));
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(reportData.getFurnaceTapInfo().getDelayedCode());
      row.getCell(3).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getO2Flow()));
      row.getCell(5).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getO2Start()));
      row.getCell(7).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getThistimeO2UseQuantity()));
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(reportData.getFurnaceTapInfo().getFbottomContion());
      row.getCell(3).setCellValue(reportData.getFurnaceTapInfo().getFwallContion());
      row.getCell(5).setCellValue(reportData.getFurnaceTapInfo().getFroofContion());
      row.getCell(7).setCellValue(reportData.getFurnaceTapInfo().getTappingSpoutContion());
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getFbottomUsage()));
      row.getCell(3).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getFwallUsage()));
      row.getCell(5).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getFroofUsage()));
      row.getCell(7).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getTappingSpoutUsage()));
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(reportData.getFurnaceTapInfo().getPatchingPosition());
      row.getCell(3).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getPatchingAmount()));
      row.getCell(5).setCellValue(reportData.getFurnaceTapInfo().getRammingPosition());
      row.getCell(7).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getRammingAmount()));
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getElectrodeUseQuantity()));
      row.getCell(3).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getElectrodeBrokenQuantity()));
      row.getCell(5).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getPlugUseQuantity()));
      row.getCell(7).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getPlugBrokenQuantity()));
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValue(reportData.getFurnaceTapInfo().getTapTemp()));
      row.getCell(3).setCellValue(reportData.getFurnaceTapInfo().getMemo());
      currentRowNum = currentRowNum + 2;

      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(reportData.getFurnaceTapInfo().getLeader());
      row.getCell(3).setCellValue(reportData.getFurnaceTapInfo().getFurnaceLeader());
      row.getCell(5).setCellValue(reportData.getFurnaceTapInfo().getMaterialStaff());
      row.getCell(7).setCellValue(getStringValueOfDate(reportData.getFurnaceTapInfo().getTapDuration(), "mm:ss"));

      currentRowNum = currentRowNum + 4;

      currentRowNum = insertChemistryInfo(reportData.getChemistryInfoList(), sheet, currentRowNum) + 3;
      currentRowNum = insertChargeMaterialInfo(reportData.getChargeMaterialInfoList(), sheet, currentRowNum) + 3;
      currentRowNum = insertAdditionMaterialInfo(reportData.getAdditionMaterialInfoList(), sheet, currentRowNum) + 3;
      currentRowNum = insertO2BlowingInfo(reportData.getO2BlowingInfoList(), sheet, currentRowNum) + 3;
      currentRowNum = insertVoltChangeInfo(reportData.getVoltChangeInfoList(), sheet, currentRowNum) + 3;
      currentRowNum = insertDipElectrodeInfo(reportData.getDipElectrodeInfoList(), sheet, currentRowNum) + 3;
      insertTempMeasureInfo(reportData.getTempMeasureInfoList(), sheet, currentRowNum);
    }
  }

  private void insertTempMeasureInfo(List<TempMeasureInfo> tempMeasureInfoList, Sheet sheet, int currentRowNum) {
    addRow(tempMeasureInfoList, sheet, currentRowNum, 3);
    for (int i = 0; i < tempMeasureInfoList.size(); i++) {
      TempMeasureInfo tempMeasureInfo = tempMeasureInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValue(tempMeasureInfo.getTimes()));
      row.getCell(1).setCellValue(tempMeasureInfo.getTemperature());
    }
  }

  private int insertDipElectrodeInfo(List<DipElectrodeInfo> dipElectrodeInfoList, Sheet sheet, int currentRowNum) {
    addRow(dipElectrodeInfoList, sheet, currentRowNum, 3);
    for (int i = 0; i < dipElectrodeInfoList.size(); i++) {
      DipElectrodeInfo dipElectrodeInfo = dipElectrodeInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValue(dipElectrodeInfo.getTimes()));
      row.getCell(1).setCellValue(getStringValueOfDate(dipElectrodeInfo.getTimeBegin(), "yyyy/M/d HH:mm"));
      row.getCell(2).setCellValue(getStringValueOfDate(dipElectrodeInfo.getTimeEnd(), "yyyy/M/d HH:mm"));
    }
    return dipElectrodeInfoList.size() <= 3 ? currentRowNum + 2 : currentRowNum + dipElectrodeInfoList.size() - 1;
  }

  private int insertVoltChangeInfo(List<VoltChangeInfo> voltChangeInfoList, Sheet sheet, int currentRowNum) {
    addRow(voltChangeInfoList, sheet, currentRowNum, 3);
    for (int i = 0; i < voltChangeInfoList.size(); i++) {
      VoltChangeInfo voltChangeInfo = voltChangeInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValue(voltChangeInfo.getTimes()));
      row.getCell(1).setCellValue(getStringValueOfDate(voltChangeInfo.getTimeBegin(), "yyyy/M/d HH:mm"));
      row.getCell(2).setCellValue(getStringValueOfDate(voltChangeInfo.getTimeEnd(), "yyyy/M/d HH:mm"));
      row.getCell(3).setCellValue(getStringValue(voltChangeInfo.getVolt()));
    }
    return voltChangeInfoList.size() <= 3 ? currentRowNum + 2 : currentRowNum + voltChangeInfoList.size() - 1;
  }

  private int insertO2BlowingInfo(List<O2BlowingInfo> o2BlowingInfoList, Sheet sheet, int currentRowNum) {
    addRow(o2BlowingInfoList, sheet, currentRowNum, 3);
    for (int i = 0; i < o2BlowingInfoList.size(); i++) {
      O2BlowingInfo o2BlowingInfo = o2BlowingInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValue(o2BlowingInfo.getTimes()));
      row.getCell(1).setCellValue(getStringValue(o2BlowingInfo.getPressure()));
      row.getCell(2).setCellValue(getStringValueOfDate(o2BlowingInfo.getTimeBegin(), "yyyy/M/d HH:mm"));
      row.getCell(3).setCellValue(getStringValueOfDate(o2BlowingInfo.getTimeEnd(), "yyyy/M/d HH:mm"));
    }
    return o2BlowingInfoList.size() <= 3 ? currentRowNum + 2 : currentRowNum + o2BlowingInfoList.size() - 1;
  }

  private int insertAdditionMaterialInfo(List<AdditionMaterialInfo> additionMaterialInfoList, Sheet sheet, int currentRowNum) {
    addRow(additionMaterialInfoList, sheet, currentRowNum, 2);
    for (int i = 0; i < additionMaterialInfoList.size(); i++) {
      AdditionMaterialInfo additionMaterialInfo = additionMaterialInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValue(additionMaterialInfo.getTimes()));
      StringBuilder sb = new StringBuilder();
      String limeWeight = getStringValueWithScale(additionMaterialInfo.getLimeWeight(), 1);
      if (StringUtils.isNotBlank(limeWeight)) {
        sb.append("石灰-").append(limeWeight).append(";");
      }
      String ironOreWeight = getStringValueWithScale(additionMaterialInfo.getLronoreWeight(), 1);
      if (StringUtils.isNotBlank(ironOreWeight)) {
        sb.append("铁矿石-").append(ironOreWeight).append(";");
      }
      String cokeWeight = getStringValueWithScale(additionMaterialInfo.getCokeWeight(), 1);
      if (StringUtils.isNotBlank(cokeWeight)) {
        sb.append("焦炭-").append(cokeWeight).append(";");
      }
      String lanceQuantity = getStringValueWithScale(additionMaterialInfo.getLanceQuantity(), 1);
      if (StringUtils.isNotBlank(lanceQuantity)) {
        sb.append("吹氧管-").append(lanceQuantity).append(";");
      }
      String carbonWeight = getStringValueWithScale(additionMaterialInfo.getCarbonWeight(), 1);
      if (StringUtils.isNotBlank(carbonWeight)) {
        sb.append("碳粉-").append(carbonWeight).append(";");
      }
      String simnWeight = getStringValueWithScale(additionMaterialInfo.getSimnWeight(), 1);
      if (StringUtils.isNotBlank(simnWeight)) {
        sb.append("硅锰铁-").append(simnWeight).append(";");
      }
      String fesiWeight = getStringValueWithScale(additionMaterialInfo.getFesiWeight(), 1);
      if (StringUtils.isNotBlank(fesiWeight)) {
        sb.append("硅铁-").append(fesiWeight).append(";");
      }
      String fluoriteWeight = getStringValueWithScale(additionMaterialInfo.getFluoriteWeight(), 1);
      if (StringUtils.isNotBlank(fluoriteWeight)) {
        sb.append("萤石-").append(fluoriteWeight).append(";");
      }
      String hottopWeight = getStringValueWithScale(additionMaterialInfo.getHottopWeight(), 1);
      if (StringUtils.isNotBlank(hottopWeight)) {
        sb.append("覆盖剂-").append(hottopWeight).append(";");
      }
      String thermoQuantity = getStringValueWithScale(additionMaterialInfo.getThermoQuantity(), 1);
      if (StringUtils.isNotBlank(thermoQuantity)) {
        sb.append("热电偶-").append(thermoQuantity).append(";");
      }
      String feWeight = getStringValueWithScale(additionMaterialInfo.getFeWeight(), 1);
      if (StringUtils.isNotBlank(feWeight)) {
        sb.append("生铁-").append(feWeight).append(";");
      }
      String hold1Weight = getStringValueWithScale(additionMaterialInfo.getHold1Weight(), 1);
      if (StringUtils.isNotBlank(hold1Weight)) {
        sb.append("预留1-").append(hold1Weight).append(";");
      }
      String hold2Weight = getStringValueWithScale(additionMaterialInfo.getHold2Weight(), 1);
      if (StringUtils.isNotBlank(hold2Weight)) {
        sb.append("预留2-").append(hold2Weight).append(";");
      }
      String hold3Weight = getStringValueWithScale(additionMaterialInfo.getHold3Weight(), 1);
      if (StringUtils.isNotBlank(hold3Weight)) {
        sb.append("预留3-").append(hold3Weight).append(";");
      }
      row.getCell(1).setCellValue(sb.toString());
    }
    return additionMaterialInfoList.size() <= 2 ? currentRowNum + 1 : currentRowNum + additionMaterialInfoList.size() - 1;
  }

  private int insertChargeMaterialInfo(List<ChargeMaterialInfo> chargeMaterialInfoList, Sheet sheet, int currentRowNum) {
    addRow(chargeMaterialInfoList, sheet, currentRowNum, 2);
    for (int i = 0; i < chargeMaterialInfoList.size(); i++) {
      ChargeMaterialInfo chargeMaterialInfo = chargeMaterialInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      StringBuilder sb = new StringBuilder();
      String purchaseWheelWeight = getStringValueWithScale(chargeMaterialInfo.getPurchaseWheelWeight(), 1);
      if (StringUtils.isNotBlank(purchaseWheelWeight)) {
        sb.append("外购车轮-").append(purchaseWheelWeight).append(";");
      }
      String wheelReturnsWeight = getStringValueWithScale(chargeMaterialInfo.getWheelReturnsWeight(), 1);
      if (StringUtils.isNotBlank(wheelReturnsWeight)) {
        sb.append("自产车轮-").append(wheelReturnsWeight).append(";");
      }
      String railWeight = getStringValueWithScale(chargeMaterialInfo.getRailWeight(), 1);
      if (StringUtils.isNotBlank(railWeight)) {
        sb.append("道轨-").append(railWeight).append(";");
      }
      String bobsAndHeadsWeight = getStringValueWithScale(chargeMaterialInfo.getBobsAndHeadsWeight(), 1);
      if (StringUtils.isNotBlank(bobsAndHeadsWeight)) {
        sb.append("冒口及补贴-").append(bobsAndHeadsWeight).append(";");
      }
      String roughReturnsWeight = getStringValueWithScale(chargeMaterialInfo.getRoughReturnsWeight(), 1);
      if (StringUtils.isNotBlank(roughReturnsWeight)) {
        sb.append("粗回炉料-").append(roughReturnsWeight).append(";");
      }
      String wheelTyreWeight = getStringValueWithScale(chargeMaterialInfo.getWheelTyreWeight(), 1);
      if (StringUtils.isNotBlank(wheelTyreWeight)) {
        sb.append("轮箍（轴）-").append(wheelTyreWeight).append(";");
      }
      String turnningWeight = getStringValueWithScale(chargeMaterialInfo.getTurnningWeight(), 1);
      if (StringUtils.isNotBlank(turnningWeight)) {
        sb.append("钢屑-").append(turnningWeight).append(";");
      }
      String hammerWeight = getStringValueWithScale(chargeMaterialInfo.getHammerWeight(), 1);
      if (StringUtils.isNotBlank(hammerWeight)) {
        sb.append("镐边/大沿铁-").append(hammerWeight).append(";");
      }
      String guardRailBuckleWeight = getStringValueWithScale(chargeMaterialInfo.getGuardRailBuckleWeight(), 1);
      if (StringUtils.isNotBlank(guardRailBuckleWeight)) {
        sb.append("护栏扣-").append(guardRailBuckleWeight).append(";");
      }
      String steelBoardWeight = getStringValueWithScale(chargeMaterialInfo.getSteelBoardWeight(), 1);
      if (StringUtils.isNotBlank(steelBoardWeight)) {
        sb.append("钢板/角钢/槽钢-").append(steelBoardWeight).append(";");
      }
      String couplerWeight = getStringValueWithScale(chargeMaterialInfo.getCouplerWeight(), 1);
      if (StringUtils.isNotBlank(couplerWeight)) {
        sb.append("车钩/侧架/连接杆-").append(couplerWeight).append(";");
      }
      String hboardWeight = getStringValueWithScale(chargeMaterialInfo.getHboardWeight(), 1);
      if (StringUtils.isNotBlank(hboardWeight)) {
        sb.append("道板/工字板-").append(hboardWeight).append(";");
      }
      row.getCell(0).setCellValue(getStringValue(chargeMaterialInfo.getTimes()));
      row.getCell(1).setCellValue(getStringValueOfDate(chargeMaterialInfo.getChargeTime(), "yyyy/M/d HH:mm"));
      row.getCell(2).setCellValue(getStringValueOfDate(chargeMaterialInfo.getPoweronTime(), "yyyy/M/d HH:mm"));
      row.getCell(3).setCellValue(getStringValueWithScale(chargeMaterialInfo.getMtotalWeight(), 1));
      row.getCell(4).setCellValue(sb.toString());
    }
    return chargeMaterialInfoList.size() <= 2 ? currentRowNum + 1 : currentRowNum + chargeMaterialInfoList.size() - 1;
  }

  private int insertChemistryInfo(List<ChemistryInfo> chemistryInfoList, Sheet sheet, int currentRowNum) {
    addRow(chemistryInfoList, sheet, currentRowNum, 3);
    for (int i = 0; i < chemistryInfoList.size(); i++) {
      ChemistryInfo chemistryInfo = chemistryInfoList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(chemistryInfo.getSampleNo());
      row.getCell(1).setCellValue(chemistryInfo.getC().setScale(3, RoundingMode.HALF_UP).toPlainString());
      row.getCell(2).setCellValue(chemistryInfo.getSi().setScale(3, RoundingMode.HALF_UP).toPlainString());
      row.getCell(3).setCellValue(chemistryInfo.getMn().setScale(3, RoundingMode.HALF_UP).toPlainString());
      row.getCell(4).setCellValue(chemistryInfo.getP().setScale(3, RoundingMode.HALF_UP).toPlainString());
      row.getCell(5).setCellValue(chemistryInfo.getS().setScale(3, RoundingMode.HALF_UP).toPlainString());
      row.getCell(6).setCellValue(chemistryInfo.getCrNiMo().setScale(3, RoundingMode.HALF_UP).toPlainString());
      row.getCell(7).setCellValue(chemistryInfo.getCr().setScale(3, RoundingMode.HALF_UP).toPlainString());
    }
    return chemistryInfoList.size() <= 3 ? currentRowNum + 2 : currentRowNum + chemistryInfoList.size() - 1;
  }

  @Data
  private class ReportData {

    private Integer lastTapNo;
    private String lastTapTime;
    private FurnaceTapInfo furnaceTapInfo;
    private List<ChemistryInfo> chemistryInfoList;
    private List<ChargeMaterialInfo> chargeMaterialInfoList;
    private List<AdditionMaterialInfo> additionMaterialInfoList;
    private List<O2BlowingInfo> o2BlowingInfoList;
    private List<VoltChangeInfo> voltChangeInfoList;
    private List<DipElectrodeInfo> dipElectrodeInfoList;
    private List<TempMeasureInfo> tempMeasureInfoList;
  }
}

