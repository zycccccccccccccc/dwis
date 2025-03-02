package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class ScrapLadleRecord {

  private String scrapCode;
  private Integer totalAmount;
  private List<ScrapLadleSeqRecord> data;
}
