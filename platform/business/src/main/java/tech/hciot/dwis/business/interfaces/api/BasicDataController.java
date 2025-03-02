package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.BasicDataService;
import tech.hciot.dwis.business.domain.model.FurnacePatchingTable;
import tech.hciot.dwis.business.domain.model.FurnaceRammingTable;
import tech.hciot.dwis.business.domain.model.FurnaceStatusTable;
import tech.hciot.dwis.business.domain.model.Location;
import tech.hciot.dwis.business.domain.model.Manufacturer;
import tech.hciot.dwis.business.domain.model.ProductType;
import tech.hciot.dwis.business.domain.model.ScrapReasonCode;

@RestController
@RequestMapping(value = "/basic")
@Api(tags = "基础数据")
@Slf4j
public class BasicDataController {

  @Autowired
  private BasicDataService basicDataService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询有效的基础数据")
  public List<Object> findEnabledBasicData (
      @RequestParam String dictName,
      @RequestParam(required = false, defaultValue = "") String location) {
    try {
      Class clazz = Class.forName("tech.hciot.dwis.business.domain.model." + dictName);
      return basicDataService.getBasicDataList(clazz, location);
    } catch (ClassNotFoundException e) {
      log.error("can't find enabled dict class: {}", dictName);
      return null;
    }
  }

  @GetMapping(value = "/furnace/status", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询有效的炉况情况")
  public List<FurnaceStatusTable> findFurnaceStatusList() {
    return basicDataService.getBasicDataList(FurnaceStatusTable.class);
  }

  @GetMapping(value = "/furnace/patching", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询有效的喷补料补炉位置")
  public List<FurnacePatchingTable> findFurnacePatchingList() {
    return basicDataService.getBasicDataList(FurnacePatchingTable.class);
  }

  @GetMapping(value = "/furnace/ramming", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询有效的打结料补炉位置")
  public List<FurnaceRammingTable> findFurnaceRammingList() {
    return basicDataService.getBasicDataList(FurnaceRammingTable.class);
  }

  @GetMapping(value = "/scrapreason/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "根据报废码查询有效的报废代码表")
  public List<ScrapReasonCode> findEnabledScrapReasonCode(
      @PathVariable String code,
      @RequestParam(required = false, defaultValue = "") String location) {
    return basicDataService.findEnabledScrapReasonCode(code, location);
  }

  @GetMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询有效的模块列表")
  public List<Location> findEnabledLocation() {
    return basicDataService.getBasicDataList(Location.class);
  }

  @GetMapping(value = "/producttype", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询有效的产品类型列表")
  public List<ProductType> findProductTypeList(int depId) {
    return basicDataService.findProductType(depId);
  }

  @GetMapping(value = "/manufacturer", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询有效的供应商列表")
  public List<Manufacturer> findManufacturerList(int productTypeId) {
    return basicDataService.findManufacturerList(productTypeId);
  }

}
