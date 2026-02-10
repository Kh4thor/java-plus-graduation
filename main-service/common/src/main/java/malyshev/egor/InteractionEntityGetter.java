package malyshev.egor;

import lombok.AllArgsConstructor;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.feign.user.UserAdminFeignClient;
import malyshev.egor.mapper.UserMapper;
import malyshev.egor.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InteractionEntityGetter {

    private final UserAdminFeignClient userAdminFeignClient;

    public User getUserById(Long userId) {
        List<Long> userIds = List.of(userId);
        final int from = 0;
        final int size = 10;
        List<UserDto> usersList = userAdminFeignClient.list(userIds, from, size);
        UserDto userDto = usersList.stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " was not found"));
        return UserMapper.toUser(userDto);
    }

}
