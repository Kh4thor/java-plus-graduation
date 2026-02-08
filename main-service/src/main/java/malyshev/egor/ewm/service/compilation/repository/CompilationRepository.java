package malyshev.egor.ewm.service.compilation.repository;

import malyshev.egor.ewm.service.compilation.model.Compilation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Основной репозиторий подборок.
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    boolean existsByTitle(String title);

    Optional<Compilation> findByTitleIgnoreCase(String title);

    List<Compilation> findByPinned(boolean pinned, Pageable pageable);
}