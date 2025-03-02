package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class HeatRecord {

  //底注包C元素目标值
  public static final BigDecimal C_TARGET = BigDecimal.valueOf(0.71);
  //备料增0.01%炭所需重量(kg)
  public static final BigDecimal C_UNIT = BigDecimal.valueOf(0.56);

  //底注包Si元素目标值
  public static final BigDecimal Si_TARGET = BigDecimal.valueOf(0.55);
  //备料增0.01%硅L1包所需重量(kg)
  public static final BigDecimal Si_UNIT_L1 = BigDecimal.valueOf(0.55);
  //备料增0.01%硅L2-L4包所需重量(kg)
  public static final BigDecimal Si_UNIT_L234 = BigDecimal.valueOf(0.5);

  //底注包Mn元素目标值
  public static final BigDecimal Mn_TARGET = BigDecimal.valueOf(0.75);
  //备料增0.01%锰所需重量(kg)
  public static final BigDecimal Mn_UNIT = BigDecimal.valueOf(0.44);

  //底注包Al元素目标值
  public static final BigDecimal Al_TARGET = BigDecimal.valueOf(0.036);
  //备料增铝L1包所需重量(kg)
  public static final BigDecimal Al_UNIT_L1 = BigDecimal.valueOf(1.9);
  //备料增铝L2-L4包所需重量(kg)
  public static final BigDecimal Al_UNIT_L234 = BigDecimal.valueOf(1.6);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String heatRecordKey;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date castDate;
  private Integer furnaceNo;
  private Integer heatSeq;
  private Integer tapSeq;
  private String pourleaderId;
  private String pourdirectId;
  private String modelId;
  private String pourId;
  private String modiId;
  private String furnaceId;
  private String bagNo;
  private String ladleNo;
  private BigDecimal bottomTemp;
  private BigDecimal outSteelTemp;
  private Integer scrapNum;
  private String delayCode;
  private BigDecimal cycletimeConveyor;
  private Integer teapotDryerNo;
  private Integer ladleDryerNo;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date recordCreated;
  private String memo;

  @Transient
  private boolean editable;
}
