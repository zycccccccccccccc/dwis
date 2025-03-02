package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class PourParams {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String type;
  private String category;
  private Integer minInt;
  private Integer maxInt;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date minTime;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date maxTime;
  private BigDecimal minDec;
  private BigDecimal maxDec;

  @Builder.Default
  private Integer enabled = 1;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
