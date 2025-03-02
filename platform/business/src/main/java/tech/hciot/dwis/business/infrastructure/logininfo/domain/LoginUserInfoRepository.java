package tech.hciot.dwis.business.infrastructure.logininfo.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.infrastructure.logininfo.domain.model.LoginUserInfo;

public interface LoginUserInfoRepository extends JpaRepository<LoginUserInfo, String>, JpaSpecificationExecutor<LoginUserInfo> {

  Optional<LoginUserInfo> findByUsername(String username);
}
