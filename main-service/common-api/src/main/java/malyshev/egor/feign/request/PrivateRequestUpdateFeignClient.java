package malyshev.egor.feign.request;

import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
@FeignClient(
        name = "request-service",
        contextId = "private-request-service-update",
        url = "${gateway.url:http://localhost:8080}",
        path = "/users/{userId}/events/{eventId}/requests"
)

/**
 * Отдельный клиент для обновления статуса заявок.
 * Вынесен из {@link PrivateRequestFeignClient} из-за ошибки создания бина
 * при наличии нескольких методов с разными параметрами.
 */

public interface PrivateRequestUpdateFeignClient {
    @PatchMapping
    EventRequestStatusUpdateResult update(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventRequestStatusUpdateRequest body
    );
}