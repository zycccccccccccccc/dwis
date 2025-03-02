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

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "account")
public class User {

  public static int USER_STATUS_ENABLE = 0;
  public static int LOGIN_STATUS_FIRST_LOGIN = 0;//用户首次登陆状态
  public static int LOGIN_STATUS_NOT_FIRST_LOGIN = 1;//用户首次登陆状态

  @Id
  @GeneratedValue(generator = "uuidStrategy")
  @GenericGenerator(name = "uuidStrategy", strategy = "uuid")
  private String id;
  private String username;
  private String password;
  private String nickname;
  private String mobile;
  private String email;
  private String avatar;
  private Integer status;
  private String photoUrl;
  private Integer loginStatus;
}
