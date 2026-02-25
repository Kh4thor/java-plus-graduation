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

/**
 * Контроллер для управления комментариями от имени их автора (частного пользователя).
 * Предоставляет методы для создания, обновления, удаления и получения комментариев к событию.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentsController {

    private final PrivateCommentService privateCommentService;

    /**
     * Создаёт новый комментарий к указанному событию.
     *
     * @param userId  идентификатор автора комментария
     * @param eventId идентификатор события, к которому оставляется комментарий
     * @param dto     данные нового комментария (текст)
     * @return созданный комментарий (сокращённая версия)
     * @throws malyshev.egor.exception.NotFoundException если пользователь, событие или заявка пользователя не найдены
     * @throws IllegalArgumentException если заявка пользователя не подтверждена или событие не опубликовано
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId,
            @RequestBody @Valid NewCommentDto dto) {
        return privateCommentService.createComment(dto, userId, eventId);
    }

    /**
     * Обновляет существующий комментарий (только автор может редактировать свой комментарий).
     *
     * @param userId    идентификатор пользователя (должен совпадать с автором комментария)
     * @param eventId   идентификатор события, к которому относится комментарий
     * @param commentId идентификатор редактируемого комментария
     * @param dto       новые данные комментария (текст)
     * @return обновлённый комментарий (сокращённая версия)
     * @throws malyshev.egor.exception.NotFoundException если комментарий, пользователь или событие не найдены
     * @throws IllegalArgumentException если пользователь не является автором комментария, комментарий не относится к событию, или событие не опубликовано
     */
    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto patchComment(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody @Valid NewCommentDto dto) {
        return privateCommentService.patchComment(dto, userId, eventId, commentId);
    }

    /**
     * Удаляет комментарий (только автор может удалить свой комментарий).
     *
     * @param userId    идентификатор пользователя (должен совпадать с автором комментария)
     * @param eventId   идентификатор события, к которому относится комментарий
     * @param commentId идентификатор удаляемого комментария
     * @return удалённый комментарий (сокращённая версия)
     * @throws malyshev.egor.exception.NotFoundException если комментарий, пользователь или событие не найдены
     * @throws IllegalArgumentException если пользователь не является автором комментария, комментарий не относится к событию, или событие не опубликовано
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortDto deleteComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        return privateCommentService.deleteCommentByPrivate(userId, eventId, commentId);
    }

    /**
     * Возвращает список всех комментариев к указанному событию, доступных данному пользователю.
     * (Обычно это все комментарии, но с фильтрацией по автору на уровне сервиса).
     *
     * @param userId  идентификатор пользователя, запрашивающего комментарии
     * @param eventId идентификатор события
     * @return список комментариев (сокращённая версия)
     * @throws malyshev.egor.exception.NotFoundException если пользователь или событие не найдены
     */
    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return privateCommentService.getAllCommentsByEventPrivate(userId, eventId);
    }
}