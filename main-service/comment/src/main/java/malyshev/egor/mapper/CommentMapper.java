package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.ewm.service.comment.dto.CommentFullDto;
import malyshev.egor.ewm.service.comment.dto.CommentShortDto;
import malyshev.egor.ewm.service.comment.model.Comment;
import malyshev.egor.ewm.service.event.mapper.EventMapper;

@UtilityClass
public class CommentMapper {

    public static CommentShortDto toShortDto(Comment comment) {
        if (comment == null)
            return null;

        return CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commentator(comment.getCommentator())
                .publishedOn(comment.getPublishedOn())
                .build();
    }

    public static CommentFullDto toFullDto(Comment comment) {
        if (comment == null)
            return null;

        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commentator(comment.getCommentator() == null ? null : EventMapper.toUserShort(comment.getCommentator()))
                .publishedOn(comment.getPublishedOn())
                .deleted(comment.isDeleted())
                .build();
    }
}