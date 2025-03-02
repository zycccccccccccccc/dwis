package tech.hciot.dwis.business.interfaces.api.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.report.TechQAReportService;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.*;

@RestController
@RequestMapping(value = "/report/techqa")
@Api(tags = "技术质量报表业务")
public class TechQAReportController {

  @Autowired
  private ReportAssembler assembler;

  @Autowired private TechQAReportService techQAReportService;

  @GetMapping(value = "total-stat", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "技术质量业务查询-汇总统计")
  @PreAuthorize("isAuthenticated()")
  public QAStat totalStat(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String ladleNo,
      @RequestParam(required = false) List<String> testCode,
      @RequestParam(required = false) List<String> scrapCode,
      @RequestParam(required = false) Integer beginTap,
      @RequestParam(required = false) Integer endTap,
      @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    if (StringUtils.isNotBlank(ladleNo)) {
      parameterMap.put("ladleNo", ladleNo);
    }
    if (CollectionUtils.isNotEmpty(testCode)) {
      parameterMap.put("testCode", testCode);
    }
    if (CollectionUtils.isNotEmpty(scrapCode)) {
      parameterMap.put("scrapCode", scrapCode);
    }
    if(beginTap != null) {
      parameterMap.put("beginTap", beginTap);
    }
    if(endTap != null) {
      parameterMap.put("endTap", endTap);
    }
    return techQAReportService.totalStat(parameterMap);
  }

