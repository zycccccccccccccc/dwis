package tech.hciot.dwis.business.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.TableInfoRepository;
import tech.hciot.dwis.business.domain.model.TableInfo;

@Service
@Slf4j
public class TableInfoService {

  @Autowired
  private TableInfoRepository tableInfoRepository;

  @Autowired
  private EntityManager entityManager;

  private Map<String, Map<String, String>> columnDescMap = new HashMap<>();

  @PostConstruct
  private void initTableInfo() {
    List<TableInfo> tableInfoList = tableInfoRepository.findAll();
    tableInfoList.forEach(tableInfo -> {
      String tableName = tableInfo.getTableName();
      Map<String, String> columnMap = columnDescMap.get(tableName);
      if (columnMap == null) {
        columnDescMap.put(tableName, new HashMap<>());
      }
      columnDescMap.get(tableName).put(tableInfo.getColumnName(), tableInfo.getColumnDesc());
    });
  }

  public List<TableInfo> findByTableName(String tableName) {
    return tableInfoRepository.findByTableName(tableName);
  }

  public Map<String, String> getColumnMap(String tableName) {
    return columnDescMap.get(tableName);
  }

  /**
   * 根据表名和ID列的值查询表对象。只能查询ID类型为整型的表
   * @param tableName
   * @param id
   * @return
   */
  public Map<String, Object> findByTableNameAndId(String tableName, Object id) {
    if (id == null) {
      log.info("no id");
      return null;
    }
    String querySql = "SELECT * FROM " + tableName + " WHERE id = " + id;
    Query query = entityManager.createNativeQuery(querySql);
    query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    Map<String, Object> result;
    try {
      result = (Map<String, Object>) query.getSingleResult();
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
    return result;
  }
}
