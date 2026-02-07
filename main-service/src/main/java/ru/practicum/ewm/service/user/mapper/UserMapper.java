package ru.practicum.ewm.service.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.model.User;

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
