package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.service.publics.PublicCommentService;
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

    private final PublicCommentService publicCommentService;

    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return publicCommentService.getAllCommentsByEventPublic(eventId);
    }
}
