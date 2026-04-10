package malyshev.egor.repository;

import java.util.Set;

public interface InMemoryUserEventsRepository {
    void addEvent(long userId, long eventId);

    Set<Long> getEventsByUser(long userId);
}
