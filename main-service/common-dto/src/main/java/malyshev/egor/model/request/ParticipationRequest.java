package malyshev.egor.model.request;

import jakarta.persistence.*;
import lombok.*;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}