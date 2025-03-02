package tech.hciot.dwis.base.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.util.StringUtils;

public class CustomOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

  private String typeName = OAuth2AccessToken.BEARER_TYPE;

  private String realmName = "oauth";

  public CustomOAuth2AuthenticationEntryPoint(WebResponseExceptionTranslator webResponseExceptionTranslator) {
    this.setExceptionTranslator(webResponseExceptionTranslator);
  }

  @Override
  protected ResponseEntity<?> enhanceResponse(ResponseEntity<?> response, Exception exception) {
    HttpHeaders headers = response.getHeaders();
    String existing = null;
    if (headers.containsKey("WWW-Authenticate")) {
      existing = extractTypePrefix(headers.getFirst("WWW-Authenticate"));
    }
    StringBuilder builder = new StringBuilder();
    builder.append(typeName + " ");
    builder.append("realm=\"" + realmName + "\"");
    if (existing != null) {
      builder.append(", " + existing);
    }
    HttpHeaders update = new HttpHeaders();
    update.putAll(response.getHeaders());
    String temp = builder.toString();
    if (temp.contains("Access token expired")) {
      temp = temp.substring(0, temp.indexOf(":")) + "\"";
    }
    update.set("WWW-Authenticate", temp);
    return new ResponseEntity<>(response.getBody(), update, response.getStatusCode());
  }

  private String extractTypePrefix(String header) {
    String existing = header;
    String[] tokens = existing.split(" +");
    if (tokens.length > 1 && !tokens[0].endsWith(",")) {
      existing = StringUtils.arrayToDelimitedString(tokens, " ").substring(existing.indexOf(" ") + 1);
    }
    return existing;
  }
}
