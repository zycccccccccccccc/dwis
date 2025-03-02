package tech.hciot.dwis.base.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import tech.hciot.dwis.base.exception.CustomOAuth2AuthenticationEntryPoint;
import tech.hciot.dwis.base.exception.OAuth2ExceptionTranslator;
import tech.hciot.dwis.base.jwt.CustomTokenServices;
import tech.hciot.dwis.base.jwt.JwtTokenUserAuthenticationConverter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Value("${resource.id}")
  private String resourceId;

  @Value("${jwt.sign_key}")
  private String signKey;

  @Override
  public void configure(ResourceServerSecurityConfigurer config) {
    config.tokenServices(tokenServices()).resourceId(resourceId)
        .authenticationEntryPoint(authenticationEntryPoint());
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .anyRequest().permitAll();
  }

  @ConditionalOnMissingBean
  @Bean
  public WebResponseExceptionTranslator webResponseExceptionTranslator() {
    return new OAuth2ExceptionTranslator();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new CustomOAuth2AuthenticationEntryPoint(webResponseExceptionTranslator());
  }

  @Bean
  @Primary
  public ResourceServerTokenServices tokenServices() {
    DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
    UserAuthenticationConverter userTokenConverter = new JwtTokenUserAuthenticationConverter();
    accessTokenConverter.setUserTokenConverter(userTokenConverter);

    CustomTokenServices customTokenServices = new CustomTokenServices();

    // 这里的签名key 保持和认证中心一致
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey(signKey);
    converter.setVerifier(new MacSigner(signKey));
    JwtTokenStore jwtTokenStore = new JwtTokenStore(converter);
    customTokenServices.setTokenStore(jwtTokenStore);
    customTokenServices.setJwtAccessTokenConverter(converter);
    customTokenServices.setDefaultAccessTokenConverter(accessTokenConverter);
    return customTokenServices;
  }
}
