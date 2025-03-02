package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.BalanceTestService;
import tech.hciot.dwis.business.application.FinalCheckTestService;
import tech.hciot.dwis.business.application.MagneticTestService;
import tech.hciot.dwis.business.application.SampleTestService;
import tech.hciot.dwis.business.application.ShotTestService;
import tech.hciot.dwis.business.application.UltraTestService;
import tech.hciot.dwis.business.domain.model.BalanceTestRecord;
import tech.hciot.dwis.business.domain.model.HbtestRecord;
import tech.hciot.dwis.business.domain.model.MtTestRecord;
import tech.hciot.dwis.business.domain.model.ShotTestRecord;
import tech.hciot.dwis.business.domain.model.ThreehbRecord;
import tech.hciot.dwis.business.domain.model.TroundRecord;
import tech.hciot.dwis.business.domain.model.UtTestRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.SampleTestRequest;

@RestController
@RequestMapping(value = "/inspectionsample")
@Api(tags = "质检工长抽验业务")
public class InspectionSampleTestController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private FinalCheckTestService finalCheckTestService;

  @Autowired
  private UltraTestService ultraTestService;

  @Autowired
  private MagneticTestService magneticTestService;

  @Autowired
  private BalanceTestService balanceTestService;

  @Autowired
  private ShotTestService shotTestService;

  @Autowired
  private SampleTestService sampleTestService;

  @RequestMapping(value = "threehb", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询三点硬度测量记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ThreehbRecord> findThreehbRecord(
      @RequestParam String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<ThreehbRecord> page = finalCheckTestService.findThreehbRecord(inspectorId, null, 1,
      currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "hbtest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询硬度机试验记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<HbtestRecord> findHbtestRecord(
      @RequestParam String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<HbtestRecord> page = finalCheckTestService.findHbtestRecord(inspectorId, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "ultra", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询超探开班试验记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<UtTestRecord> findUtTestRecord(
      @RequestParam String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<UtTestRecord> page = ultraTestService.find(inspectorId, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "magnetic", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询磁探开班试验记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<MtTestRecord> findMtTestRecord(
      @RequestParam String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<MtTestRecord> page = magneticTestService.find(inspectorId, null, null, null,
      currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "balance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询平衡机开班试验记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<BalanceTestRecord> findBalanceTestRecord(
      @RequestParam String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<BalanceTestRecord> page = balanceTestService.find(inspectorId, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "shot", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询抛丸开班试验记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ShotTestRecord> findShotTestRecord(
      @RequestParam String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<ShotTestRecord> page = shotTestService.find(inspectorId, null, null, null,
      currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "tround", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询踏面首件圆度记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<TroundRecord> findTroundRecord(
      @RequestParam String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<TroundRecord> page = finalCheckTestService.findTroundRecord(inspectorId, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "质检工抽验")
  @Log(name = "质检工抽验", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void check(@ApiParam(name = "checkType", value = "threehb-三点硬度测量，" +
    "hbtest-硬度机试验，uttest-超探开班试验，mttest-磁探开班试验，" +
    "balancetest-平衡机试验，shottest-抛丸试验，tround-踏面首件圆度专检")
                    @RequestParam String checkType,
                    @Validated @RequestBody @Request List<SampleTestRequest> request) {
    sampleTestService.check(checkType, request);
  }
}
