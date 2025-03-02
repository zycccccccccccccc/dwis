package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class FinalCheckRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String dragInspectorId;
  private String copeInspectorId;
  private String tapeInspectorId;
  private String inspectorId;
  private String wheelSerial;
  private String design;
  private String Xh;
  private Integer boreSize;
  @Column(name = "[wheel_w]")
  private Integer wheelW;
  private String testCode;
  private String scrapCode;
  private String reworkCode;
  private String heatCode;
  private String holdCode;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Column(name = "[ope_d_t]")
  private Date opeDT;
  private Integer brinReq;
  private Integer brinnelReading;
  private String cihenCode;
  private Integer ts;
  private BigDecimal grindDepth;
  private String ngrind;
  private BigDecimal hubThickness;
  private BigDecimal rimParallelism;
  @Builder.Default
  private Integer flangeTreadProfile = 1;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  @Transient
  private String tS1;

  @Transient
  @Builder.Default
  private Boolean release = false;

  @Transient
  @Builder.Default
  private List<BigDecimal> boreSizeList = new ArrayList<>();

  @Transient
  @Builder.Default
  private List<BigDecimal> wheelWidthSizeList = new ArrayList<>();

  @Transient
  private String outSourcingDep;
}
