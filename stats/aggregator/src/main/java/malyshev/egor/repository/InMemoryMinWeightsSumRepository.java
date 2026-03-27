package malyshev.egor.repository;


/*
 * Матрица сумм минимальных весов для каждой упорядоченной пары событий.
 */
public interface InMemoryMinWeightsSumRepository {

    void addToSum(long eventA, long eventB, double delta);

    double getSum(long eventA, long eventB);
}
