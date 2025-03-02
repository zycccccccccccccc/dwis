package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NotifyStatus {

  public static Integer TYPE_NOTIFICATION = 1;
  public static Integer TYPE_TECHNOLIGY_DOCUMENT = 2;

  public static Integer STATUS_UNREAD = 1;
  public static Integer STATUS_READED = 2;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private Integer notifyType;
  private String accountId;
  private Integer notifyId;

  @Builder.Default
  private Integer readStatus = STATUS_UNREAD;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();
}
