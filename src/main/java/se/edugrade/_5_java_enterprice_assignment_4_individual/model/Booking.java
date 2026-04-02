package se.edugrade._5_java_enterprice_assignment_4_individual.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String participantName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    @ManyToOne
    @JoinColumn(name = "gym_class_id", nullable = false)
    private GymClass gymClass;

    public Booking(String participantName, String email, GymClass gymClass) {
        this.participantName = participantName;
        this.email = email;
        this.gymClass = gymClass;
    }

    @PrePersist
    public void prePersist() {
        this.bookedAt = LocalDateTime.now();
    }
}
