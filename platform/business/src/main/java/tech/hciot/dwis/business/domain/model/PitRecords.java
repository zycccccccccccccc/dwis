package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PitRecords {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer pitSeq;
  private Integer pitNo;
  @Column(name = "[in_pit_d_t]")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date inPitDT;
  @Column(name = "[out_pit_d_t_cal]")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date outPitDTCal;
  @Column(name = "[out_pit_d_t_act]")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date outPitDTAct;
  private String craneInId;
  private String craneOutId;
  private String openId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date recordCreated;
}
