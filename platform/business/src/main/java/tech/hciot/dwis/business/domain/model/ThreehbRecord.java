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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = true)
public class ThreehbRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotEmpty
  private String wheelSerial;
  @NotNull
  private Integer brinnel1;
  @NotNull
  private Integer brinnel2;
  @NotNull
  private Integer brinnel3;
  private Integer difference;
  private Integer ts;
  @NotNull
  private Integer reTest;
  @NotEmpty
  private String operator;
  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String shiftNo;

  @Builder.Default
  private Integer isInspecCheck = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
