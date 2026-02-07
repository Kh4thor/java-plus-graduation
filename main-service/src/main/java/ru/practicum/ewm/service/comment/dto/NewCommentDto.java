package ru.practicum.ewm.service.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    @Size(min = 1, max = 250)
    private String text;
}
