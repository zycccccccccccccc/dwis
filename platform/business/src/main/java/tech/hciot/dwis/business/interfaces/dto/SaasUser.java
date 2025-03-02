package tech.hciot.dwis.business.interfaces.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class SaasUser implements UserDetails, CredentialsContainer {

  private static final long serialVersionUID = -277096688048418903L;
  private String password;
  private final String username;
  private final Set<GrantedAuthority> authorities;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;
  private final String accountId;
  private final String operationId;
  private final Integer depId;
  private final List<String> roleNames;

  public SaasUser(String username, String password,
                  Collection<? extends GrantedAuthority> authorities, String accountId, String operationId, List<String> roleNames, Integer depId) {
    this(username, password, true, true, true, true, authorities, accountId, operationId, depId, roleNames);
  }

  public SaasUser(String username, String password, boolean enabled,
      boolean accountNonExpired, boolean credentialsNonExpired,
      boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String accountId, String operationId, Integer depId, List<String> roleNames) {

    if (((username == null) || "".equals(username)) || (password == null)) {
      throw new IllegalArgumentException(
          "Cannot pass null or empty values to constructor");
    }

    this.username = username;
    this.password = password;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.credentialsNonExpired = credentialsNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    this.accountId = accountId;
    this.operationId = operationId;
    this.depId = depId;
    this.roleNames = roleNames;
  }

  private static SortedSet<GrantedAuthority> sortAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
    // Ensure array iteration order is predictable (as per
    // UserDetails.getAuthorities() contract and SEC-717)
    SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
        new AuthorityComparator());

    for (GrantedAuthority grantedAuthority : authorities) {
      Assert.notNull(grantedAuthority,
          "GrantedAuthority list cannot contain any null elements");
      sortedAuthorities.add(grantedAuthority);
    }

    return sortedAuthorities;
  }

  private static class AuthorityComparator implements Comparator<GrantedAuthority>,
      Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public int compare(GrantedAuthority g1, GrantedAuthority g2) {
      // Neither should ever be null as each entry is checked before adding it to
      // the set.
      // If the authority is null, it is a custom authority and should precede
      // others.
      if (g2.getAuthority() == null) {
        return -1;
      }

      if (g1.getAuthority() == null) {
        return 1;
      }

      return g1.getAuthority().compareTo(g2.getAuthority());
    }
  }

  /**
   * Returns {@code true} if the supplied object is a {@code User} instance with the same {@code username} value.
   * <p>
   * In other words, the objects are equal if they have the same username, representing the same principal.
   */
  @Override
  public boolean equals(Object rhs) {
    if (rhs instanceof SaasUser) {
      return username.equals(((SaasUser) rhs).username);
    }
    return false;
  }

  /**
   * Returns the hashcode of the {@code username}.
   */
  @Override
  public int hashCode() {
    return username.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append(": ");
    sb.append("Username: ").append(this.username).append("; ");
    sb.append("Password: [PROTECTED]; ");
    sb.append("Enabled: ").append(this.enabled).append("; ");
    sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
    sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
        .append("; ");
    sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

    if (!authorities.isEmpty()) {
      sb.append("Granted Authorities: ");

      boolean first = true;
      for (GrantedAuthority auth : authorities) {
        if (!first) {
          sb.append(",");
        }
        first = false;

        sb.append(auth);
      }
    } else {
      sb.append("Not granted any authorities");
    }

    return sb.toString();
  }

  @Override
  public void eraseCredentials() {
    password = null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getOperationId() {
    return operationId;
  }

  public Integer getDepId() {
    return depId;
  }

  public List<String> getRoleNames() {
    return roleNames;
  }
}
