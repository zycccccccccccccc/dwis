package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class MachineRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String wheelSerial;
  private String design;
  private Integer jS1;
  private Integer jS2;
  private BigDecimal tS1;
  private Integer tS2;
  private Integer kS1;
  private Integer kS2;
  private Integer wS1;
  private Integer wS2;

  private Integer jId;
  private Integer jIdLast;
  private Integer jIdRe;
  @Builder.Default
  private Integer jCounts = 0;

  private Integer tId;
  private Integer tIdLast;
  private Integer tIdRe;
  @Builder.Default
  private Integer tCounts = 0;

  private Integer kId;
  private Integer kIdLast;
  private Integer kIdRe;
  @Builder.Default
  private Integer kCounts = 0;

  private Integer wId;
  private Integer wIdLast;
  private Integer wIdRe;
  @Builder.Default
  private Integer wCounts = 0;

  private Integer qId;
  @Builder.Default
  private Integer qCounts = 0;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  private String memo;
}
