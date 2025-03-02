package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
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
public class PreCheckRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String dragInspectorId;
  private String copeInspectorId;
  private String inspectorId;
  private String wheelSerial; // 车轮序列号

  private String design; // 轮型

  @Builder.Default
  private String testCode = ""; // 试验代码

  private String scrapCode; // 报废代码

  private String reworkCode; // 返工代码

  @Builder.Default
  private String heatCode = ""; // 热处理代码

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @Column(name = "[ope_d_t]")
  private Date opeDT;

  private Integer brinReq;
  private Integer ts;
  private BigDecimal grindDepth; // 打磨深度


  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  @Transient
  private Boolean alreadHeat; // 是否完成热处理

  @Transient
  private String tapeSize; // 带尺尺寸

  @Transient
  private String hfs; // 带尺尺寸
}
