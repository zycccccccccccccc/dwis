package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "furnace_tap_table")
public class FurnaceTapCurrent {

  public static final Integer STATUS_SAVED = 1;
  public static final Integer STATUS_COMMITTED = 2;

  @Id
  private Integer id;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date castDate;
  private Integer furnaceNo;
  private Integer furnaceSeq;
  private Integer tapNo;
  private String chargeTankNo;
  private String gafferId;
  private String fsId;
  private String gmrId;
  private String furnaceKey;
  private BigDecimal mtotalWeight;

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
  private String rammingPosition;
  private Integer patchingAmount;
  private Integer rammingAmount;
  private Integer status;
  private String delayedCode;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
  private String memo;

  @Transient
  private Integer chargeTs; // 加料次数

  @Transient
  private Integer addTs; // 添加剂次数

  @Transient
  private Integer o2Ts; // 吹氧次数

  @Transient
  private Integer dipTs; // 浸电极次数

  @Transient
  private Integer voltTs; // 电压变化次数

  @Transient
  private Integer tempTs; // 测温次数
}
