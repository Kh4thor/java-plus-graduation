package malyshev.egor.controller;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.service.KafkaProducerService;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;
import stats.avro.ActionTypeAvro;
import stats.avro.UserActionAvro;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserActionControllerImpl extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.debug("Received action: user={}, event={}, type={}, timestamp={}",
                request.getUserId(), request.getEventId(), request.getActionType(), request.getTimestamp());

        UserActionAvro avroMessage = convertToAvro(request);
        kafkaProducerService.sendUserAction(avroMessage);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private UserActionAvro convertToAvro(UserActionProto proto) {
        ActionTypeAvro actionType = mapActionType(proto.getActionType());

        // преобразуем google.protobuf.Timestamp в миллисекунды
        long timestampMs = proto.getTimestamp().getSeconds() * 1000 +
                proto.getTimestamp().getNanos() / 1_000_000;

        return UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(actionType)
                .setTimestamp(timestampMs)
                .build();
    }

    private ActionTypeAvro mapActionType(ActionTypeProto protoAction) {
        return switch (protoAction) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> throw new IllegalArgumentException("Unknown action type: " + protoAction);
        };
    }
}