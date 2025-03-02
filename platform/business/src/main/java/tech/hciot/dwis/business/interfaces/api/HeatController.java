package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.HeatService;
import tech.hciot.dwis.business.domain.model.Heat;
import tech.hciot.dwis.business.domain.model.HeatParams;
import tech.hciot.dwis.business.domain.model.HiHeatPreworkRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.HeatPreworkRequest;

@RestController
@RequestMapping(value = "/heat")
@Api(tags = "热处理信息")
@Slf4j
public class HeatController {

  @Autowired
  private HeatService heatService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询高温热处理记录列表")
  @PreAuthorize("isAuthenticated()")
  public List<Heat> find(
      @RequestParam(required = false) Integer heatLine,
      @RequestParam(required = false) String inOperator,
      @RequestParam(required = false) String outOperator,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String inDate,
      @RequestParam(required = false) String outDate) {
    long startTs = System.currentTimeMillis();
    List<Heat> heatList = heatService.find(heatLine, inOperator, outOperator, wheelSerial, inDate, outDate);
    long endTs = System.currentTimeMillis();
    log.info("环形炉查询heat表耗时{}秒", (endTs - startTs) / 1000);
    return heatList;
  }

  @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存热处理记录")
  @Log(name = "保存热处理记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void save(@RequestBody @Request Heat heat) { heatService.save(heat);
  }

  @PutMapping(value = "/refresh")
  @ApiOperation(value = "刷新数据")
  @Log(name = "刷新数据", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void refreshHeatLine() {
    heatService.refreshHeatLine();
  }

  @PostMapping(value = "/prework", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存热处理环形炉开班信息")
  @Log(name = "保存热处理环形炉开班信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer addPrework(@Validated @RequestBody @Request HeatPreworkRequest heatPreworkRequest) {
    return heatService.addPrework(heatPreworkRequest.convert2Model());
  }

  @GetMapping(value = "/prework", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询热处理环形炉开班信息")
  @Log(name = "查询热处理环形炉开班信息")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<HiHeatPreworkRecord> getPrework(
      @RequestParam Integer furNo,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(heatService.getPrework(furNo, currentPage, pageSize));
  }

  @PutMapping(value = "/prework/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改热处理环形炉开班信息")
  @Log(name = "修改热处理环形炉开班信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void editPrework(@PathVariable Integer id, @Validated @RequestBody @Request HeatPreworkRequest heatPreworkRequest) {
    heatService.editPrework(id, heatPreworkRequest.convert2Model());
  }

  @GetMapping(value = "/procParams", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询工艺参数")
  public Optional<HeatParams> findEnabledParams(@RequestParam(required = true) String type ) {
    return heatService.findProcParams(type);
  }
}
