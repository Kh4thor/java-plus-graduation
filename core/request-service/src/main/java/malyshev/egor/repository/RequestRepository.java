package malyshev.egor.repository;

import malyshev.egor.dto.request.RequestStatus;
import malyshev.egor.model.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester(Long requesterId);

    boolean existsByRequesterAndEvent(Long requesterId, Long eventId);

    Long countByEventAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEvent(Long eventId);
}
