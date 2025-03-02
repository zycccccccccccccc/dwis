package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.hciot.dwis.business.infrastructure.IntegerListAttributeConverter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TechnicalDocument {

  public static Integer STATUS_UNPUBLISH = 1;
  public static Integer STATUS_PUBLISHED = 2;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String title;
  private String author;
  @Convert(converter = IntegerListAttributeConverter.class)
  private List<Integer> department;
  private String filename;
  @Lob
  @JsonIgnore
  private byte[] content;
  private Integer publishStatus;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
}
