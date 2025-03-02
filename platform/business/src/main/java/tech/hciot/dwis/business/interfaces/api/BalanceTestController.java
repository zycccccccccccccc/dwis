package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.BalanceTestService;
import tech.hciot.dwis.business.domain.model.BalanceTestRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.TestWheelResponse;

@RestController
@RequestMapping(value = "/balance/test")
@Api(tags = "平衡机开班试验记录")
public class BalanceTestController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private BalanceTestService balanceTestService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询平衡机开班试验记录列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<BalanceTestRecord> find(
    @RequestParam String inspectorId,
    @RequestParam String shiftNo,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<BalanceTestRecord> page = balanceTestService.find(inspectorId, shiftNo, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/wheel/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "轮号下拉框")
  public List<TestWheelResponse> findWheelList(
    @RequestParam(required = false, defaultValue = "") String keyword,
    @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return balanceTestService.findWheelList(keyword, limit);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加平衡机开班试验记录")
  @Log(name = "添加平衡机开班试验记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request BalanceTestRecord request) {
    return balanceTestService.add(request);
  }

}
