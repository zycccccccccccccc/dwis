package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.GraphiteRecordService;
import tech.hciot.dwis.business.application.GraphiteService;
import tech.hciot.dwis.business.domain.model.Graphite;
import tech.hciot.dwis.business.domain.model.GraphitePour;
import tech.hciot.dwis.business.domain.model.GraphiteRecord;
import tech.hciot.dwis.business.domain.model.GraphiteScrap;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/graphite")
@Api(tags = "石墨信息")
public class GraphiteController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private GraphiteService graphiteService;

  @Autowired
  private GraphiteRecordService graphiteRecordService;

  @GetMapping(value = "/maxgraphite", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "分组查询最大石墨号")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Map<String, Object>> findMaxGraphite (
      @RequestParam Integer cd,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<Map<String, Object>> page = graphiteService.findMaxGraphite(cd, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询石墨列表")
  @PreAuthorize("isAuthenticated()")
  public List<Graphite> findGraphiteList (
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false) String opType,
      @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return graphiteService.findGraphiteList(keyword, opType, limit);
  }

  @PostMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨加工")
  @Log(name = "石墨加工", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void process(@Validated @RequestBody @Request GraphiteRecord request,
                      Principal principal)
  {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    request.setGraphiteOpeId(operatorId);
    graphiteService.process(request);
  }

  @PostMapping(value = "/rework", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨返修")
  @Log(name = "石墨返修", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void rework(@Validated @RequestBody @Request GraphiteRecord request,
                     Principal principal)
  {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    request.setGraphiteOpeId(operatorId);
    graphiteService.rework(request);
  }

  @PostMapping(value = "/modifyno", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "改石墨号业务")
  @Log(name = "改石墨号业务", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void modifyNo(@Validated @RequestBody @Request GraphiteRecord request,
                       Principal principal)
  {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    request.setGraphiteOpeId(operatorId);
    graphiteService.modifyNo(request);
  }

  @PostMapping(value = "/up", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨上线")
  @Log(name = "石墨上线", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void up(@Validated @RequestBody @Request GraphiteRecord request,
                 Principal principal)
  {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    request.setGraphiteOpeId(operatorId);
    graphiteService.up(request);
  }

  @PostMapping(value = "/down", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨下线")
  @Log(name = "石墨下线", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void down(@Validated @RequestBody @Request GraphiteRecord request,
                   Principal principal)
  {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    request.setGraphiteOpeId(operatorId);
    graphiteService.down(request);
  }

  @PostMapping(value = "/scrap", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨报废")
  @Log(name = "石墨报废", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void scrap(@Validated @RequestBody @Request GraphiteRecord request,
                    Principal principal)
  {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    request.setGraphiteOpeId(operatorId);
    graphiteService.scrap(request);
  }

  @GetMapping(value = "/record", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询石墨加工记录列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<GraphiteRecord> findGraphiteRecord (
    @RequestParam(required = false) String graphiteKey,
    @RequestParam(required = false) String graphite,
    @RequestParam(required = false) String design,
    @RequestParam(required = false) Integer cd,
    @RequestParam(required = false) String reworkCode,
    @RequestParam(required = false) String scrapCode,
    @RequestParam(required = false) String processDate,
    @RequestParam(required = false, defaultValue = "") String opType,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<GraphiteRecord> page = graphiteRecordService.find(graphiteKey, graphite, design, cd, reworkCode,
      scrapCode, processDate, opType, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/record/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨加工记录的石墨号下拉框列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> findGraphiteRecordList (
    @RequestParam(required = false, defaultValue = "") String keyword,
    @RequestParam(required = false) String opType,
    @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return graphiteRecordService.findGraphiteList(keyword, opType, limit);
  }

  @PutMapping(value = "/record/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改石墨")
  @Log(name = "修改石墨", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
                     @RequestBody @Request GraphiteRecord request) {
    graphiteService.modify(id, request);
  }

  @DeleteMapping(value = "/record/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除石墨")
  @Log(name = "删除石墨", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    graphiteService.delete(id);
  }

  @GetMapping(value = "/cdlist", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询上/下箱号列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> findGraphiteCDList(
      @RequestParam(required = false, defaultValue = "1") Integer cdType) {
    return graphiteService.findGraphiteCDList(cdType);
  }

  @GetMapping(value = "/pour", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨浇注查询")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<GraphitePour> getPourInfo(
      @RequestParam(required = false) String graphite,
      @RequestParam(required = false) String beginDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return graphiteService.getPourInfo(graphite, beginDate, endDate, currentPage, pageSize);
  }

  @GetMapping(value = "/scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨报废查询")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<GraphiteScrap> getScrapScrap(
      @RequestParam(required = false) String graphiteKey,
      @RequestParam(required = false) String beginDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<GraphiteScrap> page = graphiteService.getGraphiteScrap(graphiteKey, beginDate, endDate,
      scrapCode, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/processlist", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨加工查询")
  @PreAuthorize("isAuthenticated()")
  public Object processList(
    @RequestParam(required = false) String graphiteKey,
    @RequestParam(required = false) String beginDate,
    @RequestParam(required = false) String endDate,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    if (graphiteKey != null) {
      return mgrAssembler.toPageDataResponse(
        graphiteService.processListForGraphiteProcess(graphiteKey, beginDate, endDate));
    } else {
      return mgrAssembler.toPageDataResponse(
        graphiteService.processListForGraphiteRecord(beginDate, endDate, currentPage, pageSize));
    }
  }

  @GetMapping(value = "/processlist/detail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "石墨加工详情")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<GraphiteRecord> processListDetail (
    @RequestParam(required = false) String graphiteKey,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<GraphiteRecord> page = graphiteRecordService.processListDetail(graphiteKey, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }
}
