package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmScrapResult {

  private JSONObject total;
  private List<JSONObject> resultList;
}
