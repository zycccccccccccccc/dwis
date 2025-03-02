package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Account;

public interface AccountsRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

  Optional<Account> findByUsername(String userName);

  @Query(value = "select distinct role_name from v_account_authority where username = :username", nativeQuery = true)
  List<String> findAccountRoles(@Param("username") String username);

  @Transactional
  @Modifying
  @Query(value = "delete from account where username = :username", nativeQuery = true)
  void deleteByUsername(@Param("username") String username);
}
