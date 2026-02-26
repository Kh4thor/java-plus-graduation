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

/**
 * Публичный контроллер для работы с комментариями к событиям.
 * Предоставляет эндпоинты для получения списка комментариев по идентификатору события.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {

    private final PublicCommentService publicCommentService;

    /**
     * Возвращает список всех комментариев к указанному событию.
     * Доступно без аутентификации.
     *
     * @param eventId идентификатор события, для которого запрашиваются комментарии
     * @return список комментариев в сокращённом представлении
     * @throws malyshev.egor.exception.NotFoundException если событие с указанным идентификатором не существует
     */
    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return publicCommentService.getAllCommentsByEventPublic(eventId);
    }
}