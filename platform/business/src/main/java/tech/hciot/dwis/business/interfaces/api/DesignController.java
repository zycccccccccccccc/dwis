package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import tech.hciot.dwis.business.application.DesignService;
import tech.hciot.dwis.business.domain.model.Design;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.DesignRequest;

@RestController
@RequestMapping(value = "/design")
@Api(tags = "轮型管理")
public class DesignController {

  @Autowired
  private DesignService designService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询轮型列表")
  public PageDataResponse<Design> findDesign(
      @RequestParam(required = false) String design,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler
        .toPageDataResponse(designService.findDesign(design, currentPage, pageSize));
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询线上生产用轮型列表")
  public List<String> findDesignList () {return designService.findDesignList();}

  @GetMapping(value = "/reportlist", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询报表轮型列表")
  public List<String> findDesignListForReport () {return designService.findDesignListForReport();}


  @GetMapping(value = "/alllist", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询所有轮型号列表")
  public List<String> findAllDesignList () {
    return designService.findAllDesignList();
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "添加轮型")
  @Log(name = "添加轮型", type = OperationType.OPERATION_TYPE_ADD)
  public void addDesign(@RequestBody @Request DesignRequest designRequest) {
    designService.addDesign(designRequest.convert2Model());
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "编辑轮型")
  @Log(name = "编辑轮型", type = OperationType.OPERATION_TYPE_MODIFY, operationObject = "design")
  public void editDesign(@PathVariable @Id Integer id,
                         @RequestBody @Request DesignRequest designRequest) {
    designService.editDesign(id, designRequest.convert2Model());
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "删除轮型")
  @Log(name = "删除轮型", type = OperationType.OPERATION_TYPE_DELETE, operationObject = "design")
  public void deleteDesign(@PathVariable @Id Integer id) {
    designService.deleteDesign(id);
  }
}
