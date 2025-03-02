package tech.hciot.dwis.business.infrastructure.exception;

import org.springframework.http.HttpStatus;
import tech.hciot.dwis.base.exception.PlatformException;

public enum ErrorEnum {
  SUCCESS(0, "成功", HttpStatus.OK),
  CREATED(0, "新建或修改数据成功", HttpStatus.CREATED),
  CLEARED(0, "用户删除数据成功", HttpStatus.NO_CONTENT),
  INTERNAL_SERVER_ERROR(500, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR),
  REMOTE_SERVER_UNREACHABLE(515, "服务不可用", HttpStatus.SERVICE_UNAVAILABLE),

  ACCOUNT_EXIST(1100, "账号已经存在", HttpStatus.BAD_REQUEST),
  ACCOUNT_NOT_EXIST(1101, "账号不存在", HttpStatus.BAD_REQUEST),
  PASSWORD_NOT_VALID(1102, "旧密码不正确", HttpStatus.BAD_REQUEST),
  USER_VERIFY_IS_EMPTY(1103, "手机号码不能为空", HttpStatus.BAD_REQUEST),
  ACCOUNT_DELETE_FAILED(1104, "账号删除失败", HttpStatus.BAD_REQUEST),

  ROLE_NOT_EXIST(1201, "角色不存在", HttpStatus.NOT_FOUND),
  ROLE_NAME_EXIST(1202, "角色名称已存在", HttpStatus.BAD_REQUEST),
  ROLE_IS_USED(1203, "角色已经使用", HttpStatus.BAD_REQUEST),

  DICTIONARY_CREATE_FAILED(1301, "字典数据添加失败", HttpStatus.BAD_REQUEST),
  DICTIONARY_EXIST(1302, "字典数据已存在,不能重复添加", HttpStatus.BAD_REQUEST),

  DESIGNS_EXISTS(1401, "轮型已存在", HttpStatus.BAD_REQUEST),
  DESIGNS_NOT_EXISTS(1402, "轮型不存在", HttpStatus.BAD_REQUEST),

  GRAPHITE_KEY_EXISTS(1501, "原始石墨号已存在", HttpStatus.BAD_REQUEST),

  POUR_NOT_EXISTS(1601, "浇注记录不存在", HttpStatus.BAD_REQUEST),
  COMMIT_WHEEL_LESS_THAN_THREE(1602, "提交的轮数少于3个，无法提交", HttpStatus.BAD_REQUEST),
  OPEN_ACT_MUST_HAVE_VALUE(1603, "实际开箱时间必填，无法提交", HttpStatus.BAD_REQUEST),
  In_PIT_MUST_HAVE_VALUE(1604, "入桶日期时间必填，无法提交", HttpStatus.BAD_REQUEST),
  WHEEL_SERIAL_EXIST(1605, "车轮号已存在", HttpStatus.BAD_REQUEST),
  PIT_RECORD_NOT_EXISTS(1606, "进桶记录不存在", HttpStatus.BAD_REQUEST),
  WHEEL_SERIAL_NOT_EXIST(1607, "此轮号不存在", HttpStatus.BAD_REQUEST),

  WHEEL_SERIAL_1_CAN_NOT_EMPTY(1701, "轮号1不能为空", HttpStatus.BAD_REQUEST),
  WHEEL_SERIAL_2_CAN_NOT_EMPTY(1702, "轮号2不能为空", HttpStatus.BAD_REQUEST),
  HEAT_LINE_CAN_NOT_EMPTY(1703, "路线号不能为空", HttpStatus.BAD_REQUEST),
  HEAT_NOT_EXIST(1704, "找不到热处理记录", HttpStatus.BAD_REQUEST),
  HEAT_IN_DATE_CAN_NOT_EMPTY(1705, "进炉日期不能为空", HttpStatus.BAD_REQUEST),
  HEAT_IN_TIME_CAN_NOT_EMPTY(1706, "进炉时间不能为空", HttpStatus.BAD_REQUEST),
  HEAT_OUT_DATE_CAN_NOT_EMPTY(1707, "出炉日期不能为空", HttpStatus.BAD_REQUEST),
  HEAT_OUT_TIME_CAN_NOT_EMPTY(1708, "出炉时间不能为空", HttpStatus.BAD_REQUEST),
  HEAT_IN_ID_CAN_NOT_EMPTY(1709, "进炉工长不能为空", HttpStatus.BAD_REQUEST),
  HEAT_IN_OPERATOR_CAN_NOT_EMPTY(1710, "进炉操作工不能为空", HttpStatus.BAD_REQUEST),
  HEAT_IN_SHIFT_CAN_NOT_EMPTY(1711, "进炉班次不能为空", HttpStatus.BAD_REQUEST),
  HEAT_CUT_ID_CAN_NOT_EMPTY(1712, "切割工不能为空", HttpStatus.BAD_REQUEST),
  HEAT_OUT_ID_CAN_NOT_EMPTY(1713, "出炉工长不能为空", HttpStatus.BAD_REQUEST),
  HEAT_OUT_OPERATOR_CAN_NOT_EMPTY(1714, "出炉操作工不能为空", HttpStatus.BAD_REQUEST),
  HEAT_OUT_SHIFT_CAN_NOT_EMPTY(1715, "出炉班次不能为空", HttpStatus.BAD_REQUEST),

