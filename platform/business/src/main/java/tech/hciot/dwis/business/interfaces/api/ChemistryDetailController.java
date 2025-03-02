package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.ChemistryDetailService;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.ChemistryDetailResponse;

@RestController
@RequestMapping(value = "/chemistrydetail")
@Api(tags = "化学成分超标查询业务")
public class ChemistryDetailController {

  @Autowired
  private ChemistryDetailService chemistryDetailService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询化学成分超标数据")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ChemistryDetailResponse> find(
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(chemistryDetailService.find(startDate, endDate, currentPage, pageSize));
  }

  @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "导出化学成分超标数据")
  @PreAuthorize("isAuthenticated()")
  public void export(
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      HttpServletResponse response) {
    chemistryDetailService.export(startDate, endDate, response);
  }
}
