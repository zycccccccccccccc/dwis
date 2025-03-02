package tech.hciot.dwis.base.jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

public class JwtTokenUserAuthenticationConverter implements UserAuthenticationConverter {

  @Override
  public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
    return null;
  }

  @Override
  public Authentication extractAuthentication(Map<String, ?> map) {
    if (map.containsKey(USERNAME)) {
      Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
      String username = (String) map.get(USERNAME);
      String accountId = (String) map.get("accountId");
      String operationId = (String) map.get("operationId");
      Integer depId = (Integer) map.get("depId");
      List<String> roleNames = (List<String>) map.get("roleNames");
      JwtTokenUser user = new JwtTokenUser(accountId, username, operationId,
        (Collection<GrantedAuthority>) authorities, roleNames, depId);
      return new UsernamePasswordAuthenticationToken(user, "N/A", authorities);
    }
    return null;
  }

  private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
    Object authorities = map.get(AUTHORITIES);
    if (authorities instanceof String) {
      return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
    }
    if (authorities instanceof Collection) {
      return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
          .collectionToCommaDelimitedString((Collection<?>) authorities));
    }
    throw new IllegalArgumentException("Authorities must be either a String or a Collection");
  }
}
