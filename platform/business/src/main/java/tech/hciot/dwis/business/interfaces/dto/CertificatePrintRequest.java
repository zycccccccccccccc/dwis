package tech.hciot.dwis.business.interfaces.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CertificatePrintRequest {

  private String opeId; // 操作员工号
  @NotEmpty
  private String certificateId; // 合格证号
  private Integer contractId; // 合同编号表记录的ID
  private String contractNo; // 合同编号
  private String contractName; // 合格编号下拉框选中的合同名称
  private Boolean executed; // 是否执行
  private String certificateType; // 合格证类型
}
