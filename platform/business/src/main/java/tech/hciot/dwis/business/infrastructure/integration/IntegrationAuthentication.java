package tech.hciot.dwis.business.infrastructure.integration;

import java.util.Map;
import lombok.Data;


@Data
public class IntegrationAuthentication {

  public static final String SOCIAL_TYPE_PHONE_CODE = "phone_code";

  private String authType;
  private String username;
  private Map<String, String[]> authParameters;

  public String getAuthParameter(String paramter) {
    String[] values = this.authParameters.get(paramter);
    if (values != null && values.length > 0) {
      return values[0];
    }
    return null;
  }
}
