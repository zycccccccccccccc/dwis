package tech.hciot.dwis.base.exception;

import static tech.hciot.dwis.base.exception.ErrorEnum.USER_VERIFY_IS_INVALID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

public class OAuth2ExceptionTranslator extends DefaultWebResponseExceptionTranslator {

  @Override
  public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
    ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
    CustomOAuth2Exception customOAuth2Exception;
    Throwable throwable = e.getCause();
    if (throwable instanceof PlatformException && ((PlatformException) e.getCause()).getErrorCode() == USER_VERIFY_IS_INVALID
        .getErrorcode()) {
      customOAuth2Exception = new CustomOAuth2Exception(USER_VERIFY_IS_INVALID.toString());
    } else {
      customOAuth2Exception = new CustomOAuth2Exception(responseEntity.getBody().getOAuth2ErrorCode());
    }
    return new ResponseEntity<>(customOAuth2Exception, responseEntity.getHeaders(), responseEntity.getStatusCode());
  }
}
