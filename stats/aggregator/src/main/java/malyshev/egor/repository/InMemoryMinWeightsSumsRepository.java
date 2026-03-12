package malyshev.egor.repository;


/*
 * Матрица сумм минимальных весов для каждой упорядоченной пары событий.
 */
public interface InMemoryMinWeightsSumsRepository {

    void put(long eventA, long eventB, double sum);

    double get(long eventA, long eventB);
}
