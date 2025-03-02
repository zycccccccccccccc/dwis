package tech.hciot.dwis.lab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import tech.hciot.dwis.lab.domain.MearurementsRepository;
import tech.hciot.dwis.lab.domain.ResultsRepository;
import tech.hciot.dwis.lab.domain.model.ChemistryDetail;
import tech.hciot.dwis.lab.domain.model.Measurements;
import tech.hciot.dwis.lab.domain.model.Results;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LabService {

  private static final long REFRESH_PERIOD = 1000 * 15;
  private static final long REFRESH_PERIOD_LOCAL = 1000 * 60;
  private static final long KEEPALIVE_PERIOD = 1000 * 15;

  @Autowired
  private MearurementsRepository measurementsRepository;

  @Autowired
  private ResultsRepository resultsRepository;

  @Autowired
  private DwisBusinessRemoteService dwisBusinessRemoteService;

  private Map<Integer, String> fieldMap = new HashMap<>();

  @PostConstruct
  private void init() {
    for (Field field : ChemistryDetail.class.getDeclaredFields()) {
      Order order = field.getAnnotation(Order.class);
      if (order != null) {
        fieldMap.put(order.value(), field.getName());
      }
    }
  }

  // 读取光谱仪计算机数据库数据，并解析成ChemistryDetail对象发送到服务端
  @Scheduled(initialDelay = REFRESH_PERIOD, fixedDelay = REFRESH_PERIOD)
  public void readDataFromSpectrograph() {
    log.info("receive measurements thread is running");
    measurementsRepository.findNewest().ifPresent(measurements -> {
      log.info("receive newest measurements && id is: {}", measurements.getId());
      if (measurements.getSampleSeq() != null && !measurements.getSampleSeq().isEmpty()) {
        try {
          log.info("parsed newest data && id is: {}", measurements.getId());
          ChemistryDetail chemistryDetail = parseChemistryDetail(measurements);
          if (chemistryDetail != null) {
            // 解析成功；
            try {
              // 发送DWIS服务器；
              sentToServer(chemistryDetail);
              measurements.setStatus(1);
            } catch (Exception e) {
              // 保存本地；
              log.error("Send ChemistryDetail to server failed: {}", e.getMessage());
              writeChemistryDetailToFile(chemistryDetail);
              measurements.setStatus(2);
            }
            measurementsRepository.save(measurements);
          }
        } catch (Exception e) {
          // 解析失败；
          log.info("parsed newest data failed && id is: {}", measurements.getId());
        }
      } else {
        log.info("sampleSeq of newest data is error && id is: {}", measurements.getId());
      }
    });
  }

  // 将从光谱仪计算机数据库中取到的检测数据解析成符合DWIS系统要求的格式
  private ChemistryDetail parseChemistryDetail(Measurements measurements) {
    String sampleSeq = measurements.getSampleSeq();
    if (validateSampleSeq(sampleSeq)) {
      try {
        // 解析炉号和样号
        ChemistryDetail chemistryDetail = parseFurnaceSeqAndsampleNo(sampleSeq);
        // 解析对应炉次的化学成分
        parseChemistryElement(measurements, chemistryDetail);
        chemistryDetail.setLabId((int) (System.currentTimeMillis() / 1000));
        chemistryDetail.setCreateDate(new Date());
        log.info("Parsed ChemistryDetail && SampleSeq is: {}", sampleSeq);
        return chemistryDetail;
      } catch (Exception e) {
        log.error("Parse ChemistryDetail failed: " + e.getMessage(), e);
        return null;
      }
    }
    return null;
  }
  // 判断取出数据的ID1字段（例如“1-1202-8-P1”）格式是否正确；
  private boolean validateSampleSeq (String sampleSeq) {
    // 计算字符'-'出现的次数；
    Integer count = 0;
    Integer index = sampleSeq.indexOf('-');
    if (index != null) {
      while (index >= 0) {
        count++;
        index = sampleSeq.indexOf('-', index +1);
      }
      if (count == 3) {
        String[] res = sampleSeq.split("-");
        //判断炉号、炉次、出钢号是否整型；
        try {
          Integer.valueOf(res[0].trim());
          Integer.valueOf(res[1].trim());
          Integer.valueOf(res[2].trim());
        } catch (NumberFormatException e) {
          log.error("ID1 of measurements received is error && ID1 is: {}", sampleSeq);
          return false;
        }
        // 判断样号是否正确;
        if (res[3].trim().charAt(0) != 'P' && res[3].trim().charAt(0) != 'T' && res[3].trim().charAt(0) != 'L') {
          log.error("ID1 of measurements received is error && ID1 is: {}", sampleSeq);
          return false;
        }
      } else {
        log.error("ID1 of measurements received is error && ID1 is: {}", sampleSeq);
        return false;
      }
    } else {
      log.error("ID1 of measurements received is error && ID1 is: {}", sampleSeq);
      return false;
    }
    return true;
  }

  // 解析化学元素
  private void parseChemistryElement(Measurements measurements, ChemistryDetail chemistryDetail) {
    // 根据measurementId获取对应元素检测值；
    List<Results> components = resultsRepository.findByMeasurement(measurements.getId());
    if (components.size() > 0) {
      for (int i = 0; i < components.size(); i++) {
        Results res = components.get(i);
        switch (res.getComponent()) {
          case "C":
            chemistryDetail.setC(res.getValue());
            break;
          case "Si":
            chemistryDetail.setSi(res.getValue());
            break;
          case "Mn":
            chemistryDetail.setMn(res.getValue());
            break;
          case "P":
            chemistryDetail.setP(res.getValue());
            break;
          case "S":
            chemistryDetail.setS(res.getValue());
            break;
          case "Cr":
            chemistryDetail.setCr(res.getValue());
            break;
          case "Ni":
            chemistryDetail.setNi(res.getValue());
            break;
          case "V":
            chemistryDetail.setV(res.getValue());
            break;
          case "Mo":
            chemistryDetail.setMo(res.getValue());
            break;
          case "Ti":
            chemistryDetail.setTi(res.getValue());
            break;
          case "Cu":
            chemistryDetail.setCu(res.getValue());
            break;
          case "Al":
            chemistryDetail.setAl(res.getValue());
            break;
          case "Sn":
            chemistryDetail.setSn(res.getValue());
            break;
          case "Nb":
            chemistryDetail.setNb(res.getValue());
            break;
          default:
            break;
        }
      }
    } else {
      log.error("fetch components from results failed && measurementId is {}: ", measurements.getId());
    }
  }

  // 解析炉号和样号
  private ChemistryDetail parseFurnaceSeqAndsampleNo(String sampleSeq) {
    String furnaceSeq = null; //炉号（例“2-168”）
    String sampleNo = null; //样号（例“1-L4”）
    String[] splitRes = sampleSeq.split("-");
    furnaceSeq = splitRes[0].trim() + "-" + splitRes[1].trim();
    sampleNo = splitRes[2].trim() + "-" + splitRes[3].trim();
    return ChemistryDetail.builder().furnaceSeq(furnaceSeq).sampleNo(sampleNo).build();
  }

  // 将化学成分对象发送到DWIS服务端
  private void sentToServer(ChemistryDetail chemistryDetail) {
    dwisBusinessRemoteService.add(chemistryDetail);
  }

  // 将化学成分对象写到本地文件
  private void writeChemistryDetailToFile(ChemistryDetail chemistryDetail) {
    try {
      Object jsonObject = JSONObject.toJSON(chemistryDetail);
      String path = ResourceUtils.getFile("../static/").getPath();
      Files.write(Paths.get(path + "/" + chemistryDetail.getLabId() + ".txt"),
              jsonObject.toString().getBytes());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  // 定时解析本地发送失败的化验文件
  @Scheduled(initialDelay = REFRESH_PERIOD_LOCAL, fixedDelay = REFRESH_PERIOD_LOCAL)
  @Async
  public void resentChemistryDetail() {
    File[] chemistryDetailFiles;
    try {
      chemistryDetailFiles = ResourceUtils.getFile("../static").listFiles();
    } catch (FileNotFoundException e) {
      return;
    }
    if (chemistryDetailFiles == null) {
      return;
    }
    for (File chemistryDetailFile : chemistryDetailFiles) {
      try {
        Path chemistryDetailPath = Paths.get(chemistryDetailFile.getPath());
        byte[] bytes = Files.readAllBytes(chemistryDetailPath);
        String chemistryStr = new String(bytes);
        log.info("Parse local file: {}", chemistryStr);
        ChemistryDetail chemistryDetail = JSON.parseObject(chemistryStr, ChemistryDetail.class);
        if (chemistryDetail != null) {
          sentToServer(chemistryDetail);
          Files.deleteIfExists(chemistryDetailPath);
        }
      } catch (IOException e) {
        log.error(e.getMessage());
      }
    }
  }

  // 向服务端发送本地程序运行状态
  @Scheduled(initialDelay = KEEPALIVE_PERIOD, fixedDelay = KEEPALIVE_PERIOD)
  @Async
  public void keepAlive() {
    try {
      dwisBusinessRemoteService.keepAlive();
    } catch (Exception e) {
      log.error("keep alive failed: {}", e.getMessage());
    }
  }
}
