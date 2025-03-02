package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.QMachineData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.QMachineTopInfo;

@Service
@Slf4j
public class QMachineExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "qmachine";
  }

  @Override
  public String fileName() {
    return "MS-05-F-01-01.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 calibra_wheel.operator,calibra_wheel.machine_no,calibra_wheel.ope_d_t,"
        + "calibra_wheel.inspector_id,calibra_wheel.is_check,calibra_wheel.claw1,calibra_wheel.claw1_sym,"
        + "calibra_wheel.claw2,calibra_wheel.claw3,calibra_wheel.rest1,calibra_wheel.rest2,calibra_wheel.rest3 "
        + "FROM calibra_wheel "
        + "WHERE "
        + "CONVERT(VARCHAR(10),calibra_wheel.ope_d_t,120) = :date "
        + "AND "
        + "calibra_wheel.operator = :opeId "
        + "AND "
        + "calibra_wheel.machine_no = :machineNo "
        + "ORDER BY calibra_wheel.ope_d_t ASC";

    params.put("date", date.substring(0, 10));
    params.put("opeId", opeId);
    params.put("machineNo", machineNo);
    QMachineTopInfo qMachineTopInfo = null;
    try {
      qMachineTopInfo = template
          .queryForObject(sql, params, BeanPropertyRowMapper.newInstance(QMachineTopInfo.class));
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
    if (qMachineTopInfo != null) {
      Sheet sheet = workbook.getSheetAt(0);
      int currentRowNum = 3;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(2).setCellValue(qMachineTopInfo.getOperator());
      row.getCell(5).setCellValue(getStringValue(qMachineTopInfo.getMachineNo()));
      row.getCell(8).setCellValue(getStringValueOfDate(qMachineTopInfo.getOpeDT(), "yyyy/M/d"));
      currentRowNum = currentRowNum + 2;
      row = sheet.getRow(currentRowNum);
      row.getCell(2).setCellValue(qMachineTopInfo.getInspectorId());
      row.getCell(5).setCellValue(getMark(qMachineTopInfo.getIsCheck()));

      currentRowNum = currentRowNum + 6;
      row = sheet.getRow(currentRowNum);
      row.getCell(2).setCellValue(getStringValueWithScale(qMachineTopInfo.getClaw1(), 2));
      row.getCell(3).setCellValue(getStringValueWithScale(qMachineTopInfo.getClaw1Sym(), 2));
      row.getCell(4).setCellValue(getStringValueWithScale(qMachineTopInfo.getClaw2(), 2));
      row.getCell(5).setCellValue(getStringValueWithScale(qMachineTopInfo.getClaw3(), 2));
      row.getCell(6).setCellValue(getStringValueWithScale(qMachineTopInfo.getRest1(), 2));
      row.getCell(7).setCellValue(getStringValueWithScale(qMachineTopInfo.getRest2(), 2));
      row.getCell(8).setCellValue(getStringValueWithScale(qMachineTopInfo.getRest3(), 2));

      sql = "SELECT q_machine_record.wheel_serial,q_machine_record.original_gm,q_machine_record.chuck1,"
          + "q_machine_record.pad1,q_machine_record.deviation,q_machine_record.memo "
          + "from q_machine_record "
          + "WHERE "
          + "CONVERT(VARCHAR(10),q_machine_record.ope_d_t,120) = :date "
          + "AND "
          + "q_machine_record.operator = :opeId "
          + "AND "
          + "q_machine_record.machine_no = :machineNo "
          + "ORDER BY q_machine_record.ope_d_t ASC";
      List<QMachineData> qMachineDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(QMachineData.class));
      currentRowNum = currentRowNum + 4;

      insertQMachineData(qMachineDataList, sheet, currentRowNum);
    }
  }

  private void insertQMachineData(List<QMachineData> qMachineDataList, Sheet sheet, int currentRowNum) {
    addRow(qMachineDataList, sheet, currentRowNum, 12);
    for (int i = 0; i < qMachineDataList.size(); i++) {
      QMachineData qMachineData = qMachineDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(i + 1);
      row.getCell(1).setCellValue(qMachineData.getWheelSerial());
      row.getCell(3).setCellValue(getStringValue(qMachineData.getOriginalGm()));
      row.getCell(4).setCellValue(getStringValueWithScale(qMachineData.getChuck1(), 2));
      row.getCell(5).setCellValue(getStringValueWithScale(qMachineData.getPad1(), 2));
      row.getCell(7).setCellValue(getStringValueWithScale(qMachineData.getDeviation(), 2));
      row.getCell(8).setCellValue(qMachineData.getMemo());
    }
  }
}

