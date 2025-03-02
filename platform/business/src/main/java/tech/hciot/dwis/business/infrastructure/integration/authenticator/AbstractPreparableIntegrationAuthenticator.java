package tech.hciot.dwis.business.infrastructure.integration.authenticator;

import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.infrastructure.integration.IntegrationAuthentication;

public abstract class AbstractPreparableIntegrationAuthenticator implements IntegrationAuthenticator {

  @Override
  public abstract Account authenticate(IntegrationAuthentication integrationAuthentication);

  @Override
  public abstract void prepare(IntegrationAuthentication integrationAuthentication);

  @Override
  public abstract boolean support(IntegrationAuthentication integrationAuthentication);

  @Override
  public void complete(IntegrationAuthentication integrationAuthentication) {

  }
}
