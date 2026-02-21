package malyshev.egor.user.service;

import malyshev.egor.user.dto.NewUserRequest;
import malyshev.egor.user.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDto add(NewUserRequest req);

    List<UserDto> listByIds(List<Long> ids, Pageable pageable);

    void delete(long userId);

}
