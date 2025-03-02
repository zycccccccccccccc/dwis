package tech.hciot.dwis.lab.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurements {
    @Id
    @Column(name = "[ID]")
    private Integer id;

    @Column(name = "[ID1]")
    private String sampleSeq;

    @Column(name = "[ID3]")
    private String operator;

    @Column(name = "Status")
    private Integer status;
}
