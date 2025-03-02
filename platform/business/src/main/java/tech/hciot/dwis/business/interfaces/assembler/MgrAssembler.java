package tech.hciot.dwis.business.interfaces.assembler;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.domain.AccountsRepository;
import tech.hciot.dwis.business.domain.DepartmentRepository;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.MaxGraphiteResponse;
import tech.hciot.dwis.business.interfaces.dto.PourRecordForPitRequest;
import tech.hciot.dwis.business.interfaces.dto.UserResponse;
import tech.hciot.dwis.business.interfaces.dto.WheelRecordResponse;

@Component
public class MgrAssembler {

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  public <T> PageDataResponse<T> toPageDataResponse(Page<T> elements) {
    return new PageDataResponse<>(elements.getTotalElements(), elements.getTotalPages(),
        elements.getSize(), elements.getNumber(), elements.getContent());
  }

  public UserResponse toUserResponse(Account user) {
    List<String> roles = accountsRepository.findAccountRoles(user.getUsername());
    UserResponse userResponse = UserResponse.builder().username(user.getUsername()).nickname(user.getNickname())
        .mobile(user.getMobile()).email(user.getEmail()).status(user.getStatus()).depId(user.getDepId())
        .depName(departmentRepository.findById(user.getDepId()).map(department -> department.getDepName()).orElse(null))
        .location(user.getLocation()).roles(roles).loginStatus(user.getLoginStatus()).build();
    return userResponse;
  }

  public List<PourRecord> toPourRecordList(List<PourRecordForPitRequest> pourRecordForPitRequestList) {
    List<PourRecord> pourRecordList = new ArrayList<>();
    if (pourRecordForPitRequestList != null) {
      pourRecordForPitRequestList.forEach(pourRecordForPitRequest -> pourRecordList.add(pourRecordForPitRequest.convert2Model()));
    }
    return pourRecordList;
  }

  public PageDataResponse<MaxGraphiteResponse> toMaxGraphiteResponse(Page<Map<String, Object>> elements) {
    List<Map<String, Object>> list = elements.getContent();
    List<MaxGraphiteResponse> maxGraphiteResponseList = new ArrayList<>();
    list.forEach(o -> {
      MaxGraphiteResponse response = MaxGraphiteResponse.builder()
          .graphitePre((String) o.get("graphitePre"))
          .maxGraphite((String) o.get("maxGraphite"))
          .build();
      maxGraphiteResponseList.add(response);
    });
    return new PageDataResponse<>(elements.getTotalElements(), elements.getTotalPages(),
        elements.getSize(), elements.getNumber(), maxGraphiteResponseList);
  }

  public List<WheelRecordResponse> toWheelRecordResponseList(List<WheelRecord> wheelRecordList) {
    List<WheelRecordResponse> wheelRecordResponseList = new ArrayList<>();
    wheelRecordList.forEach(wheelRecord -> {
      WheelRecordResponse wheelRecordResponse = new WheelRecordResponse();
      BeanUtils.copyProperties(wheelRecord, wheelRecordResponse);
      wheelRecordResponseList.add(wheelRecordResponse);
    });
    return wheelRecordResponseList;
  }
}
