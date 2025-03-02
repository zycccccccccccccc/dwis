package tech.hciot.dwis.business.interfaces.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuditRequest {

  @NotEmpty
  private String auditBatch;
  @NotEmpty
  private List<String> request;
}
