package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.event.LocationDto;
import malyshev.egor.model.event.Location;

public class LocationMapper {

    public static LocationDto toDto(Location location) {
        if (location == null) {
            return null;
        }
        return LocationDto.builder()
                .lon(location.getLon())
                .lat(location.getLat())
                .build();
    }

    public static Location toLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }
}


