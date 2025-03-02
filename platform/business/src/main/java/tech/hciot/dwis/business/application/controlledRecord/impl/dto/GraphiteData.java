package tech.hciot.dwis.business.application.controlledRecord.impl.dto;

import java.util.Date;
import lombok.Data;

@Data
public class GraphiteData {

  private Date processDate;
  private String graphiteKey;
  private String manufacturerName;
  private String graphite;
  private String design;
  private String cd;
  private String status;
  private String size1086;
  private String size1111;
  private Integer height;
  private Integer processSize;
  private String reworkCode;
  private String sample;
  private String processId;
  private String graphiteOpeId;
}
