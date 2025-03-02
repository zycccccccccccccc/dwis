package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
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
import tech.hciot.dwis.business.application.BatchPrintService;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.BatchPrintRequest;

@RestController
@RequestMapping(value = "/batchprint")
@Api(tags = "批量打印条码业务")
public class BatchPrintController {

  @Autowired
  private BatchPrintService batchPrintService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "导入车轮序列号")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<WheelRecord> importBatch(@Validated @RequestBody @Request BatchPrintRequest batchPrintRequest,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(batchPrintService.importBatch(batchPrintRequest, currentPage, pageSize));
  }

  @GetMapping(value = "/template")
  @ApiOperation(value = "下载入库模板")
  @PreAuthorize("isAuthenticated()")
  public void downloadTemplate(HttpServletResponse response) {
    batchPrintService.downloadTemplate(response);
  }
}
