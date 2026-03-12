package malyshev.egor.service;

import lombok.extern.slf4j.Slf4j;
import malyshev.egor.repository.InMemoryEventTotalWeightRepository;
import malyshev.egor.repository.InMemoryEventUserWeightsRepository;
import malyshev.egor.repository.InMemoryMinWeightsSumsRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SimilarityService {
    private final InMemoryMinWeightsSumsRepository inMemoryMinWeightsSumsRepository;
    private final InMemoryEventUserWeightsRepository inMemoryEventUserWeightsRepository;
    private final InMemoryEventTotalWeightRepository inMemoryEventTotalWeightRepository;


    public SimilarityService(InMemoryEventTotalWeightRepository inMemoryEventTotalWeightRepository, InMemoryMinWeightsSumsRepository inMemoryMinWeightsSumsRepository, InMemoryEventUserWeightsRepository inMemoryEventUserWeightsRepository) {
        this.inMemoryEventTotalWeightRepository = inMemoryEventTotalWeightRepository;
        this.inMemoryMinWeightsSumsRepository = inMemoryMinWeightsSumsRepository;
        this.inMemoryEventUserWeightsRepository = inMemoryEventUserWeightsRepository;
    }
}