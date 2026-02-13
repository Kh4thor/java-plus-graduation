package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.dto.comment.NewCommentDto;
import malyshev.egor.service.privates.PrivateCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentsController {

    private final PrivateCommentService privateCommentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid NewCommentDto dto) {
        return privateCommentService.createComment(dto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto patchComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody @Valid NewCommentDto dto) {
        return privateCommentService.patchComment(dto, userId, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto deleteComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        return privateCommentService.deleteCommentByPrivate(userId, eventId, commentId);
    }

    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return privateCommentService.getAllCommentsByEventPrivate(userId, eventId);
    }
}
