package tech.hciot.dwis.business.domain.model;

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
public class LabKeepalive {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Builder.Default
  private Integer id = 1;

  @Builder.Default
  private Long lastKeepaliveTime = System.currentTimeMillis();
}
