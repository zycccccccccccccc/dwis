package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

  List<Authority> findByIdIn(List<String> idList);

  @Query(value = "SELECT id, authority_name, descritpion FROM v_authority_leaf"
      + " WHERE role_id = :roleId", nativeQuery = true)
  List<Authority> findAllAuthorityOfLeaf(@Param("roleId") String roleId);
}
