package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import tech.hciot.dwis.business.application.ShotTestService;
import tech.hciot.dwis.business.domain.model.ShotTestRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/shot/test")
@Api(tags = "抛丸开班检测")
public class ShotTestController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private ShotTestService shotTestService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询抛丸开班试验记录列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ShotTestRecord> find(
      @RequestParam String inspectorId,
      @RequestParam String shiftNo,
      @RequestParam(required = false) String beginDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<ShotTestRecord> page = shotTestService.find(inspectorId, shiftNo, beginDate, endDate, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加抛丸试验记录")
  @Log(name = "添加抛丸试验记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request ShotTestRecord request) {
    return shotTestService.add(request);
  }


  @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改抛丸试验记录")
  @Log(name = "修改抛丸试验记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
                     @Validated @RequestBody @Request ShotTestRecord request) {
    shotTestService.modify(request);
  }
}