  @GetMapping(value = "day-month/detail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.1 技术质量业务查询-按天汇总-按月-详情(报表)")
  @PreAuthorize("isAuthenticated()")
  public JSON statByMonthDetail(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_MONTH);
  }

  @GetMapping(value = "day-month/chart", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.1 技术质量业务查询-按天汇总-详情(图表)")
  @PreAuthorize("isAuthenticated()")
  public List<QAStat> statByMonthChart(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.statByMonthChart(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "day-pour-leader", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.2 技术质量业务查询-按天汇总-浇注工长")
  @PreAuthorize("isAuthenticated()")
  public JSON statByPourLeader(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_POUR_LEADER);
  }

  @GetMapping(value = "day-pour-leader/chart", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.2 技术质量业务查询-按天汇总-浇注工长-图表")
  @PreAuthorize("isAuthenticated()")
  public JSON statByPourLeaderChart(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.statChart(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_POUR_LEADER);
  }

  @GetMapping(value = "day-model", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.3 技术质量业务查询-按天汇总-造型工长")
  @PreAuthorize("isAuthenticated()")
  public JSON statByModel(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_MODEL);
  }

  @GetMapping(value = "day-modi", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.4 技术质量业务查询-按天汇总-修包工")
  @PreAuthorize("isAuthenticated()")
  public JSON statByModi(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_MODI);
  }

  @GetMapping(value = "day-furnace", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.5 技术质量业务查询-按天汇总-炉长")
  @PreAuthorize("isAuthenticated()")
  public JSON statByFurnace(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_FURNACE);
  }

  @GetMapping(value = "day-temp", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.6 技术质量业务查询-按天汇总-浇注温度")
  @PreAuthorize("isAuthenticated()")
  public JSON statByTemp(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_TEMP);
  }

  @GetMapping(value = "day-xh", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.7 技术质量业务查询-按天汇总-生产线")
  @PreAuthorize("isAuthenticated()")
  public JSON statByXh(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_XH);
  }

  @GetMapping(value = "day-ladle-order", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.8 技术质量业务查询-按天汇总-小包浇注顺序")
  @PreAuthorize("isAuthenticated()")
  public JSON statByLadleOrder(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_LADLE_ORDER);
  }

  @GetMapping(value = "day-furnace-no", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.9 技术质量业务查询-按天汇总-电炉号")
  @PreAuthorize("isAuthenticated()")
  public JSON statByFurnaceNo(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_FURNACE_NO);
  }

  @GetMapping(value = "day-test-code", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.10 技术质量业务查询-按天汇总-试验码")
  @PreAuthorize("isAuthenticated()")
  public JSON statByTestCode(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam List<String> testCode,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    parameterMap.put("testCode", testCode);
    return techQAReportService.stat(parameterMap, TechQAConstant.SQL_TEMPLATE_TEST_CODE);
  }

  @GetMapping(value = "day-tap-seq", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.11 技术质量业务查询-按天汇总-出钢号")
  @PreAuthorize("isAuthenticated()")
  public JSON statByTapSeq(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) Integer beginTap,
    @RequestParam(required = false) Integer endTap,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    if(beginTap != null) {
      parameterMap.put("beginTap", beginTap);
    }
    if(endTap != null) {
      parameterMap.put("endTap", endTap);
    }
    return techQAReportService.stat(parameterMap, TechQAConstant.SQL_TEMPLATE_TAP_SEQ);
  }

  @GetMapping(value = "day-ladle-seq", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.12 技术质量业务查询-按天汇总-小包序号")
  @PreAuthorize("isAuthenticated()")
  public JSON statByLadleSeq(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_LADLE_SEQ);
  }

  @GetMapping(value = "day-single-wheel-detail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.13 技术质量业务查询-按天汇总-单日轮号明细")
  @PreAuthorize("isAuthenticated()")
  public List<SingleWheelDetail> singleWheelDetail(
    @RequestParam String beginDate) {
    return techQAReportService.singleWheelDetail(beginDate);
  }

  @GetMapping(value = "project-scrap-code", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.1 技术质量业务查询-按项目-废品代码")
  @PreAuthorize("isAuthenticated()")
  public QAStat statByProjectScrapCode(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> scrapCode,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    if (!CollectionUtils.isEmpty(scrapCode)) {
      parameterMap.put("scrapCode", scrapCode);
    }
    return techQAReportService.statByProjectScrapCode(parameterMap);
  }

  @GetMapping(value = "project-heat-order", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.2 技术质量业务查询-按项目-大包浇注顺序")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectHeatOrder(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_HEAT_ORDER);
  }

  @GetMapping(value = "project-pour-leader", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.3 技术质量业务查询-按项目-浇注工长")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectPourLeader(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_POUR_LEADER);
  }

  @GetMapping(value = "project-model", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.4 技术质量业务查询-按项目-造型工长")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectModel(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_MODEL);
  }

  @GetMapping(value = "project-furnace", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.5 技术质量业务查询-按项目-炉长")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectFurnace(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_FURNACE);
  }

  @GetMapping(value = "project-temp", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.6 技术质量业务查询-按项目-浇注温度")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectTemp(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_TEMP);
  }

  @GetMapping(value = "project-xh", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.7 技术质量业务查询-按项目-生产线")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectXh(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_XH);
  }

  @GetMapping(value = "project-furnace-no", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.8 技术质量业务查询-按项目-电炉号")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectFurnaceNo(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_FURNACE_NO);
  }

  @GetMapping(value = "project-test-code", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.9 技术质量业务查询-按项目-试验码")
  @PreAuthorize("isAuthenticated()")
  public QAStat statByProjectTestCode(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam List<String> testCode,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    parameterMap.put("testCode", testCode);
    return techQAReportService.statByProjectTestCode(parameterMap);
  }

  @GetMapping(value = "project-tap-seq", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.10 技术质量业务查询-按天汇总-出钢号")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectTapSeq(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) Integer beginTap,
    @RequestParam(required = false) Integer endTap,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    if(beginTap != null) {
      parameterMap.put("beginTap", beginTap);
    }
    if(endTap != null) {
      parameterMap.put("endTap", endTap);
    }
    return techQAReportService.stat(parameterMap, TechQAConstant.SQL_TEMPLATE_PROJECT_TAP_SEQ);
  }

  @GetMapping(value = "project-ladle-seq", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.11 技术质量业务查询-按天汇总-小包序号")
  @PreAuthorize("isAuthenticated()")
  public JSON statByProjectLadleSeq(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.stat(assembler.parameterMap(beginDate, endDate, design),
      TechQAConstant.SQL_TEMPLATE_PROJECT_LADLE_SEQ);
  }

  @GetMapping(value = "graphite-scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3 石墨废品")
  @PreAuthorize("isAuthenticated()")
  public JSONObject graphiteScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return techQAReportService.graphiteScrap(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "ladle-no", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4 技术质量业务查询-底注包废品")
  @PreAuthorize("isAuthenticated()")
  public JSON statByLadleNo(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam String ladleNo,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    parameterMap.put("ladleNo", ladleNo);
    return techQAReportService.stat(parameterMap, TechQAConstant.SQL_TEMPLATE_LADLE_NO);
  }

  @GetMapping(value = "single-scrap/chart", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5 技术质量业务查询-单项废品")
  @PreAuthorize("isAuthenticated()")
  public SingleScrapChart singleScrapChart(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> scrapCode) {
    return techQAReportService.singleScrapChart(beginDate, endDate, scrapCode);
  }

  @GetMapping(value = "machine-scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "6 机加返工代码查废品")
  @PreAuthorize("isAuthenticated()")
  public List<MachineScrap> machineScrap(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam List<String> reworkCode) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    parameterMap.put("reworkCode", reworkCode);
    return techQAReportService.machineScrap(parameterMap);
  }

  @GetMapping(value = "final-check-scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "7 终检时间查废品")
  @PreAuthorize("isAuthenticated()")
  public List<FinalCheckScrap> finalCheckScrap(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam List<String> scrapCode) {
    String endDateNextDay = StandardTimeUtil.nextDay(endDate);
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDateNextDay, null);
    if (!CollectionUtils.isEmpty(scrapCode)) {
      parameterMap.put("scrapCode", scrapCode);
    }
    return techQAReportService.finalCheckScrap(parameterMap);
  }

  @GetMapping(value = "angle-unbalance", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "8 角度不平衡")
  @PreAuthorize("isAuthenticated()")
  public List<AngleUnbalance> angleUnbalance(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam List<String> holdCode) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    parameterMap.put("holdCode", holdCode);
    return techQAReportService.angleUnbalance(parameterMap);
  }

  @GetMapping(value = "machine-unbalance", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "9 机床不平衡")
  @PreAuthorize("isAuthenticated()")
  public List<MachineUnbalance> machineUnbalance(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam List<String> holdCode) {
    String endDateNextDay = StandardTimeUtil.nextDay(endDate);
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDateNextDay, null);
    parameterMap.put("holdCode", holdCode);
    return techQAReportService.machineUnbalance(parameterMap);
  }

  @GetMapping(value = "scrap-44as", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "10 44AS")
  @PreAuthorize("isAuthenticated()")
  public List<Scrap44AS> scrap44AS(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> scrapCode) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    parameterMap.put("scrapCode", scrapCode);
    return techQAReportService.scrap44AS(parameterMap);
  }

  @GetMapping(value = "mold-age-scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "11 石墨模龄废品统计")
  @PreAuthorize("isAuthenticated()")
  public MoldAgeScrap moldAgeScrap(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    return techQAReportService.moldAgeScrap(parameterMap);
  }

  @GetMapping(value = "vibrate-wheel", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "12 振轮次数")
  @PreAuthorize("isAuthenticated()")
  public List<VibrateWheel> vibrateWheels(
          @RequestParam String beginDate,
          @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    return techQAReportService.vibrateWheels(parameterMap);
  }

  @GetMapping(value = "fur-query-res", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "13 电炉/茶壶包/底注包使用次数")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Integer> furQueryRes(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          @RequestParam Integer furType,
          @RequestParam(required = false) Integer furNo,
          @RequestParam(required = false) Integer teaBagNo,
          @RequestParam(required = false) Integer ladleBagNo) {
    return techQAReportService.furQueryRes(beginDate, endDate, furType, furNo, teaBagNo, ladleBagNo);
  }

  @GetMapping(value = "dec-query-res", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "14 电炉溶清/脱碳量查询")
  @PreAuthorize("isAuthenticated()")
  public List<DecarbonChartData> decQueryRes(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          @RequestParam(required = false) String pourLeaderId,
          @RequestParam(required = false) Integer furNo,
          @RequestParam(required = false) String furnaceId) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    if (!StringUtils.isEmpty(pourLeaderId)) { parameterMap.put("pourLeaderId", pourLeaderId); }
    if (furNo != null) { parameterMap.put("furNo", furNo); }
    if (!StringUtils.isEmpty(furnaceId)) { parameterMap.put("furnaceId", furnaceId); }
    return techQAReportService.decQueryRes(parameterMap);
  }

  @GetMapping(value = "tap-seq-list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "出钢号列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> tapSeqList() {
    return techQAReportService.tapSeqList();
  }
}
