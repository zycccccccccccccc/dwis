package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class WheelRecord implements Serializable {

  public static final String CHECK_TYPE_PRE = "PRE"; // 预检
  public static final String CHECK_TYPE_FINAL = "FINAL"; // 中间
  public static final String CHECK_TYPE_ULTRA = "ULTRA"; // 超探
  public static final String CHECK_TYPE_MAGNETIC = "MAGNETIC"; // 磁探
  public static final String CHECK_TYPE_BALANCE = "BALANCE"; // 平衡机
  public static final String CHECK_TYPE_BARCODE = "BARCODE"; // 补打条码

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer wheelId;
  private Integer ladleId;
  private String design;
  private String wheelSerial;
  private Integer heatId;
  private Integer heatTimes;
  private String heatCode;
  private String holdCode;
  private String reworkCode;
  private String testCode;
  @Builder.Default
  private Integer xrayReq = 0;
  @Builder.Default
  private Integer specialUltra = 0;
  private String scrapCode;
  @Builder.Default
  private Integer confirmedScrap = 0;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date scrapDate;
  private Integer brinReq;
  private Integer brinnelReading;
  private Integer boreSize;
  private BigDecimal tapeSize;
  @Column(name = "[wheel_w]")
  private Integer wheelW;
  private Integer balanceFlag;
  @Column(name = "[balance_v]")
  private Integer balanceV;
  @Column(name = "[balance_a]")
  private Integer balanceA;
  @Column(name = "[balance_s]")
  private String balanceS;
  private Integer preId;
  @Builder.Default
  private Integer pre = 0;
  private Integer finalId;
  @Column(name = "[final]")
  @Builder.Default
  private Integer finalCount = 0;
  private Integer ultraId;
  @Builder.Default
  private Integer ultra = 0;
  private Integer mtId;
  @Builder.Default
  private Integer mt = 0;
  @Builder.Default
  private Integer specialMt = 0;
  private Integer balanceId;
  @Builder.Default
  private Integer balance = 0;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastPre;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastFinal;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastUltra;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastMt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastBalance;
  @Builder.Default
  private Integer finished = 0;
  private Integer barcodeId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date lastBarcode;
  @Builder.Default
  private Integer barcode = 0;
  private Integer xFinishedId;
  private Integer reWeightId;
  private Integer kFinishedId;
  private Integer finRecallId;
  private Integer stockRecallId;
  private Integer outRecallId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date stockDate;
  private String hfs;
  private String checkCode;
  private String shippedNo;
  private String xh;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date preDate;
  private String shelfNumber;
  private BigDecimal grindDepth;
  private String cihenCode;
  private String nGrind;
  private String outSourcingDep;
  @Builder.Default
  private Integer finishPrint = 0;
  private String column2;
  private String column3;
  private String column4;
  private String column5;
  @Transient
  private Integer internal;
  @Transient
  private Integer balanceCheck;
  @Transient
  private String dataMatrix;
  @Transient
  private String heatCodeType;
  private String mecSerial;
  private Integer mecConfirm;
  @Transient
  @Builder.Default
  private List<Integer> boreSizeList = new ArrayList<>();
  @Transient
  private boolean pitFinished;
}
