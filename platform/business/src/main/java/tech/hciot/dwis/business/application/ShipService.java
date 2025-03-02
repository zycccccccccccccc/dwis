package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.INVALID_SHELF_NO;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.INVALID_SHIPPED_NO;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.SHIPPING_DATA_EXPORT_NO_DATA;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.ShipTemp;
import tech.hciot.dwis.business.domain.model.TrainNo;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.*;

@Service
@Slf4j
public class ShipService {

  private static final int ROW_NUM_PER_PAGE = 42;
  private static final int PAGE_SIZE = ROW_NUM_PER_PAGE * 5;
  private static final int TOTAL_ROW = 48;
  private static final int TOTAL_COLUMN = 15;

  @Autowired
  private ShipTempRepository shipTempRepository;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TrainNoRepository trainNoRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private HeatCodeRepository heatCodeRepository;

  @Autowired
  private TestCodeRepository testCodeRepository;

  @Autowired
  private HoldCodeRepository holdCodeRepository;

  public List<Object> transferData(TransferDataRequest request) {
    List<Object> transferDataResponses = null;
    String hgz_temp = request.getAbc().get(0).split(" ")[1];
    request.getAbc().forEach(abc -> {
      String[] array = StringUtils.split(abc, " ");
      String wheelSerial = array[2];
      String hgz = array[1];
      if (wheelSerial.startsWith("CO")) {
        wheelSerial = StringUtils.join(wheelSerial.substring(10, 12), wheelSerial.substring(8, 10), wheelSerial.substring(2, 8));
      }
      shipTempRepository.save(
          ShipTemp.builder().pId(request.getShippedId()).hgz(hgz).wheelSerial(wheelSerial).createTime(new Date())
              .serialNo(shipTempRepository.getMaxSerialNo() + 1).build());
    });
    if (hgz_temp != null) {
      transferDataResponses = shipTempRepository.findRepeatedWheelSerialByHgz(hgz_temp);
    }
    return transferDataResponses;
  }

  public Page<ShipCheckCodeResponse> checkData(String hgz, Integer currentPage, Integer pageSize) {

    List<ShipCheckCodeResponse> list = entityManager.createNamedQuery("ShipCheckCodeResponse").setParameter(1, hgz)
        .getResultList();
    PagedListHolder pagedListHolder = new PagedListHolder(list);
    pagedListHolder.setPageSize(pageSize);
    pagedListHolder.setPage(currentPage);
    return new PageImpl<ShipCheckCodeResponse>(pagedListHolder.getPageList(), PageRequest.of(currentPage, pageSize), list.size());
  }

  @Transactional
  public Page<ShipDataResponse> getData(String hgz, String shelfNo, Integer currentPage, Integer pageSize) {
    List<ShipTemp> shipTempList = shipTempRepository.getByHgzOrderBySerialNoAsc(hgz);

    for (int i = 0; i < shipTempList.size(); i++) {
      shipTempList.get(i).setHgzSerialNo(i + 1);
    }
    shipTempRepository.saveAll(shipTempList);
    shipTempList = shipTempRepository.getByHgzOrderByHgzSerialNoAsc(hgz);
    if (StringUtils.isNotBlank(shelfNo)) {
      if (!shelfNo.matches("^[a-zA-Z]{2}\\d+$")) {
        throw INVALID_SHELF_NO.getPlatformException();
      }
      String prefix = shelfNo.substring(0, 2);
      int start = Integer.valueOf(shelfNo.substring(2)) - 1;
      for (int i = 0; i < shipTempList.size(); i++) {
        if (i % 4 == 0) {
          start++;
        }
        shipTempList.get(i).setShelfNo(prefix + start);
      }
    } else {
      shipTempList.forEach(shipTemp -> shipTemp.setShelfNo(null));
    }
    shipTempRepository.saveAll(shipTempList);
    return shipTempRepository.getShipData(hgz, PageRequest.of(currentPage, pageSize)).map(map ->
        ShipDataResponse.builder().serialNo(map.get("hgz_serial_no") == null ? null : (Integer) map.get("hgz_serial_no"))
            .hgz(getStringValue(map.get("hgz")))
            .wheelSerial(getStringValue(map.get("wheel_serial"))).design(getStringValue(map.get("design")))
            .stockDate(map.get("stock_date") == null ? null : (Date) map.get("stock_date"))
            .boreSize((Integer) map.get("bore_size"))
            .tapeSize((BigDecimal) map.get("tape_size"))
            .wheelW((Integer) map.get("wheel_w")).balanceS(getStringValue(map.get("balance_s")))
            .shippedNo(getStringValue(map.get("shipped_no"))).shelfNo(getStringValue(map.get("shelf_no")))
            .shelfNumber(getStringValue(map.get("shelf_number"))).flag(getStringValue(map.get("flag")))
            .finished(map.get("finished") == null ? null : Integer.valueOf((Byte) map.get("finished")))
            .checkCode(getStringValue(map.get("check_code")))
            .createTime((Date) map.get("create_time"))
            .mecSerial(getStringValue(map.get("mec_serial")))
            .testCode(getStringValue(map.get("test_code")))
            .build());
  }

