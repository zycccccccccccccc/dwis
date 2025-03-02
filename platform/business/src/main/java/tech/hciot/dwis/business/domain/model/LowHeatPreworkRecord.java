package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class LowHeatPreworkRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @Column(name = "[l_date]")
  private Date lowDate;
  private Integer shift;
  private String supervisor;
  private String operator;
  private String targetTemp;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date actualCycle;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date timeChecked;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
}
