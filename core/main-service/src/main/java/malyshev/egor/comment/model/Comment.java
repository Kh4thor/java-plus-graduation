package malyshev.egor.comment.model;

import jakarta.persistence.*;
import lombok.*;
import malyshev.egor.event.model.Event;
import malyshev.egor.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentator;

    @Column(nullable = false)
    private LocalDateTime publishedOn;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private boolean deleted;
}
