package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class ReworkData {

  private String wheelSerial;
  private String reworkCode;
  private String design;
  private Integer jMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date jMachineOpeDT;
  private String jMachineOperator;
  private Integer tMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date tMachineOpeDT;
  private String tMachineOperator;
  private Integer kMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date kMachineOpeDT;
  private String kMachineOperator;
  private Integer wMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date wMachineOpeDT;
  private String wMachineOperator;
  private Integer firstJMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date firstJMachineOpeDT;
  private String firstJMachineOperator;
  private Integer firstTMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date firstTMachineOpeDT;
  private String firstTMachineOperator;
  private Integer firstKMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date firstKMachineOpeDT;
  private String firstKMachineOperator;
  private Integer firstWMachineNo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date firstWMachineOpeDT;
  private String firstWMachineOperator;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date finalCheckDT;
  private String dragInspectorId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date magCheckDT;
  private String magDragInspectorId;
}
