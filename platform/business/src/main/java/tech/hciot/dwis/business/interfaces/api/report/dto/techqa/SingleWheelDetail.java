package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleWheelDetail {

  protected String heatRecordKey;
  protected String tapSeq;
  protected String ladleSeq;
  protected String wheelSerial;
  protected String openTimeAct;
  protected String scrapCode;
  protected String confirmedScrap;
  protected String scrapDate;
  protected Integer pre;
  protected Integer finalCount;
  protected Integer ultra;
  protected Integer balance;
  protected String finished;
  protected String mecSerial;
}
