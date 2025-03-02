package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class PourRecord {

  public static final Integer BZ_CORE = 0;
  public static final Integer BZ_POUR_UNCOMMIT = 1;
  public static final Integer BZ_POUR_COMMITTED = 2;
  public static final Integer BZ_UNPIT = 3;
  public static final Integer BZ_PITTED = 4;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer pourId;
  private Integer ladleId;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date castDate;
  private String design;
  private String wheelSerial;
  private String dragNo;
  private String copeNo;

  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date pourTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Column(name = "[pour_d_t]")
  private Date pourDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date openTimeCal;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date openTimeAct;
  private Integer pitSeq;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date inPitDateTime;
  private String testCode;
  private String scrapCode;
  @Column(name = "[core_setter_id1]")
  private String coreSetterId1;
  private Integer dragScrap;
  private Integer copeScrap;
  @Column(name = "[bz]")
  private Integer bz;
  @Column(name = "[ordinal_n]")
  private Integer ordinalN;
  private String batchNo;
  @Builder.Default
  private Integer xrayReq = 0;
  @Builder.Default
  private Integer vibrateWheel = 0;
  @Builder.Default
  private Integer offPants = 0;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date recordCreated;
  @Transient
  private Integer tapSeq;
  @Transient
  private Integer ladleSeq;
  @Transient
  private Integer pitNo;
}
