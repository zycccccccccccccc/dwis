package tech.hciot.dwis.business.application.certificate;

import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CONTRACT_EXECUTED;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.CertificateRepository;
import tech.hciot.dwis.business.domain.ContractRecordRepository;
import tech.hciot.dwis.business.domain.TrainNoRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.Certificate;
import tech.hciot.dwis.business.domain.model.TrainNo;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;
import tech.hciot.dwis.business.interfaces.dto.CertificatePrintRequest;

@Service
public class CertificateService {

  @Autowired
  private CertificateRepository certificateRepository;

  @Autowired
  private TrainNoRepository trainNoRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private ContractRecordRepository contractRecordRepository;

  @Autowired
  private SqlTemplateParser sqlTemplateParser;

  @Autowired
  private EntityManager entityManager;

  @Transactional
  public CertificateInfo getPrintData(CertificatePrintRequest certificatePrintRequest) {
    TrainNo trainNo = trainNoRepository.findByShippedNo(certificatePrintRequest.getCertificateId())
      .orElseThrow(() -> PlatformException.badRequestException("合格证号不存在"));
    if (certificatePrintRequest.getExecuted()) {
      if (trainNo.getConFinshed() == 1) {
        throw CONTRACT_EXECUTED.getPlatformException();
      }
      updateTrainNoAndContract(certificatePrintRequest, trainNo);
    }
    List<Certificate> certificateList = certificateRepository
      .findByShippedNoOrderByWheelSerial(certificatePrintRequest.getCertificateId());
    if (certificateList == null || certificateList.isEmpty()) {
      throw PlatformException.badRequestException("没有查到合格证数据");
    }
    CertificateInfo certificateInfo = CertificateInfo.builder().build();
    BeanUtil.copyProperties(certificateList.get(0), certificateInfo,
      CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    certificateInfo.setCertificateList(certificateList);
    certificateInfo.setContractNo(certificatePrintRequest.getContractNo());
    certificateInfo.setContractName(certificatePrintRequest.getContractName() == null
      ? "" : certificatePrintRequest.getContractName());
    return certificateInfo;
  }

  // 更新发运车皮号表和合同表
  private void updateTrainNoAndContract(CertificatePrintRequest certificatePrintRequest, TrainNo trainNo) {
    int count = wheelRecordRepository.countByShippedNo(certificatePrintRequest.getCertificateId());
    contractRecordRepository.findById(certificatePrintRequest.getContractId()).ifPresent(contractRecord -> {
      contractRecord.setShippedSum(contractRecord.getShippedSum() + count);
      contractRecord.setSurplusSum(contractRecord.getContractSum() - contractRecord.getShippedSum());
      contractRecordRepository.save(contractRecord);
      trainNo.setCId(contractRecord.getId());
    });
    trainNo.setShippedSum(count);
    trainNo.setConOpeid(certificatePrintRequest.getOpeId());
    trainNo.setConFinshed(1);
    trainNoRepository.save(trainNo);
  }

  public List<String> getSampleWheelList(String shippedNo) {
    Query query = createQuery("sample-product");
    query.setParameter("shippedNo", shippedNo);
    List<Map<String, String>> resultList = query.getResultList();
    return resultList.stream().map(map -> map.get("wheel_serial")).collect(Collectors.toList());
  }

  private Query createQuery(String sqlTemplateName) {
    String sql = sqlTemplateParser.parseSqlTemplate("certificate", sqlTemplateName);
    Query query = entityManager.createNativeQuery(sql);
    query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    return query;
  }

  public List findSa34(String shippedNo) {
    Query query = createQuery("sa34");
    query.setParameter("shippedNo", shippedNo);
    return query.getResultList();
  }
}
