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
import tech.hciot.dwis.business.application.CihenRecordService;
import tech.hciot.dwis.business.domain.model.CihenRecord;
import tech.hciot.dwis.business.domain.model.CihenRecordPre;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddCihenRecordPreRequest;
import tech.hciot.dwis.business.interfaces.dto.AddCihenRecordRequest;

@RestController
@RequestMapping(value = "/cihen")
@Api(tags = "磁痕检测录入记录")
public class CihenRecordController {

  @Autowired
  private CihenRecordService cihenRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询磁痕检测录入记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<CihenRecord> find(
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String foreCihenCode,
      @RequestParam(required = false) String cihenCode,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(cihenRecordService.find(wheelSerial, foreCihenCode, cihenCode, scrapCode, inspectorId,
        currentPage, pageSize));
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存磁痕检测录入记录")
  @Log(name = "保存磁痕检测录入记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void save(@Validated @RequestBody @Request AddCihenRecordRequest addCihenRecordRequest) {
    cihenRecordService.add(addCihenRecordRequest.convert2Model());
  }

  @GetMapping(value = "/pre", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询磁痕外观检查录入记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<CihenRecordPre> findPre(
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(cihenRecordService.findPre(wheelSerial, inspectorId, currentPage, pageSize));
  }

  @PostMapping(value = "/pre", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存磁痕外观检查录入记录")
  @Log(name = "保存磁痕外观检查录入记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void savePre(@Validated @RequestBody @Request AddCihenRecordPreRequest addCihenRecordPreRequest) {
    cihenRecordService.addPre(addCihenRecordPreRequest.convert2Model());
  }
}
