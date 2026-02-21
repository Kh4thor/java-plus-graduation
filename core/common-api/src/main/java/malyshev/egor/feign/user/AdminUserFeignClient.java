package malyshev.egor.feign.user;

import jakarta.validation.Valid;
import malyshev.egor.dto.user.NewUserRequest;
import malyshev.egor.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service",
        contextId = "admin-request-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/admin/users")
public interface AdminUserFeignClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest req);

    @GetMapping
    public List<UserDto> list(@RequestParam(value = "ids", required = false) List<Long> ids,
                              @RequestParam(defaultValue = "0") int from,
                              @RequestParam(defaultValue = "10") int size);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void del(@PathVariable long userId);
}
