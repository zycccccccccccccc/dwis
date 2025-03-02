package tech.hciot.dwis.business.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BucketResponse {

  private String castDate;
  private Integer tapSeq;
  private Integer pitSeq;
  private Integer pitNo;
}
