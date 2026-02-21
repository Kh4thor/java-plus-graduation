package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.comment.CommentFullDto;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.model.Comment;

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
                .commentator(comment.getCommentator() == null ?
                        null : UserMapper.toUserShort(comment.getCommentator()))
                .publishedOn(comment.getPublishedOn())
                .deleted(comment.isDeleted())
                .build();
    }
}