  @Transactional
  public void deleteData(List<EditShipData> editShipDataList) {
    editShipDataList.forEach(editShipData -> shipTempRepository.deleteByHgzAndHgzSerialNo(editShipData.getHgz(),
        editShipData.getSerialNo()));
  }

  @Transactional
  public void editData(List<EditShipData> editShipDataList) {
    editShipDataList.forEach(editShipData ->
        shipTempRepository.findByHgzAndHgzSerialNo(editShipData.getHgz(), editShipData.getSerialNo())
            .ifPresent(shipTemp -> {
              if (StringUtils.isNotEmpty(editShipData.getWheelSerial())) {
                shipTemp.setWheelSerial(editShipData.getWheelSerial());
              }
              if (!editShipData.getHgzNew().equals(editShipData.getHgz())) {
                shipTemp.setHgz(editShipData.getHgzNew());
              }
              shipTempRepository.save(shipTemp);
            }));
  }

  @Transactional
  public void changeShippedNo(ChangeShippedNoRequest changeShippedNoRequest) {
    if (trainNoRepository.findByShippedNo(changeShippedNoRequest.getHgz().trim()).isPresent()) {
      throw INVALID_SHIPPED_NO.getPlatformException();
    }
    trainNoRepository.save(TrainNo.builder().shippedId(changeShippedNoRequest.getShippedId()).shippedNo(
        changeShippedNoRequest.getHgz().trim()).shippedDate(parseDate(changeShippedNoRequest.getShippedDate())).customerId(
        changeShippedNoRequest.getCustomerId()).trainNo(changeShippedNoRequest.getTrainNo().trim()).build());
    List<Object[]> data = wheelRecordRepository.findTransportWheel(changeShippedNoRequest.getHgz().trim());
    data.forEach(row -> {
      String wheelSerial = row[0].toString();
      String shelfNo = getStringValue(row[1]);
      wheelRecordRepository.findByWheelSerial(wheelSerial).ifPresent(wheelRecord -> {
        if (wheelRecord.getXrayReq() == 0 && StringUtils.isAllBlank(wheelRecord.getReworkCode(), wheelRecord.getScrapCode(), wheelRecord.getShippedNo())
                && isCihenOk(wheelRecord) && isTestCodeOk(wheelRecord) && isHeatCodeOk(wheelRecord) && isHoldCodeOk(wheelRecord)
                && wheelRecord.getStockDate() != null && wheelRecord.getFinished() == 1 && wheelRecord.getCheckCode().charAt(0) != 'A') {
          wheelRecord.setShippedNo(changeShippedNoRequest.getHgz().trim());
          wheelRecord.setShelfNumber(shelfNo);
          wheelRecordRepository.save(wheelRecord);
        }
      });
    });
  }

