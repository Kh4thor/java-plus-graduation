package malyshev.egor.mapper;

import lombok.RequiredArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.comment.CommentFullDto;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.dto.user.UserShortDto;
import malyshev.egor.model.Comment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final InteractionApiManager interactionApiManager;

    public CommentShortDto toShortDto(Comment comment) {
        if (comment == null)
            return null;

        UserDto userDto = interactionApiManager.getUserByAdmin(comment.getCommentator());
        UserShortDto commentator = UserShortDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();

        return CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commentator(commentator)
                .publishedOn(comment.getPublishedOn())
                .build();
    }

    public CommentFullDto toFullDto(Comment comment) {
        if (comment == null)
            return null;

        UserDto userDto = interactionApiManager.getUserByAdmin(comment.getCommentator());
        UserShortDto commentator = UserShortDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();

        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commentator(commentator)
                .publishedOn(comment.getPublishedOn())
                .deleted(comment.isDeleted())
                .build();
    }
}