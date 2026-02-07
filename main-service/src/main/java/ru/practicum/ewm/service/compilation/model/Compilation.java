package ru.practicum.ewm.service.compilation.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Подборка событий.
 */
@Entity
@Table(name = "compilations", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "compilation_events", joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    private Set<Long> events = new HashSet<>();

    @Column(nullable = false)
    private boolean pinned;

    @Column(nullable = false, length = 50, unique = true)
    private String title;
}