package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Graphite;

public interface GraphiteRepository extends JpaRepository<Graphite, Integer>, JpaSpecificationExecutor<Graphite> {

  @Query(value = "SELECT * FROM max_graphite WHERE cd = :cd ORDER BY graphitePre", nativeQuery = true)
  Page<Map<String, Object>> findMaxGraphite(@Param("cd") Integer cd, Pageable pageable);

  Optional<Graphite> findByGraphite(String graphite);

  Optional<Graphite> findByGrId(Integer grId);

  @Query(value = "SELECT top (:limit) * FROM graphite WHERE graphite LIKE :graphite% AND status IN :statusList ORDER BY graphite", nativeQuery = true)
  List<Graphite> findGraphiteList(@Param("graphite") String graphite, @Param("statusList") List<Integer> statusList,
      @Param("limit") Integer limit);

  @Query(value = "SELECT graphite FROM graphite WHERE cd = :cd AND status = 2 ORDER BY graphite", nativeQuery = true)
  List<String> findGraphiteCDList(@Param("cd") Integer cd);
}
