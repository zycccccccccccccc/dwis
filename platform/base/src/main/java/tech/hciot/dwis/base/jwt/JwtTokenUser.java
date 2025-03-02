package tech.hciot.dwis.base.jwt;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
public class JwtTokenUser {

  String accountId;
  String username;
  String operatorId;
  Collection<GrantedAuthority> roles;
  List<String> roleNames;
  Integer depId;
}
