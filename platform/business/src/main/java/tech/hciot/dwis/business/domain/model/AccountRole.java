package tech.hciot.dwis.business.domain.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "account_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRole {

  @Id
  @GeneratedValue(generator = "uuidStrategy")
  @GenericGenerator(name = "uuidStrategy", strategy = "uuid")
  private String id;
  private String accountId;
  private String roleId;
}
