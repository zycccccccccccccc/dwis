package tech.hciot.dwis.base.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = CustomOAuthExceptionSerializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {

  private static final long serialVersionUID = 2015623287692716839L;

  public CustomOAuth2Exception(String msg) {
    super(msg);
  }

  @Override
  public String getOAuth2ErrorCode() {
    return this.getMessage();
  }
}
