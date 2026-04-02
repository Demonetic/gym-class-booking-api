package se.edugrade._5_java_enterprice_assignment_4_individual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.GymClass;

import java.util.List;

public interface GymClassRepository extends JpaRepository<GymClass, Long> {
    List<GymClass> findByInstructorIgnoreCase(String instructor);
    List<GymClass> findByDayOfWeek(String dayOfWeek);
}
