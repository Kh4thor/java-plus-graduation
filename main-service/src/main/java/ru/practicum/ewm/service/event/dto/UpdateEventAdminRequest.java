package ru.practicum.ewm.service.event.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "annotation length must be 20..2000")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "description length must be 20..7000")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "title length must be 3..120")
    private String title;

    private String stateAction;
}
