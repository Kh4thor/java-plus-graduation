package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.user.UserShortDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.model.user.User;

@UtilityClass
public final class UserMapper {
    public static UserDto toDto(User u) {
        if (u == null) {
            return null;
        }
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static UserShortDto toUserShort(User u) {
        if (u == null) {
            return null;
        }
        return UserShortDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }

    public static User toUser(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static User toUser(UserShortDto dto, String email) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(email)
                .build();
    }
}
