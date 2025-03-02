package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "v_pour_delay_stat")
public class PourDelayStat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer heatRecordId;
  private String pourDate;
  private Integer heatSeq;


  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date pourBeginTime;


  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date pourEndTime;

  private Integer pourDuration;

  private Integer pourWheelNum;
}
