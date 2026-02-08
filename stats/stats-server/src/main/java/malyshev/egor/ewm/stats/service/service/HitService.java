package malyshev.egor.ewm.stats.service.service;


import malyshev.egor.ewm.stats.dto.EndpointHitDto;
import malyshev.egor.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface HitService {

    EndpointHitDto create(EndpointHitDto hitDto);

    // НОВАЯ основная «типобезопасная» сигнатура
    List<ViewStatsDto> viewStats(LocalDateTime start,
                                 LocalDateTime end,
                                 List<String> uris,
                                 boolean unique);

    // Адаптер для совместимости со старыми тестами/вызовами
    DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    default List<ViewStatsDto> viewStats(String start,
                                         String end,
                                         List<String> uris,
                                         boolean unique) {
        return viewStats(LocalDateTime.parse(start, FMT),
                LocalDateTime.parse(end,   FMT),
                uris, unique);
    }
}