package malyshev.egor.ewm.stats.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import malyshev.egor.ewm.stats.dto.ViewStatsDto;
import malyshev.egor.ewm.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new malyshev.egor.ewm.stats.dto.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
            "FROM EndpointHit as h " +
            "WHERE h.hitTimestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri")
    List<ViewStatsDto> getStats(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    @Query("SELECT new malyshev.egor.ewm.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit as h " +
            "WHERE h.hitTimestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri")
    List<ViewStatsDto> getUniqueStats(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    @Query("SELECT new malyshev.egor.ewm.stats.dto.ViewStatsDto(h.app, h.uri, count(h.ip)) " +
            "FROM EndpointHit as h " +
            "WHERE h.uri IN (:uris) AND h.hitTimestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri")
    List<ViewStatsDto> getStatsByUris(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);

    @Query("SELECT new malyshev.egor.ewm.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit as h " +
            "WHERE h.uri IN :uris AND h.hitTimestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri")
    List<ViewStatsDto> getUniqueStatsByUris(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);
}
