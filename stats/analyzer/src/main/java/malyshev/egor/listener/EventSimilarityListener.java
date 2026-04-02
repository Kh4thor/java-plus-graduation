//package malyshev.egor.listener;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import malyshev.egor.service.EventSimilarityService;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import stats.avro.EventSimilarityAvro;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class EventSimilarityListener {
//
//    private final EventSimilarityService eventSimilarityService;
//
//    @KafkaListener(
//            topics = "#{kafkaProperties.topics.eventsSimilarityTopic}",
//            containerFactory = "eventSimilarityKafkaListenerContainerFactory"
//    )
//    public void consumeEventSimilarity(EventSimilarityAvro similarity) {
//        eventSimilarityService.process(similarity);
//    }
//}
