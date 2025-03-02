package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WheelRework {
  private String castDate;
  private String castTotal;
  private String preInsp;
  private String good1;
  private String pre1;
  private String pre2;
  private String pre3;
  private String pre4;
  private String fin1;
  private String fin2;
  private String fin3;

  private String prew;
  private String p4;
  private String p5;
  private String p6;
  private String p7;
  private String ph;
  private String poth;

  private String frew;
  private String f4;
  private String f5;
  private String f6;
  private String f7;
  private String fh;
  private String foth;
}
