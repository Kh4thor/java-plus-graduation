package ru.practicum.ewm.service.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentShortDto {
    private Long id;
    private String text;
    private User commentator;
    private LocalDateTime publishedOn;
}
