package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.Set;
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
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

  public static final Integer STATUS_ENABLED = 0;
  public static final Integer STATUS_DISABLED = 1;

  @Id
  private String id;
  private String username;
  private String password;
  private String mobile;
  private String email;
  private Integer status;
  private Integer loginStatus;
  private String nickname;
  private String location;
  private String teamLeaderId;
  private Integer depId;
  private Integer stationId;
  private String memo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
  @Default
  private Integer enabled = 1;
  @Default
  private Integer isLeader = 0;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "v_account_authority", joinColumns = {@JoinColumn(name = "uid")}
      , inverseJoinColumns = {@JoinColumn(name = "authority_id")})
  @OrderBy("id ASC")
  private Set<Authority> authorities;
}
