package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.user.NewUserRequest;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.service.AdminUserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления пользователями от имени администратора.
 * Предоставляет эндпоинты для создания, получения списка и удаления пользователей.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUsersController {

    private final AdminUserService adminUserService;

    /**
     * Создаёт нового пользователя.
     *
     * @param req объект с данными нового пользователя (имя, email)
     * @return созданный пользователь с присвоенным идентификатором
     * @throws org.springframework.dao.DataIntegrityViolationException если пользователь с таким email уже существует
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest req) {
        return adminUserService.add(req);
    }

    /**
     * Возвращает список пользователей с возможностью фильтрации по идентификаторам и пагинацией.
     * Сортировка всегда по идентификатору в порядке возрастания.
     *
     * @param ids  список идентификаторов пользователей для фильтрации (необязательный)
     * @param from количество элементов, которое нужно пропустить (для пагинации), по умолчанию 0
     * @param size количество элементов на странице, по умолчанию 10
     * @return список пользователей
     */
    @GetMapping
    public List<UserDto> list(@RequestParam(value = "ids", required = false) List<Long> ids,
                              @RequestParam(defaultValue = "0") int from,
                              @RequestParam(defaultValue = "10") int size) {
        // Жёстко задаём сортировку по id ASC (стабильный порядок)
        var pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return adminUserService.listByIds(ids, pageable);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId идентификатор удаляемого пользователя
     * @throws malyshev.egor.exception.NotFoundException если пользователь с указанным идентификатором не найден
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void del(@PathVariable long userId) {
        adminUserService.delete(userId);
    }
}