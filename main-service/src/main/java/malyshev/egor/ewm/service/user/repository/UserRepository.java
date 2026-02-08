package malyshev.egor.ewm.service.user.repository;

import malyshev.egor.ewm.service.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
