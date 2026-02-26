package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.comment.CommentFullDto;
import malyshev.egor.service.admins.AdminCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления комментариями от имени администратора.
 * Предоставляет эндпоинты для получения всех комментариев к событию и удаления комментария.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentsController {

    private final AdminCommentService adminCommentService;

    /**
     * Возвращает список всех комментариев к указанному событию.
     * Доступно только администратору.
     *
     * @param eventId идентификатор события
     * @return список комментариев к событию
     * @throws malyshev.egor.exception.NotFoundException если событие не найдено
     */
    @GetMapping
    public List<CommentFullDto> getAllCommentsByEvent(
            @PathVariable Long eventId
    ) {
        return adminCommentService.getAllCommentsByEventAdmin(eventId);
    }

    /**
     * Удаляет комментарий по его идентификатору.
     * Доступно только администратору.
     *
     * @param eventId   идентификатор события, к которому относится комментарий
     * @param commentId идентификатор комментария
     * @return удалённый комментарий
     * @throws malyshev.egor.exception.NotFoundException если комментарий или событие не найдены
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto deleteComment(@PathVariable Long eventId, @PathVariable Long commentId) {
        return adminCommentService.deleteCommentByAdmin(eventId, commentId);
    }
}