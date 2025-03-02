package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import lombok.Data;

@Data
public class ReworkStatTotal {

  private Integer castFlat = 0;
  private Integer castTap = 0;
  private Integer castDatum = 0;
  private Integer castHub = 0 ;
  private Integer machineFlat = 0;
  private Integer machineTap = 0;
  private Integer machineDatum = 0;
  private Integer machineHub = 0;
  private Integer monthCast = 0;
  private Integer monthMachine = 0;
  private Integer monthTotal = 0;
}
