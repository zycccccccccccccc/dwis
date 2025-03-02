package tech.hciot.dwis.business.domain.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "authority")
@Data
public class AuthorityTree {

  @Id
  @GeneratedValue(generator = "uuidStrategy")
  @GenericGenerator(name = "uuidStrategy", strategy = "uuid")
  private String id;
  private String authorityName;
  private String descritpion;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_id")
  private List<AuthorityTree> children = new ArrayList<>();
}
