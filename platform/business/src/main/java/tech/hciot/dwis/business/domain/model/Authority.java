package tech.hciot.dwis.business.domain.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class Authority {

  @Id
  @GeneratedValue(generator = "uuidStrategy")
  @GenericGenerator(name = "uuidStrategy", strategy = "uuid")
  private String id;
  private String authorityName;
  private String descritpion;
}
