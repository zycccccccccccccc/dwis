package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import org.apache.commons.lang3.StringUtils;
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
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.CorrectWheelRecordService;
import tech.hciot.dwis.business.domain.model.CorrectWheelRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddCorrectWheelRecord;

@RestController
@RequestMapping(value = "/correctwheel")
@Api(tags = "纠轮操作记录")
public class CorrectWheelRecordController {

  @Autowired
  private CorrectWheelRecordService correctWheelRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询纠轮操作记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<CorrectWheelRecord> find(
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String holdCode,
      @RequestParam(required = false) String reworkCode,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false) String cihenCode,
      @RequestParam(required = false) String formerStockDate,
      @RequestParam(required = false) String formerShippedNo,
      @RequestParam(required = false, defaultValue = "1") Integer recallType,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    return mgrAssembler
        .toPageDataResponse(
            correctWheelRecordService.find(StringUtils.defaultString(inspectorId, operator), wheelSerial, holdCode,
                reworkCode, scrapCode, cihenCode, formerStockDate, formerShippedNo, recallType, currentPage, pageSize));
  }

  @PostMapping(value = "/finish", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存成品纠回记录")
  @Log(name = "保存成品纠回记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void finish(@Validated @RequestBody @Request AddCorrectWheelRecord addCorrectWheelRecord) {
    correctWheelRecordService.finish(addCorrectWheelRecord.convert2Model());
  }

  @PostMapping(value = "/stock", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存入库纠回记录")
  @Log(name = "保存入库纠回记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void stock(@Validated @RequestBody @Request AddCorrectWheelRecord addCorrectWheelRecord) {
    correctWheelRecordService.stock(addCorrectWheelRecord.convert2Model());
  }

  @PostMapping(value = "/return", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存返厂纠回记录")
  @Log(name = "保存返厂纠回记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void returnCorrect(@Validated @RequestBody AddCorrectWheelRecord addCorrectWheelRecord) {
    correctWheelRecordService.returnCorrect(addCorrectWheelRecord.convert2Model());
  }
}
