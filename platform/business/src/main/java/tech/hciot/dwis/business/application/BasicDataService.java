package tech.hciot.dwis.business.application;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.HeatCodeRepository;
import tech.hciot.dwis.business.domain.HoldCodeRepository;
import tech.hciot.dwis.business.domain.ManufacturerRepository;
import tech.hciot.dwis.business.domain.ProductTypeRepository;
import tech.hciot.dwis.business.domain.ScrapReasonCodeRepository;
import tech.hciot.dwis.business.domain.TestCodeRepository;
import tech.hciot.dwis.business.domain.model.HeatCode;
import tech.hciot.dwis.business.domain.model.HoldCode;
import tech.hciot.dwis.business.domain.model.Manufacturer;
import tech.hciot.dwis.business.domain.model.ProductType;
import tech.hciot.dwis.business.domain.model.ScrapReasonCode;
import tech.hciot.dwis.business.domain.model.TestCode;

@Service
public class BasicDataService {

  @Autowired
  private TestCodeRepository testCodeRepository;

  @Autowired
  private HeatCodeRepository heatCodeRepository;

  @Autowired
  private HoldCodeRepository holdCodeRepository;

  @Autowired
  private ScrapReasonCodeRepository scrapReasonCodeRepository;

  @Autowired
  private ProductTypeRepository productTypeRepository;

  @Autowired
  private ManufacturerRepository manufacturerRepository;

  @Autowired
  private EntityManager entityManager;

  public List getBasicDataList(Class z, String location) {
    String tableName = humpToLine(z.getSimpleName());
    String querySql = "SELECT * FROM " + tableName + " WHERE enabled = 1 AND id <> 0 ";
    if (StringUtils.isNotBlank(location)) {
      querySql = querySql + " AND (location" + " LIKE '%" + location + "%' OR location LIKE '%all%') ";
    }
    Query query = entityManager.createNativeQuery(querySql, z);
    List resultList = query.getResultList();
    return resultList;
  }

  public List getBasicDataList(Class z) {
    return getBasicDataList(z, "");
  }

  private String humpToLine(String hump) {
    return hump.replaceAll("[A-Z]", "_$0").toLowerCase().substring(1);
  }

  public Optional<TestCode> findTestCodeByCode(String code) {
    return testCodeRepository.findByCode(code);
  }

  public Optional<HeatCode> findHeatCodeByCode(String code) {
    return heatCodeRepository.findByCodeAndEnabled(code, 1);
  }

  public Optional<HoldCode> findHoldCodeByCode(String code) {
    return holdCodeRepository.findByCodeAndEnabled(code, 1);
  }

  public List<ScrapReasonCode> findEnabledScrapReasonCode(String code, String location) {
    return scrapReasonCodeRepository.findByScrapCode(code, location);
  }

  public List<ProductType> findProductType(int depId) {
    return productTypeRepository.findProductTypeList("," + depId + ",");
  }

  public List<Manufacturer> findManufacturerList(int productTypeId) {
    return manufacturerRepository.findManufacturerList("," + productTypeId + ",");
  }
}
