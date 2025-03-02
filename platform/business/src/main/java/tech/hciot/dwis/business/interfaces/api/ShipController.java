package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import tech.hciot.dwis.business.application.ShipService;
import tech.hciot.dwis.business.domain.model.TrainNo;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.*;

@RestController
@RequestMapping(value = "/ship")
@Api(tags = "车轮发运业务")
public class ShipController {

  @Autowired
  private ShipService shipService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "传输数据")
  @Log(name = "传输数据", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public List<Object> transferData(@Validated @RequestBody @Request TransferDataRequest request) {
    return shipService.transferData(request);
  }

  @GetMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "检查数据")
  @Log(name = "检查数据")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ShipCheckCodeResponse> checkData(
      @RequestParam String hgz,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(shipService.checkData(hgz.trim(), currentPage, pageSize));
  }

  @GetMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "获取待编辑数据")
  @Log(name = "获取待编辑数据")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ShipDataResponse> getData(
      @RequestParam String hgz,
      @RequestParam(required = false) String shelfNo,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(shipService.getData(hgz.trim(), shelfNo, currentPage, pageSize));
  }

  @DeleteMapping(value = "/data", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除待编辑数据")
  @Log(name = "删除待编辑数据")
  @PreAuthorize("isAuthenticated()")
  public void deleteData(@Validated @RequestBody List<EditShipData> editShipDataList) {
    shipService.deleteData(editShipDataList);
  }

  @PutMapping(value = "/data", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑数据")
  @Log(name = "编辑数据")
  @PreAuthorize("isAuthenticated()")
  public void editData(@Validated @RequestBody List<EditShipData> editShipDataList) {
    shipService.editData(editShipDataList);
  }


  @PutMapping(value = "/hgz", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "更新合格证号")
  @Log(name = "更新合格证号")
  @PreAuthorize("isAuthenticated()")
  public void changeShippedNo(@Validated @RequestBody ChangeShippedNoRequest changeShippedNoRequest) {
    shipService.changeShippedNo(changeShippedNoRequest);
  }

  @GetMapping(value = "/hgz/{hgz}")
  @ApiOperation(value = "更新合格证号预检")
  @Log(name = "更新合格证号预检")
  @PreAuthorize("isAuthenticated()")
  public int checkShippedNo(@PathVariable String hgz) {
    return shipService.checkShippedNo(hgz);
  }

  @GetMapping(value = "/train", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询车皮号")
  @Log(name = "查询车皮号")
  @PreAuthorize("isAuthenticated()")
  public TrainNo getTrain(@RequestParam String hgz) {
    return shipService.getTrain(hgz);
  }

  @PostMapping(value = "/train/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改车皮号")
  @Log(name = "修改车皮号")
  @PreAuthorize("isAuthenticated()")
  public void changeTrain(@PathVariable Integer id, @RequestBody ChangeTrainRequest changeTrainRequest) {
    shipService.changeTrain(id, changeTrainRequest);
  }

  @PostMapping(value = "/correct")
  @ApiOperation(value = "纠回车轮")
  @Log(name = "纠回车轮")
  @PreAuthorize("isAuthenticated()")
  public Integer correct(@RequestParam String hgz) {
    return shipService.correct(hgz);
  }

  @DeleteMapping(value = "/prepare")
  @ApiOperation(value = "删除预备数据")
  @Log(name = "删除预备数据")
  @PreAuthorize("isAuthenticated()")
  public Integer deletePrepare(@RequestParam String hgz) {
    return shipService.deletePrepare(hgz);
  }

  @GetMapping(value = "/wheel", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询不打磨车轮数据")
  @Log(name = "查询不打磨车轮数据")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ShipCheckCodeResponse> getWheel(
      @RequestParam String hgz,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(shipService.getWheel(hgz, currentPage, pageSize));
  }

  @GetMapping(value = "/print", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "打印发运单")
  @Log(name = "打印发运单")
  @PreAuthorize("isAuthenticated()")
  public void print(@RequestParam String hgz, HttpServletResponse response) {
    shipService.print(hgz, response);
  }
}
