package se.edugrade._5_java_enterprice_assignment_4_individual.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.Booking;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.GymClass;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class GymRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GymClassRepository gymClassRepository;

    @Autowired
    BookingRepository bookingRepository;

    private GymClass createGymClass(String name, String instructor, String description, String dayOfWeek,
                                    String startTime, int durationMinutes, int maxParticipants) {
        return new GymClass(
                name, instructor, description, dayOfWeek,
                startTime, durationMinutes, maxParticipants
        );
    }

    @Test
    @DisplayName("findByInstructor returns matching classes")
    void findByInstructor_existingInstructor_shouldReturnMatchingClasses() {
        GymClass yoga = createGymClass(
                "Mud Roll",
                "Shrek",
                "Loosen up every ogre joint.",
                "Monday",
                "09:00",
                60,
                10
        );

        GymClass cardio = createGymClass(
                "Waffle Sprint",
                "Donkey",
                "High-speed cardio powered by chaos and the imagination of waffles.",
                "Tuesday",
                "14:00",
                45,
                12
        );

        GymClass strength = createGymClass(
                "Log Press",
                "Shrek",
                "Serious upper-body training for ogre muscles.",
                "Friday",
                "18:00",
                90,
                8
        );

        entityManager.persistAndFlush(yoga);
        entityManager.persistAndFlush(cardio);
        entityManager.persistAndFlush(strength);

        List<GymClass> result = gymClassRepository.findByInstructorIgnoreCase("shrek");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(GymClass::getName)
                .containsExactlyInAnyOrder("Mud Roll", "Log Press");
    }

    @Test
    @DisplayName("findByDayOfWeek returns matching classes")
    void findByDayOfWeek_existingDay_shouldReturnMatchingClasses() {
        GymClass agility = createGymClass(
                "Knight Dodge Bootcamp",
                "Fiona",
                "Agility training for battle-ready princesses.",
                "Monday",
                "09:00",
                60,
                10
        );

        GymClass hiit = createGymClass(
                "Fairy HIIT Madness",
                "Fairy Godmother",
                "Explosive interval training fueled by questionable potions.",
                "Monday",
                "11:00",
                50,
                15
        );

        GymClass cardio = createGymClass(
                "Blind Agility Drills",
                "Three Blind Mice",
                "Unpredictable footwork drills that somehow work without vision.",
                "Tuesday",
                "14:00",
                45,
                12
        );

        entityManager.persistAndFlush(agility);
        entityManager.persistAndFlush(hiit);
        entityManager.persistAndFlush(cardio);

        List<GymClass> result = gymClassRepository.findByDayOfWeek("Monday");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(GymClass::getName)
                .containsExactlyInAnyOrder("Knight Dodge Bootcamp", "Fairy HIIT Madness");
    }

    @Test
    @DisplayName("countByGymClassId returns correct booking count")
    void countByGymClassId_existingBookings_shouldReturnCorrectCount() {
        GymClass gymClass = createGymClass(
                "Wing Endurance",
                "Dragon",
                "A fiery endurance session.",
                "Wednesday",
                "17:30",
                75,
                10
        );

        entityManager.persistAndFlush(gymClass);

        Booking booking1 = new Booking("Donkey", "donkey@swamp.com", gymClass);
        Booking booking2 = new Booking("Fiona", "fiona@swamp.com", gymClass);
        Booking booking3 = new Booking("Puss", "puss@boots.com", gymClass);

        entityManager.persistAndFlush(booking1);
        entityManager.persistAndFlush(booking2);
        entityManager.persistAndFlush(booking3);

        long result = bookingRepository.countByGymClassId(gymClass.getId());

        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("countByGymClassId returns zero when class has no bookings")
    void countByGymClassId_noBookings_shouldReturnZero() {
        GymClass gymClass = createGymClass(
                "Onion Breath Recovery",
                "Shrek",
                "Slow recovery work with deep smelly breaths.",
                "Sunday",
                "08:00",
                30,
                20
        );

        entityManager.persistAndFlush(gymClass);

        long result = bookingRepository.countByGymClassId(gymClass.getId());

        assertThat(result).isZero();
    }
}
