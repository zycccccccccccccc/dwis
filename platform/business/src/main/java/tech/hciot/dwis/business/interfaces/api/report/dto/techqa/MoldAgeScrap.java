package tech.hciot.dwis.business.interfaces.api.report.dto.techqa;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoldAgeScrap {
  // 上箱石墨模龄废品详细数据
  private JSONObject copeResult;
  // 下箱石墨模龄废品详细数据
  private JSONObject dragResult;

  // 上箱石墨模龄废品合计数据
  private List<JSONObject> copeTotalList;
  //下箱石墨模龄废品合计数据
  private List<JSONObject> dragTotalList;
}