  public TrainNo getTrain(String hgz) {
    return trainNoRepository.findByShippedNo(hgz).orElse(null);
  }

  public void changeTrain(Integer id, ChangeTrainRequest changeTrainRequest) {
    trainNoRepository.findById(id).ifPresent(trainNo -> {
      trainNo.setTrainNo(changeTrainRequest.getTrainNo());
      trainNo.setShippedDate(parseDate(changeTrainRequest.getShippedDate()));
      trainNo.setCustomerId(changeTrainRequest.getCustomerId());
      trainNoRepository.save(trainNo);
    });
  }

  @Transactional
  public Integer correct(String hgz) {
    Integer count = wheelRecordRepository.correctWheel(hgz);
    trainNoRepository.deleteByShippedNo(hgz);
    return count;
  }

  @Transactional
  public Integer deletePrepare(String hgz) {
    return shipTempRepository.deleteByHgz(hgz);
  }

  public Page<ShipCheckCodeResponse> getWheel(String hgz, Integer currentPage, Integer pageSize) {
    List<ShipCheckCodeResponse> list = entityManager.createNamedQuery("WheelResponse").setParameter(1, hgz)
        .getResultList();
    PagedListHolder pagedListHolder = new PagedListHolder(list);
    pagedListHolder.setPageSize(pageSize);
    pagedListHolder.setPage(currentPage);
    return new PageImpl<ShipCheckCodeResponse>(pagedListHolder.getPageList(), PageRequest.of(currentPage, pageSize), list.size());
  }

  public void print(String hgz, HttpServletResponse response) {
    ShipWheelRecord shipWheelRecord = new ShipWheelRecord();
    List<String> wheelList = new ArrayList<>();
    trainNoRepository.getPrintData(hgz).forEach(row -> {
      if (wheelList.isEmpty()) {
        shipWheelRecord.setDesign(getStringValue(row[0]));
        shipWheelRecord.setShippedDate(row[1] == null ? null : DateFormatUtils.format((Date) row[1], "yyyy/M/d"));
        shipWheelRecord.setBalanceS(getStringValue(row[4]));
        shipWheelRecord.setShippedNo(getStringValue(row[5]));
        shipWheelRecord.setShippedId(getStringValue(row[6]));
        shipWheelRecord.setTrainNo(getStringValue(row[7]));
        shipWheelRecord.setCustomer(getStringValue(row[8]));
      }
      wheelList.add(getStringValue(row[2]));
    });

    if (!wheelList.isEmpty()) {
      shipWheelRecord.setWheelList(wheelList);
      shipWheelRecord.setAmount(wheelList.size());
      export(shipWheelRecord, response);
    } else {
      throw SHIPPING_DATA_EXPORT_NO_DATA.getPlatformException();
    }
  }

