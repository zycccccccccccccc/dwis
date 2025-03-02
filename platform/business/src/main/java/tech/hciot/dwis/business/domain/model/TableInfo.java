package tech.hciot.dwis.business.domain.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Table(name = "v_table_info")
@IdClass(TableInfoKey.class)
public class TableInfo {

  @Id
  private String tableName;
  @Id
  private String columnName;
  private String columnDesc;
  private Boolean isNullable;
  private String columnType;
  private Integer columnId;
}

class TableInfoKey implements Serializable {
  @Id
  private String tableName;
  @Id
  private String columnName;
}