package malyshev.egor.feign.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "request-service",
        contextId = "request-stats",
        url = "${gateway.url:http://localhost:8080}")
public interface ConfirmedRequestsFeignClient {

    @GetMapping("/events/{eventId}/requests/confirmed")
    Long countConfirmedRequests(@PathVariable("eventId") Long eventId);
}