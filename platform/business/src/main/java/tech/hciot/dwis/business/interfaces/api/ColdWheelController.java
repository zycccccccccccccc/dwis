package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
import tech.hciot.dwis.business.application.ColdWheelService;
import tech.hciot.dwis.business.domain.model.ColdWheel;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddColdWheelRequest;
import tech.hciot.dwis.business.interfaces.dto.BucketResponse;
import tech.hciot.dwis.business.interfaces.dto.MarkColdWheelRequest;

@RestController
@RequestMapping(value = "/coldwheel")
@Api(tags = "冷割车轮记录")
public class ColdWheelController {

  @Autowired
  private ColdWheelService coldWheelService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询冷割车轮记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ColdWheel> find(
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false) String startDateTime,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(coldWheelService.find(inspectorId, startDateTime, currentPage, pageSize));
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存冷割车轮记录")
  @Log(name = "保存冷割车轮记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void save(@Validated @RequestBody @Request AddColdWheelRequest addColdWheelRequest) {
    coldWheelService.add(addColdWheelRequest.convert2Model());
  }

  @GetMapping(value = "/bucket", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询冷割车轮桶号数据")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<BucketResponse> findBucket(
      @RequestParam(required = false) Integer tapSeq,
      @RequestParam(required = false) String castDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(coldWheelService.findBucket(castDate, tapSeq, currentPage, pageSize));
  }

  @PostMapping(value = "/mark", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "冷割车轮标记")
  @Log(name = "冷割车轮标记", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void mark(@Validated @RequestBody @Request MarkColdWheelRequest markColdWheelRequest) {
    coldWheelService.mark(markColdWheelRequest);
  }
}
