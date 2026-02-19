package malyshev.egor.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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