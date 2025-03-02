package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
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
public class ShotTestRecord extends InspecCheckable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String operator;
  private String inspectorId;
  private String shiftNo;
  private Integer peeningTime;
  private BigDecimal intensityFront;
  private BigDecimal intensityBack;
  private Integer coverageFront;
  private Integer coverageBack;
  private Integer sieveNo;
  private String shotType;
  private Integer amountOnsieve;
  private String shotpeenerNo;
  private String batchNo;
  private Integer ts;

  @Builder.Default
  private Integer isInspecCheck = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
