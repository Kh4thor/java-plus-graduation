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
        @UniqueConstraint(columnNames = {"eventA", "eventB"})
})
public class Similarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventA;

    @Column(nullable = false)
    private Long eventB;

    @Column(nullable = false)
    private Float similarity;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}
