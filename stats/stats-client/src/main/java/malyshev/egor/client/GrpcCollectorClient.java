package malyshev.egor.client;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcCollectorClient {

    @GrpcClient("userActionControllerGrpcClient")
    private UserActionControllerGrpc.UserActionControllerBlockingStub userActionStub;

    public void sendView(long userId, long eventId) {
        Instant now = Instant.now();
        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_VIEW)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build())
                .build();
        try {
            userActionStub.collectUserAction(action);
            log.debug("View sent: userId={}, eventId={}", userId, eventId);
        } catch (StatusRuntimeException e) {
            log.error("Failed to send view: userId={}, eventId={}", userId, eventId, e);
        }
    }

    public void sendLike(long userId, long eventId) {
        Instant now = Instant.now();
        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_LIKE)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build())
                .build();
        try {
            userActionStub.collectUserAction(action);
            log.debug("Like sent: userId={}, eventId={}", userId, eventId);
        } catch (StatusRuntimeException e) {
            log.error("Failed to send like: userId={}, eventId={}", userId, eventId, e);
        }
    }

    public void sendRegister(long userId, long eventId) {
        Instant now = Instant.now();
        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_REGISTER)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(now.getEpochSecond())
                        .setNanos(now.getNano())
                        .build())
                .build();
        try {
            userActionStub.collectUserAction(action);
            log.debug("Register sent: userId={}, eventId={}", userId, eventId);
        } catch (StatusRuntimeException e) {
            log.error("Failed to send register: userId={}, eventId={}", userId, eventId, e);
        }
    }
}