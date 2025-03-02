package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.WheelInStoreExportService;
import tech.hciot.dwis.business.application.WheelInStoreService;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.ChangeCheckCodeRequest;
import tech.hciot.dwis.business.interfaces.dto.OutfitCheckResponse;
import tech.hciot.dwis.business.interfaces.dto.PreInStoreRequest;
import tech.hciot.dwis.business.interfaces.dto.WheelInStoreRequest;
import tech.hciot.dwis.business.interfaces.dto.WheelInStoreResponse;

@RestController
@RequestMapping(value = "/wheelinstock")
@Api(tags = "CJ33车轮入库业务")
public class WheelInStoreController {

  @Autowired
  private WheelInStoreService wheelInStoreService;

  @Autowired
  private WheelInStoreExportService wheelInStoreExportService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "/checkw")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "校验车轮(W)")
  public PageDataResponse<WheelInStoreResponse> checkWheelW(
      @Validated @RequestBody @Request WheelInStoreRequest wheelInStoreRequest,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(wheelInStoreService.checkWheelW(wheelInStoreRequest, currentPage, pageSize));
  }

  @PostMapping(value = "/checkabc")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "校验车轮(ABC)")
  public PageDataResponse<WheelInStoreResponse> checkWheelABC(
      @Validated @RequestBody @Request WheelInStoreRequest wheelInStoreRequest,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(wheelInStoreService.checkWheelABC(wheelInStoreRequest, currentPage, pageSize));
  }

  @GetMapping(value = "/checkoutfit")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "外观抽检")
  public void checkOutfit(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String design,
      HttpServletResponse response) {
    List<OutfitCheckResponse> checkDataList = wheelInStoreService.checkOutfit(startDate, endDate, design);
    wheelInStoreExportService.exportCheckData(checkDataList, response);
  }

  @PostMapping(value = "/preinstore")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "预入库")
  public Integer preInStore(
      @Validated @RequestBody @Request PreInStoreRequest preInStoreRequest) {
    return wheelInStoreService.preInStore(preInStoreRequest);
  }

  @PutMapping(value = "/changecode")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "变更验收编号")
  public void changeCode(
      @Validated @RequestBody @Request ChangeCheckCodeRequest changeCheckCodeRequest) {
    wheelInStoreService.changeCode(changeCheckCodeRequest);
  }

  @GetMapping(value = "/checkdata/export")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "交验数据导出")
  public void checkDataExport(
      @RequestParam String checkCode,
      @RequestParam String design,
      HttpServletResponse response) {
    wheelInStoreService.checkDataExport(checkCode, design, response);
  }

  @GetMapping(value = "/shippingdata/export")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "合格证导出")
  public void shippingDataExport(
      @RequestParam String checkCode,
      HttpServletResponse response) {
    wheelInStoreService.shippingDataExport(checkCode, response);
  }
}
