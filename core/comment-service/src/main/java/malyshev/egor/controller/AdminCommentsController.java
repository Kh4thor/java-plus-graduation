package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.comment.CommentFullDto;
import malyshev.egor.service.admins.AdminCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentsController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public List<CommentFullDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return adminCommentService.getAllCommentsByEventAdmin(eventId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto deleteComment(@PathVariable Long eventId, @PathVariable Long commentId) {
        return adminCommentService.deleteCommentByAdmin(eventId, commentId);
    }

}
