package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProdStatChart {

  public static final Map<String, String> NAME_MAP = new HashMap<>();

  static {
    NAME_MAP.put("total", "在制总数");
    NAME_MAP.put("machineTotal", "在制加工");
    NAME_MAP.put("xray", "XRAY");
    NAME_MAP.put("finishedTotal", "在制成品");
    NAME_MAP.put("scrap", "有废品代码");
    NAME_MAP.put("unheat", "没热处理");
    NAME_MAP.put("heated", "热处理后");
    NAME_MAP.put("machiningArht", "进加工的ARHT");
    NAME_MAP.put("unmachineArht", "没进加工的ARHT");

    NAME_MAP.put("whlCast", "浇注车轮");
    NAME_MAP.put("tlInPit", "进缓冷桶");
    NAME_MAP.put("tlHt", "热处理车轮");
    NAME_MAP.put("preInsp", "预检车轮");
    NAME_MAP.put("finalInsp", "终检车轮");
    NAME_MAP.put("machine", "加工");
    NAME_MAP.put("toDock", "成品入库");
    NAME_MAP.put("finScrap", "确认报废");
    NAME_MAP.put("shipped", "发运");
  }
  private List<ProdStatByDay> polylineChart; // 折线图
  private List<Map<String, String>> barChart; // 柱状图
  private String barTotal; // 柱状图合计
  private List<Map<String, String>> sectorChart; // 饼图
  private String sectorTotal; // 饼图合计
}
