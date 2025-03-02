package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class PreCheckReworkScrapData {

  private String wheelSerial;
  private String design;
  private String reworkCode;
  private String scrapCode;
  private String currentReworkCode;
  private String currentScrapCode;
  private Integer confirmedScrap;
  private String dragInspectorId;
  private String copeInspectorId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;
}
