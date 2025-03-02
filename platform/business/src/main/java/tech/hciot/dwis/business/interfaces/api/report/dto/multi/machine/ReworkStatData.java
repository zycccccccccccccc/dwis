package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import lombok.Data;

@Data
public class ReworkStatData {

  private String machineDate;
  private Integer castFlat;
  private Integer castTap;
  private Integer castDatum;
  private Integer castHub;
  private Integer machineFlat;
  private Integer machineTap;
  private Integer machineDatum;
  private Integer machineHub;
  private Integer monthCast;
  private Integer monthMachine;
  private Integer monthTotal;
}
