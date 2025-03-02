package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.ChemistryDetailService;
import tech.hciot.dwis.business.domain.model.ChemistryDetail;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/lab")
@Api(tags = "化验室业务")
public class LabController {

  @Autowired
  private ChemistryDetailService chemistryDetailService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询化学成分数据")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ChemistryDetail> findLab(
      @RequestParam(required = false) String furnaceSeq,
      @RequestParam(required = false) String sampleNo,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(chemistryDetailService.findLab(furnaceSeq, sampleNo, currentPage, pageSize));
  }

  @GetMapping(value = "/newest", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询最新的实验室化学成分")
  @PreAuthorize("isAuthenticated()")
  public ChemistryDetail findNewest(@RequestParam Integer furnaceNo, @RequestParam String location) {
    return chemistryDetailService.findNewest(furnaceNo, location);
  }

  @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "按炉号和炉次查询历史记录")
  @PreAuthorize("isAuthenticated()")
  public List<ChemistryDetail> findHistory(@RequestParam String furnaceSeq, @RequestParam String location) {
    return chemistryDetailService.findHistory(furnaceSeq, location);
  }

  @GetMapping(value = "/repeat-record", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询炉号和样号重复的记录")
  @PreAuthorize("isAuthenticated()")
  public ChemistryDetail findRepeatRecord(@RequestParam Integer id) {
    return chemistryDetailService.findRepeatRecord(id);
  }

  @GetMapping(value = "/furnace", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "熔炼加料页面的化学成分")
  @PreAuthorize("isAuthenticated()")
  public List<ChemistryDetail> findLabForFurnace(@RequestParam Integer furnaceNo,
                                                 @RequestParam Integer furnaceSeq,
                                                 @RequestParam String year) {
    return chemistryDetailService.findLabForFurnace(furnaceNo, furnaceSeq, year);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加实验室化学成分")
  @Log(name = "添加实验室化学成分", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public ChemistryDetail add(@RequestBody @Request ChemistryDetail request) {
    return chemistryDetailService.add(request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改实验室化学成分")
  @Log(name = "修改实验室化学成分", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable Integer id,
                     @RequestBody @Request ChemistryDetail request, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    request.setOpreId(operatorId);
    chemistryDetailService.modify(id, request);
  }

  @GetMapping(value = "/status")
  @ApiOperation(value = "查看本地化验室程序运行状态")
  @PreAuthorize("isAuthenticated()")
  public Integer labStatus() {
    return chemistryDetailService.labStatus();
  }

  @RequestMapping(value = "/sse", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ApiOperation(value = "监听本地化验室消息推送")
  @PreAuthorize("permitAll()")
  public SseEmitter sse() {
    final SseEmitter emitter = new SseEmitter(-1L);
    chemistryDetailService.addSseEmitters(emitter);
    return emitter;
  }

  @PostMapping(value = "/keepalive")
  @PreAuthorize("isAuthenticated()")
  public void keepAlive() {
    chemistryDetailService.keepAlive();
  }
}
