package malyshev.egor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.model.EventSimilarity;
import malyshev.egor.repository.EventSimilarityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stats.avro.EventSimilarityAvro;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventSimilarityService {

    private final EventSimilarityRepository eventSimilarityRepository;

    @Transactional
    public void process(EventSimilarityAvro similarityAvro) {
        long eventA = similarityAvro.getEventA();
        long eventB = similarityAvro.getEventB();
        double score = similarityAvro.getScore();
        Instant timestamp = similarityAvro.getTimestamp();

        if (eventA > eventB) {
            long temp = eventA;
            eventA = eventB;
            eventB = temp;
        }

        Optional<EventSimilarity> existing = eventSimilarityRepository.findByEventAAndEventB(eventA, eventB);
        if (existing.isPresent()) {
            EventSimilarity eventSimilarity = existing.get();
            eventSimilarity.setSimilarity(score);
            eventSimilarity.setTimestamp(timestamp);
            eventSimilarityRepository.save(eventSimilarity);
            log.debug("Updated similarity for ({},{}) to {}", eventA, eventB, score);
        } else {
            EventSimilarity newSimilarity = EventSimilarity.builder()
                    .eventA(eventA)
                    .eventB(eventB)
                    .similarity(score)
                    .timestamp(timestamp)
                    .build();
            eventSimilarityRepository.save(newSimilarity);
            log.debug("Created new similarity for ({},{}) = {}", eventA, eventB, score);
        }
    }
}
