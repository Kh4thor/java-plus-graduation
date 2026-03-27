package malyshev.egor.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.stats.proto.ActionTypeProto;
import malyshev.egor.stats.proto.UserActionControllerGrpc;
import malyshev.egor.stats.proto.UserActionProto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * gRPC-клиент для отправки действий пользователя (просмотры, лайки, регистрации)
 * в сервис-коллектор. Использует блокирующий stub для вызова метода {@code collectUserAction}.
 * <p>При возникновении ошибок gRPC (например, недоступность сервера) исключения логируются,
 * но не пробрасываются дальше, чтобы не нарушать основной поток приложения.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcCollectorClient {

    /**
     * Блокирующий stub для вызова методов gRPC-сервиса {@code UserActionController}.
     * Имя клиента настраивается в {@code application.yml} через параметр {@code grpc.client.userActionControllerGrpcClient}.
     */
    @GrpcClient("userActionControllerGrpcClient")
    private UserActionControllerGrpc.UserActionControllerBlockingStub userActionStub;

    /**
     * Отправляет действие просмотра мероприятия.
     *
     * @param userId  идентификатор пользователя, совершившего действие
     * @param eventId идентификатор мероприятия, которое было просмотрено
     */
    public void sendView(long userId, long eventId) {
        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_VIEW)
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()))
                .build();
        try {
            Empty response = userActionStub.collectUserAction(action);
            log.debug("View sent: userId={}, eventId={}", userId, eventId);
        } catch (StatusRuntimeException e) {
            log.error("Failed to send view: userId={}, eventId={}", userId, eventId, e);
        }
    }

    /**
     * Отправляет лайк мероприятия.
     *
     * @param userId  идентификатор пользователя, поставившего лайк
     * @param eventId идентификатор мероприятия, которое было отмечено лайком
     */
    public void sendLike(long userId, long eventId) {
        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_LIKE)
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()))
                .build();
        try {
            Empty response = userActionStub.collectUserAction(action);
            log.debug("Like sent: userId={}, eventId={}", userId, eventId);
        } catch (StatusRuntimeException e) {
            log.error("Failed to send like: userId={}, eventId={}", userId, eventId, e);
        }
    }

    /**
     * Отправляет регистрацию пользователя на мероприятие.
     *
     * @param userId  идентификатор пользователя, зарегистрировавшегося
     * @param eventId идентификатор мероприятия, на которое выполнена регистрация
     */
    public void sendRegister(long userId, long eventId) {
        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_REGISTER)
                .setTimestamp(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()))
                .build();
        try {
            Empty response = userActionStub.collectUserAction(action);
            log.debug("Register sent: userId={}, eventId={}", userId, eventId);
        } catch (StatusRuntimeException e) {
            log.error("Failed to send register: userId={}, eventId={}", userId, eventId, e);
        }
    }
}