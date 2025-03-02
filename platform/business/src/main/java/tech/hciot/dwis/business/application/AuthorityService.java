package tech.hciot.dwis.business.application;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.AuthorityRepository;
import tech.hciot.dwis.business.domain.AuthorityTreeRepository;
import tech.hciot.dwis.business.domain.model.Authority;
import tech.hciot.dwis.business.domain.model.AuthorityTree;

@Service
public class AuthorityService {

  @Autowired
  private AuthorityTreeRepository authorityTreeRepository;

  @Autowired
  private AuthorityRepository authorityRepository;

  public List<AuthorityTree> getAuthorityTree() {
    return authorityTreeRepository.findLevelOne();
  }

  public List<Authority> findByIdList(List<String> idList) {
    return authorityRepository.findByIdIn(idList);
  }
}
