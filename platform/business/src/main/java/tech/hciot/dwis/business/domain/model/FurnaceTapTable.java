package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicInsert
public class FurnaceTapTable {

  public static final Integer STATUS_SAVED = 1;
  public static final Integer STATUS_COMMITTED = 2;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @NotNull
  private Date castDate;
  @NotNull
  private Integer furnaceNo;
  @NotNull
  private Integer furnaceSeq;
  @NotNull
  private Integer tapNo;
  private String chargeTankNo;
  @NotNull
  private String gafferId;
  @NotNull
  private String fsId;
  @NotNull
  private String gmrId;
  private String furnaceKey;
  @Builder.Default
  private BigDecimal mtotalWeight = new BigDecimal(0);

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date tapTime;

  @JsonSerialize(using = CustomerMinuteSecondSerialize.class)
  @JsonDeserialize(using = CustomerMinuteSecondDeserialize.class)
  private Time tapDuration;

  private Integer tapTemp;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date firstPoweronTime;
  private BigDecimal emeterReading;
  private BigDecimal thistimeEconsumption;
  private Integer o2Flow;
  private Integer thistimeO2UseQuantity;
  private Integer electrodeUseQuantity;
  private Integer electrodeBrokenQuantity;
  private Integer plugUseQuantity;
  private Integer plugBrokenQuantity;
  private String fbottomContion;
  private String fwallContion;
  private String froofContion;
  private String tappingSpoutContion;
  private Integer fbottomUsage;
  private Integer fwallUsage;
  private Integer froofUsage;
  private Integer tappingSpoutUsage;
  private String patchingPosition;
  private Integer patchingAmount;
  private String rammingPosition;
  private Integer rammingAmount;
  private Integer status;
  private String delayedCode;

  @JsonFormat(pattern = "yyyy-M-d HH:mm", timezone = "GMT+8")
  private Date createTime;
  private String memo;
}
