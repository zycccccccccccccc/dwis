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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AuditDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String auditBatch;
  private String operator;
  private String wheelSerial;

  @Column(name = "[user]")
  private String user;
  private String cph;
  private String companyCode;
  private String idNumber;
  private String c103;
  private String c104;
  private String c105;
  private String c106;
  private String c107;
  private String c108;
  private String c109;
  private String c110;
  private String c111;
  private String c112;
  private String c113;
  private String c114;
  private String c115;
  private String c116;
  private String c117;
  private String c118;
  private String c119;
  private String c120;
  private String c121;
  private String c122;
  private String c123;
  private String c126;
  private String c130;
  private String c131;
  private String c133;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date opeDT;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
}
