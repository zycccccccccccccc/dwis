package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.business.domain.model.AuthorityTree;

public interface AuthorityTreeRepository extends JpaRepository<AuthorityTree, String> {


  @Query(value = "select * from authority a where a.parent_id is null", nativeQuery = true)
  List<AuthorityTree> findLevelOne();
}
