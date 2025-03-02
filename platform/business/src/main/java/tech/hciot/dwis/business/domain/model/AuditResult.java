package tech.hciot.dwis.business.domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "v_audit_result")
public class AuditResult {

  @Id
  private Integer id;
  private String auditBatch;
  private String wheelSerial;

  private String auditTapeSize;
  private String internalDesign;
  private String internalBoreSize;
  private String externalDesign;
  private String externalBoreSize;

  private String design;
  private String tapeSize;
  private String boreSize;
  private Integer finished;
}
