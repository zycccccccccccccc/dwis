package tech.hciot.dwis.base.jwt;

import java.util.Map;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

public class CustomTokenServices implements ResourceServerTokenServices {

  @Setter
  private TokenStore tokenStore;

  @Setter
  private DefaultAccessTokenConverter defaultAccessTokenConverter;

  @Setter
  private JwtAccessTokenConverter jwtAccessTokenConverter;

  @Override
  public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
    OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);
    UserAuthenticationConverter userTokenConverter = new JwtTokenUserAuthenticationConverter();
    defaultAccessTokenConverter.setUserTokenConverter(userTokenConverter);
    Map<String, ?> map = jwtAccessTokenConverter.convertAccessToken(readAccessToken(accessToken), oAuth2Authentication);
    return defaultAccessTokenConverter.extractAuthentication(map);
  }

  @Override
  public OAuth2AccessToken readAccessToken(String accessToken) {
    return tokenStore.readAccessToken(accessToken);
  }
}
