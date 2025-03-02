package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.business.infrastructure.DownloadUtil.download;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.DesignRepository;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.BatchPrintRequest;

@Service
public class BatchPrintService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private DesignRepository designRepository;

  @Autowired
  private BalanceService balanceService;

  public Page<WheelRecord> importBatch(BatchPrintRequest batchPrintRequest, Integer currentPage, Integer pageSize) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT * FROM wheel_record ";
    if (CollectionUtils.isNotEmpty(batchPrintRequest.getWheelSerial())) {
      sql += "WHERE wheel_serial IN (:wheelSerial) ";
      //按照导入的文件默认排序
      sql += "ORDER BY CHARINDEX (wheel_serial,'" + StringUtils.join(batchPrintRequest.getWheelSerial(), ",") + "')";
      //sql += "ORDER BY CHARINDEX (',' + CONVERT (nvarchar, wheel_serial) + ',', ',' + CONVERT (nvarchar, :wheelSerial) + ',')" ;
      //params.put("wheelSerial", StringUtils.join(batchPrintRequest.getWheelSerial(), ","));
      params.put("wheelSerial", batchPrintRequest.getWheelSerial());
    }
    String designFlag = "";
    String finishedFlag = "";
    if (batchPrintRequest.getDesignAsc() != null) {
      if (batchPrintRequest.getDesignAsc()) {
        designFlag = "design ASC";
      } else {
        designFlag = "design DESC";
      }
    }
    if (batchPrintRequest.getFinishedAsc() != null) {
      if (batchPrintRequest.getFinishedAsc()) {
        finishedFlag = "finished ASC";
      } else {
        finishedFlag = "finished DESC";
      }
    }
    /*if (!StringUtils.isAllBlank(designFlag, finishedFlag)) {
      sql += "ORDER BY " + (StringUtils.isBlank(designFlag) ? "" : (designFlag + ",")) + (StringUtils.isBlank(finishedFlag) ? ""
          : (finishedFlag + ","));
      sql = sql.substring(0, sql.length() - 1);
    }*/

    List<WheelRecord> list = template.query(sql, params, BeanPropertyRowMapper.newInstance(WheelRecord.class));
    PagedListHolder pagedListHolder = new PagedListHolder(list);
    pagedListHolder.setPage(currentPage);
    pagedListHolder.setPageSize(pageSize);
    return new PageImpl<WheelRecord>(pagedListHolder.getPageList(), PageRequest.of(currentPage, pageSize), list.size())
        .map(wheelRecord -> {
          wheelRecord.setInternal(
              designRepository.findByDesign(wheelRecord.getDesign()).map(design -> design.getInternal()).orElse(null));
            /*if (wheelRecord.getFinished() == 1) {
            if (wheelRecord.getDesign().equals("IR33") || wheelRecord.getDesign().equals("CJ33") || wheelRecord.getDesign()
                .equals("CG33") || wheelRecord.getDesign().equals("PAK950") || wheelRecord.getDesign().equals("GEZ")) {
              wheelRecord.setDataMatrix(balanceService.generateExternalDataMatrix(wheelRecord));
            } else if (wheelRecord.getInternal() == 1 && ((wheelRecord.getWheelW() < 137
                || wheelRecord.getTapeSize().doubleValue() < 840)
                || wheelRecord.getWheelW() == 137 && wheelRecord.getTapeSize().doubleValue() >= 840)) {
              wheelRecord.setDataMatrix(balanceService.generateInternalDataMatrix(wheelRecord));
            }
          }*/
          if (wheelRecord.getFinished() == 1) {
            if (wheelRecord.getInternal() == 1) {
              wheelRecord.setDataMatrix(balanceService.generateInternalDataMatrix(wheelRecord));
            } else {
              wheelRecord.setDataMatrix(balanceService.generateExternalDataMatrix(wheelRecord));
            }
          }
          return wheelRecord;
        });
  }

  public void downloadTemplate(HttpServletResponse response) {
    String fileName = "Batch_Print.xlsx";
    download(response, fileName);
  }
}
