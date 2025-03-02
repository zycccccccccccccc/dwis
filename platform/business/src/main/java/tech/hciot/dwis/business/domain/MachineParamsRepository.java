package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.MachineParams;

import java.util.List;
import java.util.Optional;

public interface MachineParamsRepository extends JpaRepository<MachineParams, Integer>, JpaSpecificationExecutor<MachineParams> {

  List<MachineParams> findByProcessAndEnabled(String process, Integer enabled);

}
