package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.event.UserShortDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.model.user.User;

@UtilityClass
public final class UserMapper {
    public static UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static UserShortDto toUserShort(User u) {
        return UserShortDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }
}
