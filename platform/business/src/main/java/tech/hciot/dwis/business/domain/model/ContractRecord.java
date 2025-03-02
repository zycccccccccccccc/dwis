package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ContractRecord implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String contractNo;
  private String design;
  private String customerId;
  private String operator;
  private Integer contractSum;
  private Integer shippedSum;
  private Integer surplusSum;
  private Integer enabled;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date startDate;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date endDate;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date createTime;
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "customerId", referencedColumnName = "customerId", nullable = false, insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private Customer customer;
}
