package malyshev.egor.service;

import malyshev.egor.ewm.service.user.dto.NewUserRequest;
import malyshev.egor.ewm.service.user.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDto add(NewUserRequest req);

    List<UserDto> listByIds(java.util.List<Long> ids, Pageable pageable);

    void delete(long userId);

}
