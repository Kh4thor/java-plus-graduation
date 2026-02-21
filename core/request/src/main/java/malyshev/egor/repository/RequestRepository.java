package malyshev.egor.repository;

import malyshev.egor.model.request.ParticipationRequest;
import malyshev.egor.model.request.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);
}


