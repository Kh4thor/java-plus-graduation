package malyshev.egor.comment.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.comment.dto.CommentShortDto;
import malyshev.egor.comment.service.CommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return commentService.getAllCommentsByEventPublic(eventId);
    }
}
