package malyshev.egor.request.repository;

import malyshev.egor.request.model.ParticipationRequest;
import malyshev.egor.request.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(long requesterId);

    boolean existsByRequesterIdAndEventId(long requesterId, long eventId);

    long countByEventIdAndStatus(long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(long eventId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(long requesterId, long eventId);
}


