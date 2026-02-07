package ru.practicum.ewm.service.request.service;

import ru.practicum.ewm.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto createRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateEventRequests(long userId, long eventId, EventRequestStatusUpdateRequest body);
}
