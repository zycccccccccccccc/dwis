package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.MaterialRecordService;
import tech.hciot.dwis.business.domain.model.MaterialRecord;
import tech.hciot.dwis.business.domain.model.MaterialRecordDetail;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/material")
@Api(tags = "原材料管理记录")
public class MaterialRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private MaterialRecordService materialRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询原材料管理记录列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<MaterialRecordDetail> find(
      @RequestParam(required = false) String materialName,
      @RequestParam(required = false) String batchNo,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String startTime,
      @RequestParam(required = false) String suspendTime,
      @RequestParam(required = false) String stopTime,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<MaterialRecordDetail> page = materialRecordService.find(materialName, batchNo, status,
      startTime, suspendTime, stopTime, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "原材料名称列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> findMaterialNameList(Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    Integer depId = user.getDepId();
    return materialRecordService.findMaterialNameList(depId);
  }

  @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "导出原材料管理记录列表")
  public void export(
    @RequestParam(required = false) String materialName,
    @RequestParam(required = false) String batchNo,
    @RequestParam(required = false) Integer status,
    @RequestParam(required = false) String startTime,
    @RequestParam(required = false) String suspendTime,
    @RequestParam(required = false) String stopTime,
    HttpServletResponse response) {
    Page<MaterialRecordDetail> page = materialRecordService.find(materialName, batchNo, status,
    startTime, suspendTime, stopTime, 0, 50000);
    materialRecordService.export(page.getContent(), response);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加原材料管理记录")
  @Log(name = "添加原材料管理记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request MaterialRecord request, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    return materialRecordService.add(request, operatorId);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑原材料管理记录")
  @Log(name = "编辑原材料管理记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
                     @RequestBody @Request MaterialRecord request) {
    materialRecordService.modify(id, request);
  }

  @PutMapping(value = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "开始原材料")
  @PreAuthorize("isAuthenticated()")
  public void modify(@RequestBody List<Integer> idList, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    materialRecordService.start(idList, operatorId);
  }

  @PutMapping(value = "/suspend", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "暂停原材料")
  @PreAuthorize("isAuthenticated()")
  public void suspend(@RequestBody List<Integer> idList, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    materialRecordService.suspend(idList, operatorId);
  }

  @PutMapping(value = "/stop", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "结束原材料")
  @PreAuthorize("isAuthenticated()")
  public void stop(@RequestBody List<Integer> idList, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    materialRecordService.stop(idList, operatorId);
  }
}
