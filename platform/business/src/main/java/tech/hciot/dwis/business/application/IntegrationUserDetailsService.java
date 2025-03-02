package tech.hciot.dwis.business.application;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.CustomOAuth2Exception;
import tech.hciot.dwis.base.exception.ErrorEnum;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.infrastructure.integration.IntegrationAuthentication;
import tech.hciot.dwis.business.infrastructure.integration.IntegrationAuthenticationContext;
import tech.hciot.dwis.business.infrastructure.integration.authenticator.IntegrationAuthenticator;
import tech.hciot.dwis.business.interfaces.assembler.SmCenterAssembler;

/**
 * 集成认证用户服务
 *
 * @author LIQIU
 * @date 2018-3-7
 **/
@Service
public class IntegrationUserDetailsService implements UserDetailsService {

  @Autowired
  private SmCenterAssembler smCenterAssembler;

  private List<IntegrationAuthenticator> authenticators;

  @Autowired(required = false)
  public void setIntegrationAuthenticators(List<IntegrationAuthenticator> authenticators) {
    this.authenticators = authenticators;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    IntegrationAuthentication integrationAuthentication = IntegrationAuthenticationContext.get();
    //判断是否是集成登录
    if (integrationAuthentication == null) {
      integrationAuthentication = new IntegrationAuthentication();
    }
    integrationAuthentication.setUsername(username);
    Account user = this.authenticate(integrationAuthentication);

    if (user == null) {
      throw new UsernameNotFoundException("username");
    } else if (user.getStatus() == Account.STATUS_DISABLED) {
      throw new CustomOAuth2Exception(ErrorEnum.ACCOUNT_DISABLED.name());
    }

    return smCenterAssembler.toSaasUser(user);
  }

  private Account authenticate(IntegrationAuthentication integrationAuthentication) {
    if (this.authenticators != null) {
      for (IntegrationAuthenticator authenticator : authenticators) {
        if (authenticator.support(integrationAuthentication)) {
          return authenticator.authenticate(integrationAuthentication);
        }
      }
    }
    return null;
  }
}
