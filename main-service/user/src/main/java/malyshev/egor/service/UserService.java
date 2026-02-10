package malyshev.egor.service;

import malyshev.egor.dto.user.NewUserRequest;
import malyshev.egor.dto.user.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDto add(NewUserRequest req);

    List<UserDto> listByIds(java.util.List<Long> ids, Pageable pageable);

    void delete(long userId);

}
