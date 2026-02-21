package malyshev.egor.feign.request;

import malyshev.egor.dto.request.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@FeignClient(
        name = "request-service",
        contextId = "private-request-service-list",
        url = "${gateway.url:http://localhost:8080}",
        path = "/users/{userId}/events/{eventId}/requests"
)
public interface PrivateRequestFeignClient {

    @GetMapping
    List<ParticipationRequestDto> list(
            @PathVariable long userId,
            @PathVariable long eventId
    );
}