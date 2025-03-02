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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String name;
  private String depId;
  private String explain;
  private Integer enabled;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  @Builder.Default
  private Date createTime = new Date();

  private String memo;
}
