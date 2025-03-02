package tech.hciot.dwis.business.domain.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRoleView {

  @Id
  private String id;
  private String username;
  private String mobile;
  private Integer status;
  private Integer loginStatus;
  private String nickname;
  private String avatar;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "account_role", joinColumns = {@JoinColumn(name = "accountId")}
      , inverseJoinColumns = {@JoinColumn(name = "roleId")})
  @OrderBy("id ASC")
  private List<Role> roles;
}
