package malyshev.egor.ewm.service.category.repository;

import malyshev.egor.ewm.service.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
