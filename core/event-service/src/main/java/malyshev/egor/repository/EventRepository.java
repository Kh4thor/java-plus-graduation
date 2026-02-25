package malyshev.egor.repository;

import malyshev.egor.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByInitiator(Long userId);

    List<Event> findByIdIn(List<Long> ids);

    void deleteByCategory(Long categoryId);

    boolean existsByCategory(Long categoryId);
}