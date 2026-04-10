package malyshev.egor.repository.impl;

import malyshev.egor.repository.InMemoryUserEventsRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Хранит факт взаимодействия пользователя с событием (без весов).
 * Позволяет быстро получить список событий, с которыми пользователь уже работал, без необходимости просматривать
 * все записи в весовом репозитории.
 *
 * Ключ: Long — идентификатор пользователя (userId).
 * Значение: Set<Long> — id событий, с которыми взаимодействовал пользователь (без веса).
 */
@Repository
public class InMemoryUserEventsRepositoryImpl implements InMemoryUserEventsRepository {
    private final Map<Long, Set<Long>> userEvents = new ConcurrentHashMap<>();

    @Override
    public void addEvent(long userId, long eventId) {
        userEvents.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(eventId);
    }

    @Override
    public Set<Long> getEventsByUser(long userId) {
        return userEvents.getOrDefault(userId, Set.of());
    }
}
