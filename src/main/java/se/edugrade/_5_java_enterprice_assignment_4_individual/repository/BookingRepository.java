package se.edugrade._5_java_enterprice_assignment_4_individual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.gymClass.id = :classId")
    long countByGymClassId(@Param("classId") Long classId);
}
