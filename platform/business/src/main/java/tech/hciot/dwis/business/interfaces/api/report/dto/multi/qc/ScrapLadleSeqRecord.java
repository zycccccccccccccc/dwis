package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Data;

@Data
public class ScrapLadleSeqRecord {

  private Integer xh;
  @JsonIgnore
  private String scrapCode;
  private List<ScrapLadleSeqDetail> detail;
}
