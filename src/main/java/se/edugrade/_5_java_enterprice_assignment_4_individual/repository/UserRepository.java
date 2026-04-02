package se.edugrade._5_java_enterprice_assignment_4_individual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
