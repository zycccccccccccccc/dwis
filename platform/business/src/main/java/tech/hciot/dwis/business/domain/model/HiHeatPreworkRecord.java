package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HiHeatPreworkRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @Column(name = "[h_date]")
  private Date highDate;
  @Column(name = "[furno]")
  private Integer furNo;
  private Integer shift;
  private String supervisor;
  private String operator;
  private String targetTemp;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date actualCycle;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date timeChecked;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date treadQuenchDelay;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date treadTimeChecked;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date sprayTime;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date sprayTimeChecked;
  private BigDecimal waterPressure;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date waterPressureTimeChecked;
  private Integer waterTemp;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date waterTempTimeChecked;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
}
