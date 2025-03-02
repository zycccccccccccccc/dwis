package tech.hciot.dwis.business.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnInfo {
  private String prop;
  private String label;

  @Builder.Default
  private String type = "input";

  private Boolean required;

  @Builder.Default
  private Boolean key = false;

  @Builder.Default
  private Map<String, Object> opProps = new HashMap<>();

  private List<Option> options;

  private String validate;

  private Integer columnId;
}