  private void export(ShipWheelRecord shipWheelRecord, HttpServletResponse response) {
    try {
      ClassPathResource resource = new ClassPathResource("static/ship-wheellist.xlsx");
      InputStream inputStream = resource.getInputStream();

      Workbook workbook = new XSSFWorkbook(inputStream);
      generateWorkSheet(workbook, shipWheelRecord);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=ship-wheellist.xlsx");
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void generateWorkSheet(Workbook workbook, ShipWheelRecord shipWheelRecord) {
    Date now = new Date();
    int pageSum = (shipWheelRecord.getAmount() + PAGE_SIZE - 1) / PAGE_SIZE;
    Sheet sheet = workbook.getSheetAt(0);

    Row row = sheet.getRow(1);
    Cell cell = row.getCell(1);
    cell.setCellValue(shipWheelRecord.getCustomer());
    cell = row.getCell(14);
    cell.setCellValue(shipWheelRecord.getBalanceS());
    row = sheet.getRow(2);
    cell = row.getCell(2);
    cell.setCellValue(shipWheelRecord.getShippedId());
    cell = row.getCell(6);
    cell.setCellValue(shipWheelRecord.getShippedNo());
    cell = row.getCell(9);
    cell.setCellValue(shipWheelRecord.getTrainNo());
    cell = row.getCell(13);
    cell.setCellValue(shipWheelRecord.getShippedDate());
    row = sheet.getRow(3);
    cell = row.getCell(6);
    cell.setCellValue(shipWheelRecord.getDesign());
    cell = row.getCell(13);
    cell.setCellValue(shipWheelRecord.getAmount());

    for (int page = 1; page < pageSum; page++) {
      log.info("copy page {}", page);
      ExcelUtil.copyCell(sheet, 0, 0,
          sheet, TOTAL_ROW * page, 0,
          TOTAL_ROW, TOTAL_COLUMN);
    }

    for (int k = 0; k < pageSum; k++) {
      int currentPageFirstRow = k * TOTAL_ROW;
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < ROW_NUM_PER_PAGE; j++) {
          row = sheet.getRow(currentPageFirstRow + j + 5);
          cell = row.getCell(i * 3);
          int index = (k * PAGE_SIZE) + i * ROW_NUM_PER_PAGE + j;
          if (shipWheelRecord.getWheelList().size() > index) {
            cell.setCellValue(shipWheelRecord.getWheelList().get(index));
          }
        }
      }
      row = sheet.getRow(currentPageFirstRow + TOTAL_ROW - 1);
      row.getCell(0).setCellValue(DateFormatUtils.format(now, "yyyy/M/d HH:mm:ss"));
      row.getCell(12).setCellValue(StringUtils.join("Page ", k + 1, " of ", pageSum));
      sheet.setRowBreak(row.getRowNum());
      sheet.setAutobreaks(true);
    }

    sheet.protectSheet("dwis");
  }

  public int checkShippedNo(String hgz) {
    return wheelRecordRepository.countByHgzAndMecSerial(hgz);
  }

  //判断磁痕代码
  private boolean isCihenOk(WheelRecord wheelRecord) {
    boolean result = false;
    if (wheelRecord.getDesign().equals("CJ33") || wheelRecord.getDesign().equals("CP33")) {
      if (wheelRecord.getCihenCode().equals("OK")) {
        result = true;
      }
    } else {
      result = true;
    }
    return result;
  }

  //判断试验代码
  private boolean isTestCodeOk(WheelRecord wheelRecord) {
    AtomicBoolean result = new AtomicBoolean(false);
    if (StringUtils.isBlank(wheelRecord.getTestCode())) {
      result.set(true);
    } else {
      testCodeRepository.findByCode(wheelRecord.getTestCode()).ifPresent(testCode -> {
        if (testCode.getCodeType().equals("Release")) {
          result.set(true);
        }
      });
    }
    return result.get();
  }

  //判断热处理代码
  private boolean isHeatCodeOk(WheelRecord wheelRecord) {
    AtomicBoolean result = new AtomicBoolean(false);
    if (StringUtils.isBlank(wheelRecord.getHeatCode())) {
      result.set(true);
    } else {
      heatCodeRepository.findByCode(wheelRecord.getHeatCode()).ifPresent(heatCode -> {
        if (heatCode.getCodeType().equals("Release")) {
          result.set(true);
        }
      });
    }
    return result.get();
  }

  //判断保留代码
  private boolean isHoldCodeOk(WheelRecord wheelRecord) {
    AtomicBoolean result = new AtomicBoolean(false);
    if (StringUtils.isBlank(wheelRecord.getHoldCode())) {
      result.set(true);
    } else {
      holdCodeRepository.findByCode(wheelRecord.getHoldCode()).ifPresent(holdCode -> {
        if (holdCode.getCodeType().equals("Release")) {
          result.set(true);
        }
      });
    }
    return result.get();
  }
}
