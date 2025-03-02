package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.SampleWheelService;
import tech.hciot.dwis.business.domain.model.SampleWheelRecord;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.SampleWheelRequest;

@RestController
@RequestMapping(value = "/samplewheel")
@Api(tags = "抽检车轮录入业务")
public class SampleWheelController {

  @Autowired
  private SampleWheelService sampleWheelService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "抽检车轮录入数据查询")
  public PageDataResponse<SampleWheelRecord> find(
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(sampleWheelService.find(inspectorId, currentPage, pageSize));
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新增抽检车轮录入数据")
  public void add(@Validated @RequestBody @Request SampleWheelRequest sampleWheelRequest) {
    sampleWheelService.add(sampleWheelRequest.convert2Model());
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "删除抽检车轮录入数据")
  public void delete(@PathVariable Integer id) {
    sampleWheelService.delete(id);
  }
}
