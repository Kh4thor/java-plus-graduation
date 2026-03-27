package malyshev.egor.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.service.RecommendationService;
import malyshev.egor.stats.proto.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsServiceGrpc.RecommendationsServiceImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        long userId = request.getUserId();
        int maxResults = request.getMaxResults();
        log.debug("getRecommendationsForUser: userId={}, maxResults={}", userId, maxResults);

        List<RecommendationService.RecommendedEvent> recommendationsForUser =
                recommendationService.getRecommendationsForUser(userId, maxResults);

        for (RecommendationService.RecommendedEvent recommendedEvent : recommendationsForUser) {
            RecommendedEventProto proto = RecommendedEventProto.newBuilder()
                    .setEventId(recommendedEvent.eventId())
                    .setScore(recommendedEvent.score())
                    .build();
            responseObserver.onNext(proto);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        long eventId = request.getEventId();
        long userId = request.getUserId();
        int maxResults = request.getMaxResults();
        log.debug("getSimilarEvents: eventId={}, userId={}, maxResults={}", eventId, userId, maxResults);

        List<RecommendationService.RecommendedEvent> recommendationsForUser =
                recommendationService.getSimilarEvents(eventId, userId, maxResults);

        for (RecommendationService.RecommendedEvent recommendedEvent : recommendationsForUser) {
            RecommendedEventProto proto = RecommendedEventProto.newBuilder()
                    .setEventId(recommendedEvent.eventId())
                    .setScore(recommendedEvent.score())
                    .build();
            responseObserver.onNext(proto);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        List<Long> eventIds = request.getEventIdList();
        log.debug("getInteractionsCount: eventIds={}", eventIds);

        List<RecommendationService.RecommendedEvent> recommendationsForUser =
                recommendationService.getInteractionsCount(eventIds);

        for (RecommendationService.RecommendedEvent rec : recommendationsForUser) {
            RecommendedEventProto proto = RecommendedEventProto.newBuilder()
                    .setEventId(rec.eventId())
                    .setScore(rec.score())
                    .build();
            responseObserver.onNext(proto);
        }
        responseObserver.onCompleted();
    }
}