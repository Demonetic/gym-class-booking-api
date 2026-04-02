package se.edugrade._5_java_enterprice_assignment_4_individual.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.BookingRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.BookingResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.exception.BookingNotFoundException;
import se.edugrade._5_java_enterprice_assignment_4_individual.exception.CapacityExceededException;
import se.edugrade._5_java_enterprice_assignment_4_individual.exception.GymClassNotFoundException;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.Booking;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.GymClass;
import se.edugrade._5_java_enterprice_assignment_4_individual.repository.BookingRepository;
import se.edugrade._5_java_enterprice_assignment_4_individual.repository.GymClassRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final GymClassRepository gymClassRepository;

    public List<BookingResponse> findBookingsByGymClassId(Long classId) {
        GymClass gymClass = getGymClassByIdOrThrow(classId);
        return gymClass.getBookings().stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional
    public BookingResponse createBooking(Long classId, BookingRequest req) {
        GymClass gymClass = getGymClassByIdOrThrow(classId);

        long bookingCount = bookingRepository.countByGymClassId(classId);
        if (bookingCount >= gymClass.getMaxParticipants()) {
            throw new CapacityExceededException(gymClass.getName(), gymClass.getMaxParticipants());
        }

        Booking booking = new Booking(
                req.participantName(),
                req.email(),
                gymClass
        );

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Created booking id={} for gymClass id={}", savedBooking.getId(), classId);

        return BookingResponse.from(savedBooking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        getBookingByIdOrThrow(bookingId);
        bookingRepository.deleteById(bookingId);
        log.info("Deleted booking id={}", bookingId);
    }

    private void getBookingByIdOrThrow(Long bookingId) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    private GymClass getGymClassByIdOrThrow(Long classId) {
        return gymClassRepository.findById(classId)
                .orElseThrow(() -> new GymClassNotFoundException(classId));
    }
}
