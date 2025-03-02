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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.TechnicalDocumentService;
import tech.hciot.dwis.business.domain.model.TechnicalDocument;
import tech.hciot.dwis.business.domain.model.TechnicalDocumentInfo;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/techdoc")
@Api(tags = "技术文件")
public class TechnicalDocumentController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private TechnicalDocumentService technicalDocumentService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询技术文件列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<TechnicalDocumentInfo> find(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String accountId = user.getAccountId();
    Page<TechnicalDocumentInfo> page = technicalDocumentService.find(accountId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/mgr", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "管理员查看技术文件列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<TechnicalDocument> findForMgr(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<TechnicalDocument> page = technicalDocumentService.findForMgr(currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiOperation(value = "添加技术文件")
  @Log(name = "添加技术文件", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void add(@RequestParam("title") String title,
                               @RequestParam("department") List<Integer> department,
                               @RequestParam("file") MultipartFile file,
                               Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String author = user.getOperatorId();
    technicalDocumentService.add(title, author, department, file);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除技术文件")
  @Log(name = "删除技术文件", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    technicalDocumentService.delete(id);
  }

  @PutMapping(value = "/publish/{id}")
  @ApiOperation(value = "发布技术文件")
  @PreAuthorize("isAuthenticated()")
  public void publish(@PathVariable @Id Integer id) {
    technicalDocumentService.publish(id);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查看技术文档详情")
  @PreAuthorize("isAuthenticated()")
  public TechnicalDocument findById(@PathVariable Integer id, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String accountId = user.getAccountId();
    return technicalDocumentService.findById(id, accountId);
  }

  @GetMapping(value = "/static/{fileName:.+}")
  @ApiOperation(value = "查看技术文档内容")
  @PreAuthorize("permitAll()")
  public void content(@PathVariable("fileName") String filename,
                                    HttpServletResponse response) {
    technicalDocumentService.content(filename, response);
  }
}
