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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUsersController {

    private final AdminUserService adminUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest req) {
        return adminUserService.add(req);
    }

    @GetMapping
    public List<UserDto> list(@RequestParam(value = "ids", required = false) List<Long> ids,
                              @RequestParam(defaultValue = "0") int from,
                              @RequestParam(defaultValue = "10") int size) {
        // Жёстко задаём сортировку по id ASC (стабильный порядок)
        var pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return adminUserService.listByIds(ids, pageable);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void del(@PathVariable long userId) {
        adminUserService.delete(userId);
    }
}