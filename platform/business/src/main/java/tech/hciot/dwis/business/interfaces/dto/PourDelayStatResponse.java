package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PourDelayStatResponse {

  private Integer heatRecordId;
  private String pourDate;
  private Integer heatSeq;


@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date pourStartDate;


@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date pourEndDate;

  private Integer pourDuration;


@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastPourEndDate;

  private Integer pourInterval;
  private Integer pourWheelNum;
  private Integer scrapNum;
  private String delayCode;
  private String memo;
}
