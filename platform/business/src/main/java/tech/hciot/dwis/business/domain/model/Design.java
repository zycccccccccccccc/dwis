package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
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
public class Design {

    public static final Integer IS_INTERNAL_YES = 1; // internal字段为1表示国内轮

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String design;
    @Builder.Default
    private String baseDesign = "";
    private String typeKxsj;
    private String steelClass;
    private Integer balanceCheck;
    private Integer internal;
    private String drawingNo;
    private String approbationNo;
    private BigDecimal weight;
    private String spec;
    private String transferRecordNo;

    @Builder.Default
    private Integer enabled = 1;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Builder.Default
    private Date createTime = new Date();
    private String memo;
}
