package tech.hciot.dwis.business;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import tech.hciot.dwis.base.exception.CustomOAuth2AuthenticationEntryPoint;
import tech.hciot.dwis.base.exception.OAuth2ExceptionTranslator;
import tech.hciot.dwis.business.infrastructure.integration.IntegrationAuthenticationFilter;
import tech.hciot.dwis.business.interfaces.dto.SaasUser;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  @Value("${jwt.sign_key}")
  private String signKey;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private IntegrationAuthenticationFilter integrationAuthenticationFilter;

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints.authenticationManager(this.authenticationManager).accessTokenConverter(accessTokenConverter()).tokenStore(tokenStore())
        .exceptionTranslator(webResponseExceptionTranslator()).userDetailsService(userDetailsService);
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new CustomOAuth2AuthenticationEntryPoint(webResponseExceptionTranslator());
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    oauthServer.allowFormAuthenticationForClients().addTokenEndpointAuthenticationFilter(integrationAuthenticationFilter);
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.jdbc(dataSource);
  }

  /**
   * token converter
   */
  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
      /***
       * 重写增强token方法,用于自定义一些token返回的信息
       */
      @Override
      public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (null != authentication.getUserAuthentication()) {
          SaasUser user = (SaasUser) authentication.getUserAuthentication().getPrincipal();
          /** 自定义一些token属性 ***/
          final Map<String, Object> additionalInformation = new HashMap<>();
          additionalInformation.put("accountId", user.getAccountId());
          additionalInformation.put("roleNames", user.getRoleNames());
          additionalInformation.put("username", user.getUsername());
          additionalInformation.put("operationId", user.getOperationId());
          additionalInformation.put("depId", user.getDepId());
          ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
        }
        OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
        return enhancedToken;
      }
    };
    accessTokenConverter.setSigningKey(signKey);
    return accessTokenConverter;
  }

  /**
   * token store
   */
  @Bean
  public TokenStore tokenStore() {
    TokenStore tokenStore = new JwtTokenStore(accessTokenConverter());
    return tokenStore;
  }

  @ConditionalOnMissingBean
  @Bean
  public WebResponseExceptionTranslator webResponseExceptionTranslator() {
    return new OAuth2ExceptionTranslator();
  }

}
