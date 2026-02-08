package malyshev.egor.ewm.service.compilation.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class QuerydslUtils {

    /**
     * Преобразует Spring Data Sort в массив OrderSpecifier для QueryDSL.
     */
    public static <T> OrderSpecifier<?>[] toOrderSpecifierArray(Sort sort, PathBuilder<T> entityPath) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            orders.add(new OrderSpecifier<>(direction, entityPath.getComparable(order.getProperty(), Comparable.class)));
        });
        return orders.toArray(new OrderSpecifier<?>[0]);
    }
}
