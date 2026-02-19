package malyshev.egor.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.comment.dto.CommentShortDto;
import malyshev.egor.comment.dto.NewCommentDto;
import malyshev.egor.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentsController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid NewCommentDto dto) {
        return commentService.createComment(dto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto patchComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody @Valid NewCommentDto dto) {
        return commentService.patchComment(dto, userId, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto deleteComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        return commentService.deleteCommentByPrivate(userId, eventId, commentId);
    }

    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return commentService.getAllCommentsByEventPublic(eventId);
    }
}
