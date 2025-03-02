package tech.hciot.dwis.business.domain.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class Role {

  @Id
  @GeneratedValue(generator = "uuidStrategy")
  @GenericGenerator(name = "uuidStrategy", strategy = "uuid")
  private String id;
  private String roleName;
  private String remark;
  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "role_authority", joinColumns = {@JoinColumn(name = "role_id")}
      , inverseJoinColumns = {@JoinColumn(name = "authority_id")})
  @OrderBy("id ASC")
  private List<Authority> authorities;
}
