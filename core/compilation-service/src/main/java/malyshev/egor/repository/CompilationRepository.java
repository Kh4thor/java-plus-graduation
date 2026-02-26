package malyshev.egor.repository;

import malyshev.egor.model.Compilation;
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