package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
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
import tech.hciot.dwis.business.application.LowService;
import tech.hciot.dwis.business.domain.model.Heat;
import tech.hciot.dwis.business.domain.model.LowHeatPreworkRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.LowPreworkRequest;

@RestController
@RequestMapping(value = "/low")
@Api(tags = "回火炉低温热处理")
@Slf4j
public class LowController {

  @Autowired
  private LowService lowService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询回火炉低温热处理记录列表")
  @PreAuthorize("isAuthenticated()")
  public List<Heat> find(
      @RequestParam(required = false) String inOperator,
      @RequestParam(required = false) String outOperator,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String inDate,
      @RequestParam(required = false) String outDate) {
    long startTs = System.currentTimeMillis();
    List<Heat> heatList = lowService.find(inOperator, outOperator, wheelSerial, inDate, outDate);
    long endTs = System.currentTimeMillis();
    log.info("回火炉查询heat表耗时{}秒", (endTs - startTs) / 1000);
    return heatList;
  }

  @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存回火炉低温热处理")
  @Log(name = "保存回火炉低温热处理", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void save(@RequestBody @Request Heat heat) { lowService.save(heat);
  }

  @PutMapping(value = "/refresh")
  @ApiOperation(value = "刷新回火炉低温热处理")
  @Log(name = "刷新回火炉低温热处理", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void refreshLow() {
    lowService.refreshLow();
  }

  @PutMapping(value = "/modify/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改高低温热处理数据")
  @Log(name = "修改高低温热处理数据", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyHeat(@PathVariable @Id String id,
                         @RequestBody @Request Heat heat) {
    lowService.modifyHeat(id, heat);
  }

  @PostMapping(value = "/prework", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存热处理回火炉开班信息")
  @Log(name = "保存热处理回火炉开班信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer addPrework(@Validated @RequestBody @Request LowPreworkRequest lowPreworkRequest) {
    return lowService.addPrework(lowPreworkRequest.convert2Model());
  }

  @GetMapping(value = "/prework", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询热处理回火炉开班信息")
  @Log(name = "查询热处理回火炉开班信息")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<LowHeatPreworkRecord> getPrework(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(lowService.getPrework(currentPage, pageSize));
  }

  @PutMapping(value = "/prework/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改热处理回火炉开班信息")
  @Log(name = "修改热处理回火炉开班信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void editPrework(@PathVariable Integer id, @Validated @RequestBody @Request LowPreworkRequest lowPreworkRequest) {
    lowService.editPrework(id, lowPreworkRequest.convert2Model());
  }
}
