package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

@Data
public class CihenData {

  private String wheelSerial;
  private Integer grindTime;
  private Integer copeCihenSum;
  private Integer copeSandHoles;
  private Integer dragCihenSum;
  private Integer dragSandHoles;
  private Integer ts;
}
