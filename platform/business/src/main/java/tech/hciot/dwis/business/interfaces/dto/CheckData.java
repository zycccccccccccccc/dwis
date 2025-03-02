package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class CheckData {

  private String checkCode;
  private String castDate;
  private String wheelSerial;
  private String design;
  private String heatNo;
  private Integer wheelW;
  private Integer tape;
  private Integer boreSize;
  private Integer brinnelReading;
  private Date lastBalance;
  private Date stockDate;
  private BigDecimal c;
  private BigDecimal si;
  private BigDecimal mn;
  private BigDecimal s;
  private BigDecimal p;
  private BigDecimal al;
  private BigDecimal ni;
  private BigDecimal cr;
  private BigDecimal mo;
  private BigDecimal v;
  private BigDecimal cu;
  private BigDecimal ti;
  private BigDecimal nb;
}
