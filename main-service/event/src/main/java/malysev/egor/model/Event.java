package malysev.egor.model;

import jakarta.persistence.*;
import lombok.*;
import malyshev.egor.ewm.service.category.model.Category;
import malyshev.egor.ewm.service.user.model.User;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(nullable = false, length = 7000)
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "lat", column = @Column(name = "location_lat", nullable = false)),
        @AttributeOverride(name = "lon", column = @Column(name = "location_lon", nullable = false))
    })
    private Location location;

    @Column(nullable = false)
    private boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private int participantLimit;

    @Builder.Default
    @Column(name = "request_moderation", nullable = false)
    private boolean requestModeration = true;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Enumerated(STRING) @Column(nullable = false)
    private EventState state;

    @Column(nullable = false, length = 120)
    private String title;
}
