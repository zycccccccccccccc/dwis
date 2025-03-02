package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class WheelDevRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String design;
  @NotEmpty
  private String distance;
  @NotNull
  private Integer rimDev;
  @NotNull
  private Integer hubBack;
  @NotNull
  private Integer hubFront;
  @NotNull
  private Integer sideBack;
  @NotNull
  private Integer sideFront;
  @NotNull
  private Integer frontRim;
  @NotNull
  private Integer backRim;
  private Integer diffRim;
  private Integer ts;
  @NotEmpty
  private String operator;
  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String shiftNo;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
