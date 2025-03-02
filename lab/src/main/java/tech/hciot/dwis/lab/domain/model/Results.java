package tech.hciot.dwis.lab.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ResultsId.class)
public class Results {

    @Id
    @Column(name = "[Measurement]")
    private Integer measurementId;

    @Id
    @Column(name = "[Component]")
    private String component;

    @Column(name = "[Value_]")
    private BigDecimal value;
}

class ResultsId implements Serializable {
    private Integer measurementId;
    private String component;
}
