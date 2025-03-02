package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExportService;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;

@RestController
@RequestMapping(value = "/controlledrecord")
@Api(tags = "受控记录导出业务")
public class ControlledRecordExportController {

  @Autowired
  private ControlledRecordExportService controlledRecordExportService;

  @GetMapping(value = "/tapsheet")
  @ApiOperation(value = "电炉出钢单")
  @Log(name = "电炉出钢单")
  @PreAuthorize("isAuthenticated()")
  public void tapSheet(@RequestParam String date, @RequestParam Integer tapNo, HttpServletResponse response) {
    controlledRecordExportService.export("tapsheet", date, tapNo, null, null, null, response);
  }

  @GetMapping(value = "/pourguidance")
  @ApiOperation(value = "浇注开箱生产总记录(浇注指导)")
  @Log(name = "浇注开箱生产总记录(浇注指导)")
  @PreAuthorize("isAuthenticated()")
  public void pourGuidance(@RequestParam String date, @RequestParam Integer tapNo, HttpServletResponse response) {
    controlledRecordExportService.export("pourguidance", date, tapNo, null, null, null, response);
  }

  @GetMapping(value = "/heat")
  @ApiOperation(value = "热处理生产统计")
  @Log(name = "热处理生产统计")
  @PreAuthorize("isAuthenticated()")
  public void heat(@RequestParam String date, @RequestParam Integer heatLine, HttpServletResponse response) {
    controlledRecordExportService.export("heat", date, null, heatLine, null, null, response);
  }

  @GetMapping(value = "/graphite")
  @ApiOperation(value = "石墨型加工记录")
  @Log(name = "石墨型加工记录")
  @PreAuthorize("isAuthenticated()")
  public void graphite(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("graphite", date, null, null, null, null, response);
  }

  @GetMapping(value = "/jmachine")
  @ApiOperation(value = "基面及定位面加工记录")
  @Log(name = "基面及定位面加工记录")
  @PreAuthorize("isAuthenticated()")
  public void jmachine(@RequestParam String date, @RequestParam Integer machineNo, @RequestParam String opeId,
      HttpServletResponse response) {
    controlledRecordExportService.export("jmachine", date, null, null, machineNo, opeId, response);
  }

  @GetMapping(value = "/tmachine")
  @ApiOperation(value = "踏面加工记录")
  @Log(name = "踏面加工记录")
  @PreAuthorize("isAuthenticated()")
  public void tmachine(@RequestParam String date, @RequestParam Integer machineNo, @RequestParam String opeId,
      HttpServletResponse response) {
    controlledRecordExportService.export("tmachine", date, null, null, machineNo, opeId, response);
  }

  @GetMapping(value = "/kmachine")
  @ApiOperation(value = "镗孔加工记录")
  @Log(name = "镗孔加工记录")
  @PreAuthorize("isAuthenticated()")
  public void kmachine(@RequestParam String date, @RequestParam Integer machineNo, @RequestParam String opeId,
      HttpServletResponse response) {
    controlledRecordExportService.export("kmachine", date, null, null, machineNo, opeId, response);
  }

  @GetMapping(value = "/qmachine")
  @ApiOperation(value = "车轮去重记录")
  @Log(name = "车轮去重记录")
  @PreAuthorize("isAuthenticated()")
  public void qmachine(@RequestParam String date, @RequestParam Integer machineNo, @RequestParam String opeId,
      HttpServletResponse response) {
    controlledRecordExportService.export("qmachine", date, null, null, machineNo, opeId, response);
  }

  @GetMapping(value = "/shottest")
  @ApiOperation(value = "抛丸试验记录表")
  @Log(name = "抛丸试验记录表")
  @PreAuthorize("isAuthenticated()")
  public void shotTest(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("shottest", date, null, null, null, null, response);
  }

  @GetMapping(value = "/hbtest")
  @ApiOperation(value = "布氏硬度试验记录")
  @Log(name = "布氏硬度试验记录")
  @PreAuthorize("isAuthenticated()")
  public void hbTest(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("hbtest", date, null, null, null, null, response);
  }

  @GetMapping(value = "/wheeldev")
  @ApiOperation(value = "车轮尺寸偏差检查记录")
  @Log(name = "车轮尺寸偏差检查记录")
  @PreAuthorize("isAuthenticated()")
  public void wheelDev(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("wheeldev", date, null, null, null, null, response);
  }

  @GetMapping(value = "/threehb")
  @ApiOperation(value = "轮辋三点硬度检测")
  @Log(name = "轮辋三点硬度检测")
  @PreAuthorize("isAuthenticated()")
  public void threeHb(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("threehb", date, null, null, null, null, response);
  }

  @GetMapping(value = "/tape")
  @ApiOperation(value = "带尺记录")
  @Log(name = "带尺记录")
  @PreAuthorize("isAuthenticated()")
  public void tape(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("tape", date, null, null, null, null, response);
  }

  @GetMapping(value = "/ut")
  @ApiOperation(value = "相控试验记录")
  @Log(name = "相控试验记录")
  @PreAuthorize("isAuthenticated()")
  public void ut(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("ut", date, null, null, null, null, response);
  }

  @GetMapping(value = "/mt")
  @ApiOperation(value = "磁探试验记录")
  @Log(name = "磁探试验记录")
  @PreAuthorize("isAuthenticated()")
  public void mt(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("mt", date, null, null, null, null, response);
  }

  @GetMapping(value = "/balance")
  @ApiOperation(value = "平衡机工作记录表")
  @Log(name = "平衡机工作记录表")
  @PreAuthorize("isAuthenticated()")
  public void balance(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("balance", date, null, null, null, null, response);
  }

  @GetMapping(value = "/chemistry")
  @ApiOperation(value = "浇注化学成分分析")
  @Log(name = "浇注化学成分分析")
  @PreAuthorize("isAuthenticated()")
  public void chemistry(@RequestParam String date, HttpServletResponse response) {
    controlledRecordExportService.export("chemistry", date, null, null, null, null, response);
  }
}
