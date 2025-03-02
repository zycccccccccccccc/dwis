package tech.hciot.dwis.business.application;

import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.AccountDetailRepository;
import tech.hciot.dwis.business.domain.AccountsRepository;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.domain.model.AccountDetail;
import tech.hciot.dwis.business.interfaces.dto.AddStaffRequest;
import tech.hciot.dwis.business.interfaces.dto.ModifyStaffRequest;
import tech.hciot.dwis.business.interfaces.dto.StaffDetail;
import tech.hciot.dwis.business.interfaces.dto.StaffIdAndNameResponse;

@Service
@Slf4j
public class StaffService {

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private AccountDetailRepository accountDetailRepository;

  @Autowired
  private PasswordEncoder bCryptPasswordEncoder;

  public Page<StaffDetail> find(String operatorId,
      String operatorName,
      Integer status,
      Integer depId,
      Integer currentPage,
      Integer pageSize) {
    Specification<AccountDetail> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(operatorId)) {
        list.add(criteriaBuilder.like(root.get("username"), "%" + operatorId + "%"));
      }
      if (StringUtils.isNotBlank(operatorName)) {
        list.add(criteriaBuilder.like(root.get("nickname"), "%" + operatorName + "%"));
      }
      if (status != null) {
        list.add(criteriaBuilder.equal(root.get("status"), status));
      }
      if (depId != null) {
        list.add(criteriaBuilder.equal(root.get("depId"), depId));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    return accountDetailRepository.findAll(specification, PageRequest.of(currentPage, pageSize))
        .map(account -> StaffDetail.builder()
            .id(account.getId())
            .teamLeaderId(account.getTeamLeaderId())
            .createTime(account.getCreateTime())
            .depId(account.getDepId())
            .depName(account.getDepName())
            .stationId(account.getStationId())
            .stationName(account.getStationName())
            .email(account.getEmail())
            .location(new JSONArray(Arrays.asList(
              StringUtils.split(account.getLocation().substring(1, account.getLocation().length() - 1), ","))))
            .operatorId(account.getUsername())
            .operatorName(account.getNickname())
            .memo(account.getMemo())
            .status(account.getStatus())
            .mobile(account.getMobile())
            .enabled(account.getEnabled())
            .isLeader(account.getIsLeader())
            .build());
  }

  public List<StaffIdAndNameResponse> findOperatorIdList(String operatorId, String location, Boolean isLeader,
      Integer depId, Integer stationId, Integer limit) {
    Specification<Account> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(operatorId)) {
        list.add(criteriaBuilder.like(root.get("username"), operatorId + "%"));
      }
      if (StringUtils.isNotBlank(location)) {
        Predicate p1 = criteriaBuilder.like(root.get("location"), "%," + location + ",%");
        Predicate p2 = criteriaBuilder.equal(root.get("location"), ",all,");
        list.add(criteriaBuilder.or(p1, p2));
      }
      if (isLeader != null) {
        list.add(criteriaBuilder.equal(root.get("isLeader"), isLeader ? 1 : 0));
      }
      if (depId != null) {
        list.add(criteriaBuilder.equal(root.get("depId"), depId));
      }
      if (stationId != null) {
        list.add(criteriaBuilder.equal(root.get("stationId"), stationId));
      }
      list.add(criteriaBuilder.equal(root.get("enabled"), 1));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    Stream<Account> accountStream = accountsRepository.findAll(specification).stream();
    return accountStream.limit(limit).map(account -> StaffIdAndNameResponse
        .builder()
        .operatorId(account.getUsername())
        .operatorName(account.getNickname())
        .build())
        .collect(Collectors.toList());
  }

  @Transactional
  public Account add(AddStaffRequest request) {
    accountsRepository.findByUsername(request.getOperatorId()).ifPresent(staff -> {
      throw PlatformException.badRequestException("工号已存在: " + request.getOperatorId());
    });

    Account account = new Account();
    account.setId(request.getOperatorId());
    account.setUsername(request.getOperatorId());
    account.setTeamLeaderId(request.getTeamLeaderId());
    account.setLocation("," + StringUtils.join(request.getLocation(), ",") + ",");
    account.setDepId(request.getDepId());
    account.setStationId(request.getStationId());
    account.setMobile(request.getMobile());
    account.setMemo(request.getMemo());
    account.setEmail(request.getEmail());
    account.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
    account.setNickname(request.getOperatorName());
    account.setStatus(Account.STATUS_ENABLED);
    account.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
    account.setIsLeader(request.getIsLeader() == null ? 0 : request.getIsLeader());
    accountService.createAccountAndRole(account, request.getRoleId());

    return account;
  }

  @Transactional
  public void modify(String id, ModifyStaffRequest newStaff) {
    accountsRepository.findByUsername(newStaff.getOperatorId()).ifPresent(staff1 -> {
      if (!staff1.getId().equals(id)) {
        throw PlatformException.badRequestException("工号已存在: " + newStaff.getOperatorId());
      }
    });
    accountsRepository.findById(id).map(staff -> {
      staff.setDepId(newStaff.getDepId());
      staff.setStationId(newStaff.getStationId());
      staff.setEmail(newStaff.getEmail());
      staff.setNickname(newStaff.getOperatorName());
      staff.setLocation("," + StringUtils.join(newStaff.getLocation(), ",") + ",");
      staff.setMemo(newStaff.getMemo());
      staff.setTeamLeaderId(newStaff.getTeamLeaderId());
      staff.setMobile(newStaff.getMobile());
      staff.setEnabled(newStaff.getEnabled() == null ? 1 : newStaff.getEnabled());
      staff.setIsLeader(newStaff.getIsLeader() == null ? 0 : newStaff.getIsLeader());

      accountsRepository.save(staff);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("员工信息不存在: " + newStaff.getOperatorName()));
  }

  @Transactional
  public void delete(String id) {
    accountsRepository.findById(id).ifPresent(staff -> {
      Account account = accountService.getAccountById(id);
      accountService.deleteAccountById(account.getId());
    });
  }
}
