package ru.practicum.ewm.service.event.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
    private double lat;
    private double lon;
}
