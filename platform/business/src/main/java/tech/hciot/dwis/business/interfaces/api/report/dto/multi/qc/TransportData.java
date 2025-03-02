package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import lombok.Data;

import java.util.List;

@Data
public class TransportData {

  private Integer xrayAmount;
  private Integer deweightAmount;
  private Integer boreAmount;
  private List<TransportXrayData> xrayList;
  private List<TransportDeweightData> deweightList;
  private List<TransportBoreData> boreList;
}
