package tech.hciot.dwis.business.interfaces.api.report;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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
import tech.hciot.dwis.business.application.report.techqa.TechQAExportService;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByFurnace;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByFurnaceNo;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByLadleNo;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByLadleOrder;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByLadleSeq;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByModel;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByModi;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByMonth;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByPourLeader;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectFurnace;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectFurnaceNo;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectHeatOrder;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectLadleSeq;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectModel;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectPourLeader;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectTapSeq;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectTemp;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByProjectXh;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByTapSeq;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByTemp;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByTestCode;
import tech.hciot.dwis.business.application.report.techqa.configure.StatByXh;

@RestController
@RequestMapping(value = "/report/techqa")
@Api(tags = "技术质量报表业务")
public class TechQAReportExportController {

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private TechQAExportService techQAExportService;

  @GetMapping(value = "day-month/detail/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.1 技术质量业务查询-按天汇总-按月-详情(报表)")
  @PreAuthorize("isAuthenticated()")
  public void statByMonthDetail(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export1Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByMonth());
  }

  @GetMapping(value = "day-pour-leader/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.2 技术质量业务查询-按天汇总-浇注工长")
  @PreAuthorize("isAuthenticated()")
  public void statByPourLeader(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByPourLeader());
  }

  @GetMapping(value = "day-model/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.3 技术质量业务查询-按天汇总-造型工长")
  @PreAuthorize("isAuthenticated()")
  public void statByModel(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.statByModel(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByModel());
  }

  @GetMapping(value = "day-modi/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.4 技术质量业务查询-按天汇总-修包工")
  @PreAuthorize("isAuthenticated()")
  public void statByModi(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByModi());
  }

  @GetMapping(value = "day-furnace/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.5 技术质量业务查询-按天汇总-炉长")
  @PreAuthorize("isAuthenticated()")
  public void statByFurnace(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByFurnace());
  }

  @GetMapping(value = "day-temp/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.6 技术质量业务查询-按天汇总-浇注温度")
  @PreAuthorize("isAuthenticated()")
  public void statByTemp(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByTemp());
  }

  @GetMapping(value = "day-xh/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.7 技术质量业务查询-按天汇总-生产线")
  @PreAuthorize("isAuthenticated()")
  public void statByXh(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByXh());
  }

  @GetMapping(value = "day-ladle-order/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.8 技术质量业务查询-按天汇总-小包浇注顺序")
  @PreAuthorize("isAuthenticated()")
  public void statByLadleOrder(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.export1Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByLadleOrder());
  }

  @GetMapping(value = "day-furnace-no/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.9 技术质量业务查询-按天汇总-电炉号")
  @PreAuthorize("isAuthenticated()")
  public void statByFurnaceNo(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByFurnaceNo());
  }

  @GetMapping(value = "day-test-code/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.10 技术质量业务查询-按天汇总-试验码")
  @PreAuthorize("isAuthenticated()")
  public void statByTestCode(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam List<String> testCode,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    parameterMap.put("testCode", testCode);
    techQAExportService.statByTestCode(parameterMap, response, new StatByTestCode());
  }

  @GetMapping(value = "day-tap-seq/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.11 技术质量业务查询-按天汇总-出钢号")
  @PreAuthorize("isAuthenticated()")
  public void statByTapSeq(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) Integer beginTap,
      @RequestParam(required = false) Integer endTap,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    if(beginTap != null) {
      parameterMap.put("beginTap", beginTap);
    }
    if(endTap != null) {
      parameterMap.put("endTap", endTap);
    }
    techQAExportService.export2Level(parameterMap, response, new StatByTapSeq());
  }

  @GetMapping(value = "day-ladle-seq/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.12 技术质量业务查询-按天汇总-小包序号")
  @PreAuthorize("isAuthenticated()")
  public void statByLadleSeq(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByLadleSeq());
  }

  @GetMapping(value = "day-single-wheel-detail/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "1.13 技术质量业务查询-按天汇总-单日轮号明细")
  @PreAuthorize("isAuthenticated()")
  public void singleWheelDetail(
      @RequestParam String beginDate,
      HttpServletResponse response) {
    techQAExportService.singleWheelDetail(beginDate, response);
  }

  @GetMapping(value = "project-scrap-code/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.1 技术质量业务查询-按项目-废品代码")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectScrapCode(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> scrapCode,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    if (!CollectionUtils.isEmpty(scrapCode)) {
      parameterMap.put("scrapCode", scrapCode);
    }
    techQAExportService.statByProjectScrapCode(parameterMap, response);
  }

  @GetMapping(value = "project-heat-order/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.2 技术质量业务查询-按项目-大包浇注顺序")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectHeatOrder(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectHeatOrder());
  }

  @GetMapping(value = "project-pour-leader/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.3 技术质量业务查询-按项目-浇注工长")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectPourLeader(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectPourLeader());
  }

  @GetMapping(value = "project-model/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.4 技术质量业务查询-按项目-造型工长")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectModel(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export1Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectModel());
  }

  @GetMapping(value = "project-furnace/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.5 技术质量业务查询-按项目-炉长")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectFurnace(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectFurnace());
  }

  @GetMapping(value = "project-temp/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.6 技术质量业务查询-按项目-浇注温度")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectTemp(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectTemp());
  }

  @GetMapping(value = "project-xh/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.7 技术质量业务查询-按项目-生产线")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectXh(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectXh());
  }

  @GetMapping(value = "project-furnace-no/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.8 技术质量业务查询-按项目-电炉号")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectFurnaceNo(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export2Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectFurnaceNo());
  }

  @GetMapping(value = "project-test-code/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.9 技术质量业务查询-按项目-试验码")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectTestCode(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> testCode,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    parameterMap.put("testCode", testCode);
    techQAExportService.statByProjectTestCode(parameterMap, response);
  }

  @GetMapping(value = "project-tap-seq/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.10 技术质量业务查询-按项目汇总-出钢号")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectTapSeq(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) Integer beginTap,
      @RequestParam(required = false) Integer endTap,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    if(beginTap != null) {
      parameterMap.put("beginTap", beginTap);
    }
    if(endTap != null) {
      parameterMap.put("endTap", endTap);
    }
    techQAExportService.export1Level(parameterMap, response, new StatByProjectTapSeq());
  }

  @GetMapping(value = "project-ladle-seq/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.11 技术质量业务查询-按项目汇总-小包序号")
  @PreAuthorize("isAuthenticated()")
  public void statByProjectLadleSeq(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.export1Level(assembler.parameterMap(beginDate, endDate, design),
      response, new StatByProjectLadleSeq());
  }

  @GetMapping(value = "graphite-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3 石墨废品")
  @PreAuthorize("isAuthenticated()")
  public void graphiteScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    techQAExportService.graphiteScrap(assembler.parameterMap(beginDate, endDate, design), response);
  }

  @GetMapping(value = "ladle-no/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4 技术质量业务查询-底注包废品")
  @PreAuthorize("isAuthenticated()")
  public void statByLadleNo(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam String ladleNo,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    parameterMap.put("ladleNo", ladleNo);
    techQAExportService.export1Level(parameterMap, response, new StatByLadleNo());
  }

  @GetMapping(value = "single-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5 技术质量业务查询-单项废品")
  @PreAuthorize("isAuthenticated()")
  public void singleScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      @RequestParam(required = false) List<String> scrapCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    parameterMap.put("scrapCode", scrapCode);
    techQAExportService.singleScrap(parameterMap, response);
  }

  @GetMapping(value = "machine-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "6 机加返工代码查废品")
  @PreAuthorize("isAuthenticated()")
  public void machineScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> reworkCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    parameterMap.put("reworkCode", reworkCode);
    techQAExportService.machineScrap(parameterMap, response);
  }

  @GetMapping(value = "final-check-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "7 终检时间查废品")
  @PreAuthorize("isAuthenticated()")
  public void finalCheckScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> scrapCode,
      HttpServletResponse response) {
    String endDateNextDay = StandardTimeUtil.nextDay(endDate);
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDateNextDay, null);
    if (!CollectionUtils.isEmpty(scrapCode)) {
      parameterMap.put("scrapCode", scrapCode);
    }
    techQAExportService.finalCheckScrap(parameterMap, response);
  }

  @GetMapping(value = "angle-unbalance/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "8 角度不平衡")
  @PreAuthorize("isAuthenticated()")
  public void angleUnbalance(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> holdCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    parameterMap.put("holdCode", holdCode);
    techQAExportService.angleUnbalance(parameterMap, response);
  }

  @GetMapping(value = "machine-unbalance/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "9 机床不平衡")
  @PreAuthorize("isAuthenticated()")
  public void machineUnbalance(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> holdCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    parameterMap.put("holdCode", holdCode);
    techQAExportService.machineUnbalance(parameterMap, response);
  }

  @GetMapping(value = "scrap-44as/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "10 44AS")
  @PreAuthorize("isAuthenticated()")
  public void scrap44AS(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> scrapCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    parameterMap.put("scrapCode", scrapCode);
    techQAExportService.scrap44AS(parameterMap, response);
  }

  @GetMapping(value = "mold-age-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "11 石墨模龄废品统计")
  @PreAuthorize("isAuthenticated()")
  public void moldAgeScrap(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          @RequestParam(required = false) List<String> design,
          HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    techQAExportService.moldAgeScrap(parameterMap, response);
  }

  @GetMapping(value = "vibrate-wheel/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "12 振轮次数")
  @PreAuthorize("isAuthenticated()")
  public void vibrateWheels(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    techQAExportService.vibrateWheel(parameterMap, response);
  }

  @GetMapping(value = "dec-query-res/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "14 电炉溶清&脱碳查询")
  @PreAuthorize("isAuthenticated()")
  public void decQueryRes(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          @RequestParam(required = false) String pourLeaderId,
          @RequestParam(required = false) Integer furNo,
          @RequestParam(required = false) String furnaceId,
          HttpServletResponse response) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    if (!StringUtils.isEmpty(pourLeaderId)) { parameterMap.put("pourLeaderId", pourLeaderId); }
    if (!StringUtils.isEmpty(furnaceId)) { parameterMap.put("furnaceId", furnaceId); }
    if (furNo != null) { parameterMap.put("furNo", furNo); }
    techQAExportService.decarbon(parameterMap, response);
  }
}
