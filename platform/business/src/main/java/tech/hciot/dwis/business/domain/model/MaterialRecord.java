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
public class MaterialRecord {

  public static final int STATUS_NEW = 0; // 新创建
  public static final int STATUS_STARTED = 1; // 开始
  public static final int STATUS_SUSPENDED = 2; // 暂停
  public static final int STATUS_STOPPED = 3; // 停止

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String materialName;
  private Integer manufacturerId;
  private Integer dept;
  private String batchNo;
  private String parameter1;
  private String parameter2;
  private String parameter3;
  private String parameter4;
  private String parameter5;
  private String parameter6;
  private String parameter7;
  private String parameter8;
  private String operator;
  private Integer status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date startTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date suspendTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date stopTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  private Integer materialId;
}
