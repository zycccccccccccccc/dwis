package tech.hciot.dwis.business.domain.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dictionary {
  @Id private Integer id;
  private String tableName;

  private String columns;
  private String memo;

  @Transient private List<ColumnInfo> columnInfo;

}
