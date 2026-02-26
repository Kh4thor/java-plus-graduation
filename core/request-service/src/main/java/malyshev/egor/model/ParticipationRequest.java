package malyshev.egor.model;

import jakarta.persistence.*;
import lombok.*;
import malyshev.egor.dto.request.RequestStatus;

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

    @Column
    private LocalDateTime created;

    @Column
    private Long event;

    @Column
    private Long requester;

    @Column
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}