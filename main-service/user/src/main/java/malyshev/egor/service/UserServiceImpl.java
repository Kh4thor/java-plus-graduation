package malyshev.egor.service;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.user.NewUserRequest;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.UserMapper;
import malyshev.egor.model.user.User;
import malyshev.egor.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    @Override
    @Transactional
    public UserDto add(NewUserRequest req) {
        return UserMapper.toDto(
                repo.save(User.builder()
                        .name(req.getName())
                        .email(req.getEmail())
                        .build()
                )
        );
    }

    @Override
    public List<UserDto> listByIds(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            // Если сортировка не задана — подставляем по id ASC
            Pageable p = pageable.getSort().isSorted()
                    ? pageable
                    : PageRequest.of((int) (pageable.getOffset() / pageable.getPageSize()),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "id"));

            return repo.findAll(p).getContent().stream()
                    .map(UserMapper::toDto)
                    .toList();
        } else {
            // findAllById порядок НЕ гарантирует — сортируем и только потом пагинируем
            List<User> users = repo.findAllById(ids);
            users.sort(Comparator.comparing(User::getId));

            int from = (int) pageable.getOffset();
            int size = pageable.getPageSize();
            int start = Math.min(from, users.size());
            int end = Math.min(start + size, users.size());

            return users.subList(start, end).stream()
                    .map(UserMapper::toDto)
                    .toList();
        }
    }

    @Override
    @Transactional
    public void delete(long userId) {
        if (!repo.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        repo.deleteById(userId);
    }
}
