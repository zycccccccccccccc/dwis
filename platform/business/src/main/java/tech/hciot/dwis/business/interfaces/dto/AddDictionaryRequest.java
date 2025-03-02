package tech.hciot.dwis.business.interfaces.dto;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddDictionaryRequest {
  @NotBlank
  private String tableName;
  private Map<String, String> values;
}
