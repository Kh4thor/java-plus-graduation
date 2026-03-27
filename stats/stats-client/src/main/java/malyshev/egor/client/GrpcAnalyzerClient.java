package malyshev.egor.client;

import lombok.extern.slf4j.Slf4j;
import malyshev.egor.stats.proto.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.StreamSupport;

/**
 * gRPC-клиент для взаимодействия с сервисом рекомендаций.
 * <p>Использует блокирующий stub для вызова методов:
 * <ul>
 *     <li>{@code getInteractionsCount} — получение рейтинга события</li>
 *     <li>{@code getRecommendationsForUser} — получение рекомендаций для пользователя</li>
 *     <li>{@code getSimilarEvents} — получение похожих событий</li>
 * </ul>
 * Все методы работают с серверным стримингом (server streaming), возвращаемым в виде {@code Iterator}.
 * Для удобной обработки данных применяется Stream API через {@link Spliterator}.
 * </p>
 */
@Slf4j
@Component
public class GrpcAnalyzerClient {

    /**
     * Блокирующий stub для вызова gRPC-сервиса {@code RecommendationsService}.
     * Имя клиента настраивается в {@code application.yml} через {@code grpc.client.analyzerGrpcClient}.
     */
    @GrpcClient("analyzerGrpcClient")
    private RecommendationsServiceGrpc.RecommendationsServiceBlockingStub analyzerStub;

    /**
     * Получает рейтинг (score) события по его идентификатору.
     * <p>Запрос отправляется методом {@code getInteractionsCount}, который возвращает поток с одним элементом.
     * Если поток пуст, возвращается 0.0.</p>
     *
     * @param eventId идентификатор события
     * @return рейтинг события (от 0.0 до …) или 0.0, если событие не найдено
     */
    public double getEventRating(long eventId) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addEventId(eventId)
                .build();
        Iterator<RecommendedEventProto> interactionsCount = analyzerStub.getInteractionsCount(request);
        if (interactionsCount.hasNext()) {
            return interactionsCount.next().getScore();
        }
        return 0.0;
    }

    /**
     * Получает список рекомендаций для пользователя.
     * <p>Вызывает серверный стриминг-метод {@code getRecommendationsForUser}, который возвращает поток
     * {@link RecommendedEventProto}. Результаты извлекаются в список идентификаторов событий.</p>
     *
     * @param userId     идентификатор пользователя
     * @param maxResults максимальное количество возвращаемых рекомендаций
     * @return список идентификаторов событий (не {@code null}, может быть пустым)
     */
    public List<Long> getRecommendations(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        Iterator<RecommendedEventProto> recommendations = analyzerStub.getRecommendationsForUser(request);
        List<Long> ids = new ArrayList<>();
        while (recommendations.hasNext()) {
            ids.add(recommendations.next().getEventId());
        }
        return ids;
    }

    /**
     * Получает список событий, похожих на заданное, с учётом контекста пользователя.
     * <p>Вызывает серверный стриминг-метод {@code getSimilarEvents}. Для обработки данных используется
     * {@link Spliterator} и Stream API, что позволяет лаконично преобразовать итератор в поток.</p>
     *
     * @param eventId    идентификатор исходного события
     * @param userId     идентификатор пользователя (может влиять на персонализацию похожих событий)
     * @param maxResults максимальное количество возвращаемых похожих событий
     * @return список идентификаторов похожих событий (не {@code null}, может быть пустым)
     */
    public List<Long> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        // Получаем итератор из gRPC-стрима
        Iterator<RecommendedEventProto> similarEvents = analyzerStub.getSimilarEvents(request);

        // Оборачиваем итератор в Spliterator (с сохранением порядка) для использования со StreamAPI
        Spliterator<RecommendedEventProto> spliterator =
                Spliterators.spliteratorUnknownSize(similarEvents, Spliterator.ORDERED);

        return StreamSupport.stream(spliterator, false)
                .map(RecommendedEventProto::getEventId)
                .toList();
    }
}