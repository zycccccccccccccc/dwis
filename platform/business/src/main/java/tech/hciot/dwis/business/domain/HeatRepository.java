package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Heat;

public interface HeatRepository extends JpaRepository<Heat, Integer>, JpaSpecificationExecutor<Heat> {

  List<Heat> findByXh(Integer xh);

  @Query(value = "SELECT top 1 * FROM heat WHERE wheel_serial_1 = :wheelSerial OR wheel_serial_2 = :wheelSerial ORDER BY id DESC", nativeQuery = true)
  Heat findMaxHeat(@Param("wheelSerial") String wheelSerial);

  @Query(value = "SELECT top 1 * FROM heat WHERE (wheel_serial_1 = :wheelSerial OR wheel_serial_2 = :wheelSerial) " +
      "AND id != :heatId ORDER BY id DESC", nativeQuery = true)
  Heat findMaxHeat(@Param("wheelSerial") String wheelSerial, @Param("heatId") Integer heatId);

  @Query(value = "SELECT top 1 * FROM heat WHERE heat_line = :heatLine ORDER BY id DESC", nativeQuery = true)
  Optional<Heat> findMaxHeatByHeatLine(@Param("heatLine") Integer heatLine);

  @Query(value = "UPDATE heat SET mec_serial = :flag + SUBSTRING(mec_serial,2,LEN(mec_serial)) WHERE mec_serial LIKE "
    + ":mecSerial%", nativeQuery = true)
  @Modifying
  void updateMecSerial(@Param("flag") String flag, @Param("mecSerial") String mecSerial);
}
