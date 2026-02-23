package malyshev.egor.repository;

import malyshev.egor.model.ParticipationRequest;
import malyshev.egor.dto.request.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester(Long requesterId);

    boolean existsByRequesterAndEvent(Long requesterId, Long eventId);

    Long countByEventAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEvent(Long eventId);

    Optional<ParticipationRequest> findByRequesterAndEvent(Long requesterId, Long eventId);
}
