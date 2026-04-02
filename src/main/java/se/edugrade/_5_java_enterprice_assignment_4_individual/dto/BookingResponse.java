package se.edugrade._5_java_enterprice_assignment_4_individual.dto;

import se.edugrade._5_java_enterprice_assignment_4_individual.model.Booking;

import java.time.LocalDateTime;

public record BookingResponse(Long id,
                              String participantName,
                              String email,
                              LocalDateTime bookedAt,
                              Long gymClassId) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getParticipantName(),
                booking.getEmail(),
                booking.getBookedAt(),
                booking.getGymClass().getId()
        );
    }
}
