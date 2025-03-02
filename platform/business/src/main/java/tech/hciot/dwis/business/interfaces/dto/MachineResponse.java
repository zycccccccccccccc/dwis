package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.hciot.dwis.business.domain.model.MachiningCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineResponse {

  private Integer wheelId;
  private String design;
  private String wheelSerial;
  private String reworkCode;
  private String scrapCode;
  private String testCode;
  private String holdCode;
  private Integer balanceV;
  private Integer balanceA;
  private Integer balanceS;
  private Integer jS1;
  private Integer jS2;
  private BigDecimal tS1;
  private Integer tS2;
  private Integer kS1;
  private Integer kS2;
  private Integer wS1;
  private Integer wS2;
  private Integer jCounts;
  private Integer kCounts;
  private Integer tCounts;
  private Integer qCounts;
  private Integer wCounts;

  // 是否返修车轮
  @Builder.Default
  private Integer rework = 0;

  // 在镗孔页面，根据轮号动态加载S2下拉框
  private List<String> machiningCodeList;
}
