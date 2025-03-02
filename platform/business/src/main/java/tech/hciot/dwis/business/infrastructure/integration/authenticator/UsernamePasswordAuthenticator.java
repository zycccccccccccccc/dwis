package tech.hciot.dwis.business.infrastructure.integration.authenticator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import tech.hciot.dwis.business.application.AccountService;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.infrastructure.integration.IntegrationAuthentication;


@Component
@Primary
public class UsernamePasswordAuthenticator extends AbstractPreparableIntegrationAuthenticator {

  @Autowired
  private AccountService accountService;

  @Override
  public Account authenticate(IntegrationAuthentication integrationAuthentication) {
    Account account = null;
    try {
      account = accountService.getAccount(integrationAuthentication.getUsername());
    } catch (Exception e) {

    }
    return account;
  }

  @Override
  public void prepare(IntegrationAuthentication integrationAuthentication) {

  }

  @Override
  public boolean support(IntegrationAuthentication integrationAuthentication) {
    return StringUtils.isEmpty(integrationAuthentication.getAuthType());
  }
}
