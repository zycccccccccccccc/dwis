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
import tech.hciot.dwis.business.application.GraphiteFirmService;
import tech.hciot.dwis.business.domain.model.GraphiteFirm;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.MaxGraphiteResponse;

@RestController
@RequestMapping(value = "/graphitefirm")
@Api(tags = "原始石墨信息")
public class GraphiteFirmController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private GraphiteFirmService graphiteFirmService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询原始石墨列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<GraphiteFirm> find (
    @RequestParam(required = false) String graphiteKey,
    @RequestParam(required = false) Integer diameter,
    @RequestParam(required = false) Integer status,
    @RequestParam(required = false) String receiveDate,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<GraphiteFirm> page = graphiteFirmService.find(graphiteKey, diameter, status, receiveDate,
      operatorId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/maxgraphite", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "分组查询最大原始石墨号")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<MaxGraphiteResponse> findMaxGraphite (
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<Map<String, Object>> page = graphiteFirmService.findMaxGraphite(currentPage, pageSize);
    return mgrAssembler.toMaxGraphiteResponse(page);
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询原始石墨号列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> findGraphiteFirmList (@RequestParam(required = false, defaultValue = "") String keyword,
                                            @RequestParam(required = false, defaultValue = "50")Integer limit) {
    return graphiteFirmService.findGraphiteFirmList(keyword, limit);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加原始石墨")
  @Log(name = "添加原始石墨", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request GraphiteFirm request) {
    return graphiteFirmService.add(request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑原始石墨")
  @Log(name = "编辑原始石墨", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
                     @RequestBody @Request GraphiteFirm request) {
    graphiteFirmService.modify(id, request);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除原始石墨")
  @Log(name = "删除原始石墨", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    graphiteFirmService.delete(id);
  }

  @GetMapping(value = "/{graphiteKey}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查看原始石墨详情")
  @PreAuthorize("isAuthenticated()")
  public GraphiteFirm findByGraphiteKey(@PathVariable String graphiteKey) {
    return graphiteFirmService.findByGraphiteKey(graphiteKey);
  }
}
