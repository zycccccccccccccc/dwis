package tech.hciot.dwis.base.jwt;

import java.security.Principal;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@UtilityClass
public class JwtTokenUtil {

  public static JwtTokenUser toJwtTokenUser(Principal principal) {
    return (JwtTokenUser) ((OAuth2Authentication) principal).getUserAuthentication().getPrincipal();
  }

  public static String getUsername(Principal principal) {
    return principal == null ? "" : toJwtTokenUser(principal).getUsername();
  }

}
