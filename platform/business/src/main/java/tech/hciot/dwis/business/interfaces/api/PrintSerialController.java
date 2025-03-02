package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.PrintSerialService;
import tech.hciot.dwis.business.interfaces.dto.InternalBarcodeResponse;
import tech.hciot.dwis.business.interfaces.dto.InternalSerialResponse;
import tech.hciot.dwis.business.interfaces.dto.SouthAfricaBarcodeResponse;
import tech.hciot.dwis.business.interfaces.dto.SouthAfricaSerialResponse;
import tech.hciot.dwis.business.interfaces.dto.UsaBarcodeResponse;
import tech.hciot.dwis.business.interfaces.dto.UsaSerialResponse;

@RestController
@RequestMapping(value = "/print")
@Api(tags = "打印串号业务")
public class PrintSerialController {

  @Autowired
  private PrintSerialService printSerialService;

  @GetMapping(value = "/serial/internal", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "国内串号打印")
  public List<InternalSerialResponse> internalSerial(
      @RequestParam String startSerial,
      @RequestParam String endSerial) {
    return printSerialService.internalSerial(startSerial.trim(), endSerial.trim());
  }

  @GetMapping(value = "/serial/usa", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "美国串号打印")
  public List<UsaSerialResponse> usaSerial(
      @RequestParam String startSerial,
      @RequestParam String endSerial,
      @RequestParam String design,
      @RequestParam String orderNo,
      @RequestParam String netWeight,
      @RequestParam String grossWeight,
      @RequestParam String date) {
    return printSerialService.usaSerial(startSerial.trim(), endSerial.trim(), design, orderNo.trim(), netWeight.trim(), grossWeight.trim(), date);
  }

  @GetMapping(value = "/serial/sa", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "南非串号打印")
  public List<SouthAfricaSerialResponse> southAfricaSerial(
      @RequestParam String startSerial,
      @RequestParam String endSerial,
      @RequestParam String design,
      @RequestParam String orderNo,
      @RequestParam String netWeight,
      @RequestParam String grossWeight,
      @RequestParam String boreSize) {
    return printSerialService.southAfricaSerial(startSerial.trim(), endSerial.trim(), design, orderNo.trim(), netWeight.trim(), grossWeight.trim(), boreSize.trim());
  }

  @GetMapping(value = "/barcode/internal", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "国内条码打印")
  public List<InternalBarcodeResponse> internalBarcode(
      @RequestParam String startSerial,
      @RequestParam String endSerial) {
    return printSerialService.internalBarcode(startSerial.trim(), endSerial.trim());
  }

  @GetMapping(value = "/barcode/usa", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "美国条码打印")
  public List<UsaBarcodeResponse> usaBarcode(
      @RequestParam String startSerial,
      @RequestParam String endSerial) {
    return printSerialService.usaBarcode(startSerial.trim(), endSerial.trim());
  }

  @GetMapping(value = "/barcode/sa", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "南非条码打印")
  public List<SouthAfricaBarcodeResponse> southAfricaBarcode(
      @RequestParam String startSerial,
      @RequestParam String endSerial) {
    return printSerialService.southAfricaBarcode(startSerial.trim(), endSerial.trim());
  }
}
