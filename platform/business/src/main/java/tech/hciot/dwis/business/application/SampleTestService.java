package tech.hciot.dwis.business.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.business.domain.BalanceTestRecordRepository;
import tech.hciot.dwis.business.domain.HbtestRecordRepository;
import tech.hciot.dwis.business.domain.JMachineRecordRepository;
import tech.hciot.dwis.business.domain.KMachineRecordRepository;
import tech.hciot.dwis.business.domain.MtTestRecordRepository;
import tech.hciot.dwis.business.domain.QMachineRecordRepository;
import tech.hciot.dwis.business.domain.ShotTestRecordRepository;
import tech.hciot.dwis.business.domain.TMachineRecordRepository;
import tech.hciot.dwis.business.domain.ThreehbRecordRepository;
import tech.hciot.dwis.business.domain.TroundRecordRepository;
import tech.hciot.dwis.business.domain.UtTestRecordRepository;
import tech.hciot.dwis.business.domain.WMachineRecordRepository;
import tech.hciot.dwis.business.domain.model.InspecCheckable;
import tech.hciot.dwis.business.interfaces.dto.SampleTestRequest;

@Service
@Slf4j
public class SampleTestService {

  @Autowired
  private HbtestRecordRepository hbtestRecordRepository;

  @Autowired
  private ThreehbRecordRepository threehbRecordRepository;

  @Autowired
  private UtTestRecordRepository utTestRecordRepository;

  @Autowired
  private MtTestRecordRepository mtTestRecordRepository;

  @Autowired
  private BalanceTestRecordRepository balanceTestRecordRepository;

  @Autowired
  private ShotTestRecordRepository shotTestRecordRepository;

  @Autowired
  private TroundRecordRepository troundRecordRepository;


  @Autowired
  private JMachineRecordRepository jMachineRecordRepository;


  @Autowired
  private TMachineRecordRepository tMachineRecordRepository;


  @Autowired
  private KMachineRecordRepository kMachineRecordRepository;


  @Autowired
  private QMachineRecordRepository qMachineRecordRepository;


  @Autowired
  private WMachineRecordRepository wMachineRecordRepository;

  private Map<String, JpaRepository> repositoryMap = new HashMap<>();

  @PostConstruct
  private void init() {
    repositoryMap.put("hbtest", hbtestRecordRepository);
    repositoryMap.put("threehb", threehbRecordRepository);
    repositoryMap.put("uttest", utTestRecordRepository);
    repositoryMap.put("mttest", mtTestRecordRepository);
    repositoryMap.put("balancetest", balanceTestRecordRepository);
    repositoryMap.put("shottest", shotTestRecordRepository);
    repositoryMap.put("tround", troundRecordRepository);

    repositoryMap.put("jMachine", jMachineRecordRepository);
    repositoryMap.put("tMachine", tMachineRecordRepository);
    repositoryMap.put("kMachine", kMachineRecordRepository);
    repositoryMap.put("qMachine", qMachineRecordRepository);
    repositoryMap.put("wMachine", wMachineRecordRepository);
  }

  public List<Integer> machineNoList(String checkType) {
    switch (checkType) {
      case "jMachine" :
        return jMachineRecordRepository.machineNoList();
      case "tMachine" :
        return tMachineRecordRepository.machineNoList();
      case "kMachine" :
        return kMachineRecordRepository.machineNoList();
      case "qMachine" :
        return qMachineRecordRepository.machineNoList();
      case "wMachine" :
        return wMachineRecordRepository.machineNoList();
      default:
        return new ArrayList<>();
    }
  }

  @Transactional
  public void check(String sampleTestType, List<SampleTestRequest> list) {
    list.forEach(request -> {
      repositoryMap.get(sampleTestType).findById(request.getId()).ifPresent(record -> {
        ((InspecCheckable) record).setIsInspecCheck(request.getIsInspecCheck());
        JpaRepository repository = repositoryMap.get(sampleTestType);
        repository.save(record);
      });
    });
  }
}
