package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "v_account_detail")
public class AccountDetail {

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
  private String depName;
  private Integer stationId;
  private String stationName;
  private String memo;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
  private Integer enabled;
  private Integer isLeader;
}
