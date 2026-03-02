package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "similarities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event1", "event2"})
})
public class Similarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long event1;

    @Column(nullable = false)
    private Long event2;

    @Column(nullable = false)
    private Float similarity;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}
