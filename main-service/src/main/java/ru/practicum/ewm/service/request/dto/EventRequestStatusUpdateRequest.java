package ru.practicum.ewm.service.request.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;  // какие заявки трогаем

    private Status status;

    public enum Status {
        CONFIRMED,
        REJECTED
    }
}