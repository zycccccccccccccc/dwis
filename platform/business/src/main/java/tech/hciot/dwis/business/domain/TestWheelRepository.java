package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.TestWheel;

public interface TestWheelRepository extends JpaRepository<TestWheel, Integer>, JpaSpecificationExecutor<TestWheel> {

  @Query(value = "SELECT top (:limit) * FROM test_wheel " +
    "WHERE wheel_serial LIKE :wheelSerial% AND location = :location AND enabled = 1 ORDER BY wheel_serial", nativeQuery = true)
  List<TestWheel> findByWheelSerial(@Param("wheelSerial") String wheelSerial,
                                    @Param("location") String location,
                                    @Param("limit") Integer limit);
}
