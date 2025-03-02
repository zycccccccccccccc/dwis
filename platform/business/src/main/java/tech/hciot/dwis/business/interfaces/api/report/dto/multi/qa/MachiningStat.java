package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachiningStat {
  private JSONArray machiningStat;
  private JSONObject xMachiningStat;
}
