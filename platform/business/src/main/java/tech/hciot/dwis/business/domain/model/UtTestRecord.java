package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = true)
public class UtTestRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String eqModel;
  private String eqNo;
  private String wheelSerial;
  private String design;
  private Integer probeCheck;
  private String roomTemp;

  private Integer tDb;
  @Builder.Default
  private Integer j1 = 100;
  @Builder.Default
  private Integer j2 = 100;
  @Builder.Default
  private Integer j3 = 100;
  @Builder.Default
  private Integer j4 = 100;
  @Builder.Default
  private Integer j5 = 100;
  @Builder.Default
  private Integer j6 = 100;
  @Builder.Default
  private Integer j7 = 100;

  private Integer bDb;
  @Builder.Default
  private Integer z1 = 100;
  @Builder.Default
  private Integer z2 = 100;
  @Builder.Default
  private Integer z3 = 100;
  @Builder.Default
  private Integer z4 = 100;
  @Builder.Default
  private Integer z5 = 100;

  private Integer wheelCheck;
  private Integer ts;
  private String operator;
  private String inspectorId;
  private String shiftNo;

  @Builder.Default
  private Integer isInspecCheck = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  @Override
  public void setIsInspecCheck(Integer isInspecCheck) {
    this.isInspecCheck = isInspecCheck;
    ts++;
  }
}
