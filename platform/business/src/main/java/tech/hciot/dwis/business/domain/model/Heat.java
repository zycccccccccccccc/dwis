package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
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
public class Heat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name = "[h_id]")
  private Integer hid;
  @Column(name = "[l_id]")
  private Integer lid;
  @Column(name = "[wheel_serial_1]")
  private String wheelSerial1;
  @Column(name = "[wheel_serial_2]")
  private String wheelSerial2;
  @Column(name = "[design_1]")
  private String design1;
  @Column(name = "[design_2]")
  private String design2;
  @Column(name = "[test_code_1]")
  private String testCode1;
  @Column(name = "[test_code_2]")
  private String testCode2;
  @Column(name = "[heat_code_1]")
  private String heatCode1;
  @Column(name = "[heat_code_2]")
  private String heatCode2;
  @Column(name = "[scrap_code_1]")
  private String scrapCode1;
  @Column(name = "[scrap_code_2]")
  private String scrapCode2;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date hiHeatInDate;

  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date hiHeatInTime;
  private Integer hiHeatInShift;
  private String hiHeatInId;
  private String hiHeatInOperator;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date hiHeatOutDate;

  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date hiHeatOutTime;
  private Integer hiHeatOutShift;
  private String hiHeatOutId;
  private String hiHeatOutOperator;
  private Integer heatLine;
  private Integer xh;
  private String cutId;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date lowHeatInDate;

  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date lowHeatInTime;
  private Integer lowHeatInShift;
  private String lowHeatInId;
  private String lowHeatInOperator;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date lowHeatOutDate;

  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date lowHeatOutTime;
  private Integer lowHeatOutShift;
  private String lowHeatOutId;
  private String lowHeatOutOperator;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createDateTime;
  @Transient
  private Integer heatCount1;
  @Transient
  private Integer heatCount2;
  private String mecSerial;
  @Transient
  private String mecCount;
  @Transient
  private List<String> yellowLight;
}
