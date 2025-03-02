package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
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
@Table(name = "v_graphite_process")
public class GraphiteProcess {

  @Id
  private String graphiteKey;
  private String graphite;
  private String design;
  private Integer height;

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date processDate;
}
