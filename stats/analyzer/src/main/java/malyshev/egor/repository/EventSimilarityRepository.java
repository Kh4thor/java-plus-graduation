package malyshev.egor.repository;

import malyshev.egor.model.EventSimilarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    // поиск существующей записи о сходстве
    Optional<EventSimilarity> findByEventAAndEventB(Long eventA, Long eventB);

    // Для GetRecommendationsForUser и GetSimilarEvents: все сходства, в которых участвует мероприятие eventId
    @Query("SELECT s FROM EventSimilarity s WHERE s.eventA = :eventId OR s.eventB = :eventId")
    List<EventSimilarity> findAllByEventId(@Param("eventId") Long eventId);

    // обновление сходства
    @Modifying
    @Transactional
    @Query("UPDATE EventSimilarity s SET s.similarity = :similarity, s.timestamp = :timestamp WHERE s.eventA = :eventA AND s.eventB = :eventB")
    int updateSimilarity(@Param("eventA") Long eventA, @Param("eventB") Long eventB,
                         @Param("similarity") Double similarity, @Param("timestamp") Instant timestamp);
}