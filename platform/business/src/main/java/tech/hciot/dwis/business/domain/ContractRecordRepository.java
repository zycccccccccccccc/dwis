package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.ContractRecord;

public interface ContractRecordRepository extends JpaRepository<ContractRecord, Integer>,
    JpaSpecificationExecutor<ContractRecord> {

  List<ContractRecord> findByEnabled(Integer enabled);
}
