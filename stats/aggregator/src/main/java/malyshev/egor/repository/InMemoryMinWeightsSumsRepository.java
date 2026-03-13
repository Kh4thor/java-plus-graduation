package malyshev.egor.repository;


import java.time.Instant;

/*
 * Матрица сумм минимальных весов для каждой упорядоченной пары событий.
 */
public interface InMemoryMinWeightsSumsRepository {

    void putPairSimilarity(long eventA, long eventB, double weight);

    double getPairSimilarity(long eventA, long eventB);
}