  CORRECT_WHEEL_PARAM_CAN_NOT_ALL_EMPTY(1801, "保留代码、返工代码、报废代码、磁痕代码不能全部为空", HttpStatus.BAD_REQUEST),
  CORRECT_WHEEL_SCRAP_CODE_CAN_NOT_EMPTY(1802, "报废代码不能为空", HttpStatus.BAD_REQUEST),
  CORRECT_WHEEL_RETURN_PARAM_CAN_NOT_ALL_EMPTY(1803, "返工代码、报废代码不能全部为空", HttpStatus.BAD_REQUEST),
  XRAY_PARAM_ERROR(1804, "X光结果为1A、2A时，报废原因必须为空", HttpStatus.BAD_REQUEST),
  CHEMISTRY_EXPORT_FAILED(1805, "导出化学成分超标数据失败，请稍后再试", HttpStatus.BAD_REQUEST),
  PERFORMANCE_EXPORT_FAILED(1806, "导出性能数据失败，请稍后再试", HttpStatus.BAD_REQUEST),
  SCRAP_REASON_EXPORT_FAILED(1807, "导出废品分析数据失败，请稍后再试", HttpStatus.BAD_REQUEST),
  CHECK_DATA_EXPORT_FAILED(1808, "导出交验数据失败，请稍后再试", HttpStatus.BAD_REQUEST),
  SHIPPING_DATA_EXPORT_FAILED(1809, "导出合格证数据失败，请稍后再试", HttpStatus.BAD_REQUEST),
  BALANCE_FLAG_CAN_NOT_EMPTY(1810, "平衡标识不能为空！", HttpStatus.BAD_REQUEST),
  BORE_SIZE_CAN_NOT_EMPTY(1811, "轴孔不能为空！", HttpStatus.BAD_REQUEST),
  SHELF_NUMBER_INVALID(1812, "串号输入不合法", HttpStatus.BAD_REQUEST),
  FILE_EXPORT_FAILED(1813, "导出电子文档失败，请稍后再试", HttpStatus.BAD_REQUEST),
  CONTRACT_EXECUTED(1814, "该合同已执行", HttpStatus.BAD_REQUEST),
  INVALID_SHELF_NO(1815, "开始串号输入不合法", HttpStatus.BAD_REQUEST),
  INVALID_SHIPPED_NO(1816, "合格证号已经存在", HttpStatus.BAD_REQUEST),
  WHEEL_IS_SCRAP(1817, "该轮已报废", HttpStatus.BAD_REQUEST),
  WHEEL_IS_FINISHED(1818, "该轮已成品", HttpStatus.BAD_REQUEST),
  LADLE_SEQ_INVALID(1819, "小包序列号对应的小包不存在", HttpStatus.BAD_REQUEST),
  NO_CRC_RECORD(1820, "该合格证号没有国铁记录", HttpStatus.BAD_REQUEST),
  START_PREFIX_NOT_EQUAL_END_PREFIX(1821, "开始串号和结束串号前缀不一致", HttpStatus.BAD_REQUEST),
  LADLE_EXIST(1822, "该炉下已有关联的小包信息，不能删除", HttpStatus.BAD_REQUEST),
  POUR_EXIST(1823, "该小包下有关联车轮，不能删除", HttpStatus.BAD_REQUEST),
  SHIPPING_DATA_EXPORT_NO_DATA(1824, "该合格证号没有发运单数据", HttpStatus.BAD_REQUEST),
  CHECK_DATA_EXPORT_NO_DATA(1825, "该时间段没有验收入库数据", HttpStatus.BAD_REQUEST),

  UNKNOWN_ERROR(9999, "未知错误", HttpStatus.BAD_REQUEST);

  private int errorcode;
  private String errordesc;
  private HttpStatus httpstatus;

  ErrorEnum(int errorcode, String errordesc, HttpStatus httpstatus) {
    this.errorcode = errorcode;
    this.errordesc = errordesc;
    this.httpstatus = httpstatus;
  }

  public int getErrorcode() {
    return errorcode;
  }

  public String getErrordesc() {
    return errordesc;
  }

  public HttpStatus getHttpstatus() {
    return httpstatus;
  }

  public void setErrordesc(String s) { this.errordesc = s; }

  public static ErrorEnum getErrorEnum(int errorCode) {
    for (ErrorEnum errorEnum : ErrorEnum.values()) {
      if (errorEnum.errorcode == errorCode) {
        return errorEnum;
      }
    }
    return INTERNAL_SERVER_ERROR;
  }

  public PlatformException getPlatformException() {
    return new PlatformException(errorcode, errordesc, httpstatus);
  }
}
