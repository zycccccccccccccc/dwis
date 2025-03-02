package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.DictionaryService;
import tech.hciot.dwis.business.domain.model.Dictionary;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;

@RestController
@RequestMapping(value = "/dictionary")
@Api(tags = "字典管理")
public class DictionaryController {

  @Autowired
  private DictionaryService dictionaryService;

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "获取字典表列表")
  @PreAuthorize("isAuthenticated()")
  public List<Dictionary> findList(@RequestParam(required = false, defaultValue = "") String name) {
    return dictionaryService.findDictionaryList(name);
  }

  @GetMapping(value = "/{tableName}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "获取字典表下配置表列表")
  @PreAuthorize("isAuthenticated()")
  public List<Map<String, Object>> find(@PathVariable String tableName,
                                        @RequestParam(required = false) String code,
                                        @RequestParam(required = false) String design,
                                        @RequestParam(required = false) String typeKxsj,
                                        @RequestParam(required = false) Integer enabled) {
    return dictionaryService.find(tableName, code, design, typeKxsj, enabled);
  }

  @PostMapping(value = "/{tableName}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加字典数据")
  @Log(name = "添加字典数据", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void createDictionary(@PathVariable String tableName,
                               @RequestBody Map<String, Object> valueMap) {
    dictionaryService.createDictionary(tableName, valueMap);
  }

  @PutMapping(value = "/{tableName}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改字典数据")
  @Log(name = "修改字典数据", type = "修改")
  @PreAuthorize("isAuthenticated()")
  public void updateDictionary(@PathVariable String tableName,
                               @PathVariable Integer id,
                               @RequestBody Map<String, Object> valueMap) {
    dictionaryService.updateDictionary(tableName, id, valueMap);
  }

  @DeleteMapping(value = "/{tableName}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除字典数据")
  @Log(name = "删除字典数据", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void deleteDictionary(@PathVariable String tableName,
                               @PathVariable Integer id) {
    dictionaryService.deleteDictionary(tableName, id);
  }
}
