package se.edugrade._5_java_enterprice_assignment_4_individual.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.BookingRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.BookingResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.GymClassRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.GymClassResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.exception.CapacityExceededException;
import se.edugrade._5_java_enterprice_assignment_4_individual.exception.GymClassNotFoundException;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.Booking;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.GymClass;
import se.edugrade._5_java_enterprice_assignment_4_individual.repository.BookingRepository;
import se.edugrade._5_java_enterprice_assignment_4_individual.repository.GymClassRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymClassServiceTest {
    private static final Long EXISTING_ID = 1L;
    private static final Long MISSING_ID = 999L;

    @Mock
    private GymClassRepository gymClassRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private GymClassService gymClassService;

    @InjectMocks
    private BookingService bookingService;

    private GymClass createGymClass() {
        GymClass gymClass = new GymClass(
                "Ogre Sunrise Stretch",
                "Shrek",
                "A slow but powerful mobility session for ogres who wake up cranky.",
                "Monday",
                "09:00",
                60,
                10
        );
        gymClass.setBookings(new ArrayList<>());
        return gymClass;
    }

    @Test
    @DisplayName("findByGymClassId existing id returns GymClassResponse")
    void findByGymClassId_existingId_shouldReturnResponse() {
        GymClass gymClass = createGymClass();
        gymClass.setId(EXISTING_ID);

        when(gymClassRepository.findById(EXISTING_ID)).thenReturn(Optional.of(gymClass));

        GymClassResponse result = gymClassService.findGymClassById(EXISTING_ID);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(EXISTING_ID);
        assertThat(result.name()).isEqualTo("Ogre Sunrise Stretch");
        assertThat(result.instructor()).isEqualTo("Shrek");
    }

    @Test
    @DisplayName("findGymClassById mising id throws GymClassNotFoundException")
    void findGymClassById_missingId_shouldThrow() {
        when(gymClassRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gymClassService.findGymClassById(MISSING_ID))
                .isInstanceOf(GymClassNotFoundException.class)
                .hasMessage("Gym class with id " + MISSING_ID + " not found");
    }

    @Test
    @DisplayName("createGymClass valid request saves and returns response")
    void createGymClass_validRequest_shouldSaveAndReturnResponse() {
        GymClassRequest req = new GymClassRequest(
                "Far Far Away Spin Storm",
                "Prince Charming",
                "A dramatic high-speed cycling class with royal attitude and burning legs.",
                "Wednesday",
                "17:30",
                45,
                14
        );

        GymClass savedGymClass = new GymClass(
                "Far Far Away Spin Storm",
                "Prince Charming",
                "A dramatic high-speed cycling class with royal attitude and burning legs.",
                "Wednesday",
                "17:30",
                45,
                14
        );

        savedGymClass.setId(EXISTING_ID);
        savedGymClass.setBookings(new ArrayList<>());

        when(gymClassRepository.save(any(GymClass.class))).thenReturn(savedGymClass);

        GymClassResponse result = gymClassService.createGymClass(req);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(EXISTING_ID);
        assertThat(result.name()).isEqualTo("Far Far Away Spin Storm");

        verify(gymClassRepository, times(1)).save(any(GymClass.class));
    }

    @Test
    @DisplayName("createBooking when capacity exists saves and returns response")
    void createBooking_capacityAvailable_shouldSaveAndReturnResponse() {
        GymClass gymClass = createGymClass();
        gymClass.setId(EXISTING_ID);
        gymClass.setName("Fire Endurance");
        gymClass.setMaxParticipants(5);

        BookingRequest req = new BookingRequest("Donkey", "donkey@swamp.com");

        when(gymClassRepository.findById(EXISTING_ID)).thenReturn(Optional.of(gymClass));
        when(bookingRepository.countByGymClassId(EXISTING_ID)).thenReturn(2L);
        when(bookingRepository.save(any())).thenAnswer(invocation -> {
            var booking = invocation.getArgument(0, Booking.class);
            booking.setId(10L);
            return booking;
        });

        BookingResponse result = bookingService.createBooking(EXISTING_ID, req);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.participantName()).isEqualTo("Donkey");
        assertThat(result.gymClassId()).isEqualTo(EXISTING_ID);

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createBooking when class is full throws CapacityExceededException")
    void createBooking_fullClass_shouldThrow() {
        GymClass gymClass = createGymClass();
        gymClass.setId(EXISTING_ID);
        gymClass.setMaxParticipants(3);

        BookingRequest req = new BookingRequest("Puss", "puss@boots.com");

        when(gymClassRepository.findById(EXISTING_ID)).thenReturn(Optional.of(gymClass));
        when(bookingRepository.countByGymClassId(EXISTING_ID)).thenReturn(3L);

        assertThatThrownBy(() -> bookingService.createBooking(EXISTING_ID, req))
                .isInstanceOf(CapacityExceededException.class)
                .hasMessage("Gym class 'Ogre Sunrise Stretch' is full (max 3 participants)");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteGymClass existing id calls delete once")
    void deleteGymClass_existingId_shouldCallDeleteOnce() {
        GymClass gymClass = createGymClass();
        gymClass.setId(EXISTING_ID);

        when(gymClassRepository.findById(EXISTING_ID)).thenReturn(Optional.of(gymClass));

        gymClassService.deleteGymClass(EXISTING_ID);

        verify(gymClassRepository, times(1)).deleteById(EXISTING_ID);
    }
}
