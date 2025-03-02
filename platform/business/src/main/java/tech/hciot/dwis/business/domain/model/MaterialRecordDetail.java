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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "v_material_record")
public class MaterialRecordDetail {

  @Id
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
  private String manufacturerName;
}
