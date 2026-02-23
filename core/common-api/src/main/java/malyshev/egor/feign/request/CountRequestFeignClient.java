package malyshev.egor.feign.request;

import malyshev.egor.dto.request.RequestStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "request-service",
        contextId = "request-stats",
        path = "/events/{eventId}/requests/count")
public interface CountRequestFeignClient {

    @GetMapping
    Long countByEventAndStatus(@PathVariable Long eventId, @RequestParam RequestStatus status);
}
