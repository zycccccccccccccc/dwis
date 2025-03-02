package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class UltraRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String ultraInspectorId;
  private String magCopeInspectorId;
  private String magDragInspectorId;
  private String inspectorId;
  private String wheelSerial;
  private String design;
  private String xh;
  private BigDecimal tapeSize;
  private String testCode;
  private String scrapCode;
  private String reworkCode;
  private String heatCode;
  private String balanceS;
  private String holdCode;
  private String hfs;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;
  private Integer specialMt;
  private Integer ts;
  private String remanence;
  private Integer xrayReq;

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
  private List<BigDecimal> tapeSizeList = new ArrayList<>();
}
