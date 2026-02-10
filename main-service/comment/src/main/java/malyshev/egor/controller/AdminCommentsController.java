package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.comment.CommentFullDto;
import malyshev.egor.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentsController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return commentService.getAllCommentsByEventAdmin(eventId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto deleteComment(@PathVariable Long eventId, @PathVariable Long commentId) {
        return commentService.deleteCommentByAdmin(eventId, commentId);
    }

}
