package malyshev.egor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_actions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Integer weight;   // максимальный вес действия (1,2,3)

    @Column(name = "last_interaction", nullable = false)
    private Instant lastInteraction;
}