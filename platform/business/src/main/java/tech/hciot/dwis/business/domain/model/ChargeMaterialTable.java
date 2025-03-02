package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class ChargeMaterialTable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotNull
  private Integer furnaceTapId;
  @Builder.Default
  private Integer times = 0;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @NotNull
  private Date chargeTime;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @NotNull
  private Date poweronTime;
  private BigDecimal purchaseWheelWeight;
  private BigDecimal wheelReturnsWeight;
  private BigDecimal railWeight;
  private BigDecimal bobsAndHeadsWeight;
  private BigDecimal roughReturnsWeight;
  private BigDecimal wheelTyreWeight;
  private BigDecimal turnningWeight;
  private BigDecimal hammerWeight;
  private BigDecimal guardRailBuckleWeight;
  private BigDecimal steelBoardWeight;
  private BigDecimal couplerWeight;
  private BigDecimal hboardWeight;
  private BigDecimal mtotalWeight;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
