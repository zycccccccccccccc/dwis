package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
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
import tech.hciot.dwis.business.application.StockExportService;
import tech.hciot.dwis.business.application.StockService;
import tech.hciot.dwis.business.application.StockWheelListExportService;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.CheckWheelRequest;
import tech.hciot.dwis.business.interfaces.dto.CheckWheelResponse;
import tech.hciot.dwis.business.interfaces.dto.CheckMecserialRequest;
import tech.hciot.dwis.business.interfaces.dto.CheckMecserialResponse;
import tech.hciot.dwis.business.interfaces.dto.InStockRequest;
import tech.hciot.dwis.business.interfaces.dto.StockData;
import tech.hciot.dwis.business.interfaces.dto.WheelStockData;

@RestController
@RequestMapping(value = "/stock")
@Api(tags = "入库车轮业务")
public class StockController {

  @Autowired
  private StockService stockService;

  @Autowired
  private StockExportService stockExportService;

  @Autowired
  private StockWheelListExportService stockWheelListExportService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "可入库车轮业务")
  public void stock(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String listNo,
      @RequestParam String listDate,
      @RequestParam(required = false) String balanceFlag,
      @RequestParam(required = false) String design,
      @RequestParam(required = false) Integer boreSize,
      @RequestParam(required = false) String tapeSize,
      @RequestParam(required = false) Integer wheelW,
      HttpServletResponse response) {
    StockData stockData = stockService.stock(startDate, endDate, listNo, listDate,
        balanceFlag, design, boreSize, tapeSize, wheelW);
    stockExportService.exportStock(stockData, response);
  }

  @GetMapping(value = "/bigtape", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "可入库车轮（大带尺）业务")
  public void bigTape(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String listNo,
      @RequestParam String listDate,
      @RequestParam(required = false) String balanceFlag,
      @RequestParam(required = false) String design,
      @RequestParam(required = false) Integer boreSize,
      @RequestParam(required = false) Integer wheelW,
      HttpServletResponse response) {
    StockData stockData = stockService.bigTape(startDate, endDate, listNo, listDate,
        balanceFlag, design, boreSize, wheelW);
    stockExportService.exportStock(stockData, response);
  }

  @GetMapping(value = "/wheellist", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "可入库车轮(轮号列表)业务")
  public void wheelList(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String acceptanceNo,
      @RequestParam String stockDate,
      @RequestParam(required = false) String balanceFlag,
      @RequestParam(required = false) String design,
      @RequestParam(required = false) Integer boreSize,
      @RequestParam(required = false) String tapeSize,
      @RequestParam(required = false) Integer wheelW,
      HttpServletResponse response) {
    WheelStockData stockData = stockService.wheelList(startDate, endDate, acceptanceNo, stockDate,
            balanceFlag, design, boreSize, tapeSize, wheelW);
    stockWheelListExportService.exportStock(stockData, response);
  }

  @GetMapping(value = "/checklist", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "入库后交验单业务")
  public void checklist(
      @RequestParam String acceptanceNo,
      @RequestParam String listNo,
      @RequestParam String listDate,
      @RequestParam(required = false) String design,
      HttpServletResponse response) {
    StockData stockData = stockService.checklist(acceptanceNo, listNo, listDate, design);
    stockExportService.exportStock(stockData, response);
  }

  @PostMapping(value = "/checkMecserial")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "交验性能批次")
  public List<CheckMecserialResponse> checkMecserial(
          @Validated @RequestBody CheckMecserialRequest checkMecserialRequest) {
            return stockService.checkMecserial(checkMecserialRequest);
  }

  @PostMapping(value = "/in", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "入库业务")
  public int stockIn(@Validated @RequestBody InStockRequest inStockRequest) {
    return stockService.in(inStockRequest.getStartDate(), inStockRequest.getEndDate(), inStockRequest.getAcceptanceNo().trim(),
        inStockRequest.getStockDate(), inStockRequest.getBalanceFlag(), inStockRequest.getDesign(), inStockRequest.getBoreSize(),
            inStockRequest.getTapeSize(), inStockRequest.getWheelW());
  }

  @PostMapping(value = "/in/bigtape", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "入库（大带尺）")
  public int bigTapeStockIn(@Validated @RequestBody InStockRequest inStockRequest) {
    return stockService
        .bigTapeStockIn(inStockRequest.getStartDate(), inStockRequest.getEndDate(), inStockRequest.getAcceptanceNo().trim(),
                inStockRequest.getStockDate(), inStockRequest.getBalanceFlag(), inStockRequest.getDesign(), inStockRequest.getBoreSize(),
                inStockRequest.getWheelW());
  }

  @GetMapping(value = "/check")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "验收入库业务")
  public void check(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String acceptanceNo,
      HttpServletResponse response) {
    stockService.check(startDate, endDate, acceptanceNo, response);
  }

  @PostMapping(value = "/checkabc")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "校验车轮(ABC)")
  public List<CheckWheelResponse> checkAbc(
      @Validated @RequestBody CheckWheelRequest checkWheelRequest) {
    return stockService.checkAbc(checkWheelRequest);
  }

  @PostMapping(value = "/checkw")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "校验车轮(W)")
  public List<CheckWheelResponse> checkWheel(
      @Validated @RequestBody CheckWheelRequest checkWheelRequest) {
    return stockService.checkWheel(checkWheelRequest);
  }
}
