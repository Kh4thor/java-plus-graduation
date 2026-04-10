package malyshev.egor.repository;

import malyshev.egor.model.UserAction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT u.eventId FROM UserAction u WHERE u.userId = :userId ORDER BY u.lastInteraction DESC")
    List<Long> findRecentEventIdsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT u.eventId FROM UserAction u WHERE u.userId = :userId")
    List<Long> findAllEventIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT u.eventId, SUM(u.weight) FROM UserAction u WHERE u.eventId IN :eventIds GROUP BY u.eventId")
    List<Object[]> sumWeightsByEventIds(@Param("eventIds") List<Long> eventIds);

    List<UserAction> findAllByUserId(Long userId);
}