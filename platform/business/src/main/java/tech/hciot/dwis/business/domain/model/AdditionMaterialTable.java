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
public class AdditionMaterialTable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotNull
  private Integer furnaceTapId;
  @NotNull
  private Integer apId;
  @Builder.Default
  private Integer times = 0;
  private BigDecimal limeWeight;
  private BigDecimal lronoreWeight;
  private BigDecimal cokeWeight;
  private BigDecimal lanceQuantity;
  private BigDecimal carbonWeight;
  private BigDecimal simnWeight;
  private BigDecimal fesiWeight;
  private BigDecimal fluoriteWeight;
  private BigDecimal hottopWeight;
  private BigDecimal thermoQuantity;
  private BigDecimal feWeight;
  private BigDecimal hold1Weight;
  private BigDecimal hold2Weight;
  private BigDecimal hold3Weight;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
