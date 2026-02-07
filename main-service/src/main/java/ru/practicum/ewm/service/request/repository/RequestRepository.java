package ru.practicum.ewm.service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.request.model.ParticipationRequest;
import ru.practicum.ewm.service.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(long requesterId);

    boolean existsByRequesterIdAndEventId(long requesterId, long eventId);

    long countByEventIdAndStatus(long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(long eventId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(long requesterId, long eventId);
}


