package tech.hciot.dwis.business.domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "v_graphite_scrap")
public class GraphiteScrap {

  @Id
  private String graphiteKey;
  private String graphite;
  private String design;
  private String scrapCode;
  private String scrapDate;
}
