package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import tech.hciot.dwis.business.application.MagneticTestService;
import tech.hciot.dwis.business.domain.model.MtTestRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/magnetic/test")
@Api(tags = "磁探开班试验记录")
public class MagneticTestController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private MagneticTestService magneticTestService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询磁探开班试验记录列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<MtTestRecord> find(
      @RequestParam String inspectorId,
      @RequestParam String shiftNo,
      @RequestParam(required = false) String beginDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<MtTestRecord> page = magneticTestService.find(inspectorId, shiftNo, beginDate, endDate, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/batchno/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "批次下拉框")
  public List<String> findBatchNoList(@RequestParam(required = false, defaultValue = "50") Integer limit) {
    return magneticTestService.findBatchNoList(limit);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加试验记录")
  @Log(name = "添加试验记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request MtTestRecord request) {
    return magneticTestService.add(request);
  }
}
