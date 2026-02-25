package malyshev.egor.feign.user;

import malyshev.egor.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service",
        contextId = "admin-request-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/admin/users")
public interface AdminUserFeignClient {

    @GetMapping
    List<UserDto> list(@RequestParam(value = "ids", required = false) List<Long> ids,
                       @RequestParam(defaultValue = "0") int from,
                       @RequestParam(defaultValue = "10") int size);
}
