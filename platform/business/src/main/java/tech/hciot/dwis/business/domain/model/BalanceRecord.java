package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicInsert
public class BalanceRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String balanceInspectorId;
  private String markInspectorId;
  private String stickInspectorId;
  private String inspectorId;
  private String wheelSerial;
  private String design;
  private String xh;
  private String testCode;
  private String scrapCode;
  private String reworkCode;
  private String holdCode;
  private String heatCode;
  private String balanceS;
  private Integer balanceV;
  private Integer balanceA;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;
  private Integer specialMt;
  private Integer brinReq;
  private Integer xrayReq;
  private Integer ts;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  @Transient
  @Builder.Default
  private Integer internal = 1;

  @Transient
  private Integer finished; // 是否是成品

  @Transient
  private String dataMatrix; // 二维码
}
