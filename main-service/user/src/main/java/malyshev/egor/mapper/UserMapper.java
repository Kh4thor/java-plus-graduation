package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.ewm.service.user.dto.UserDto;
import malyshev.egor.ewm.service.user.model.User;

@UtilityClass
public final class UserMapper {
    public static UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }
}
