package se.edugrade._5_java_enterprice_assignment_4_individual.dto;

import se.edugrade._5_java_enterprice_assignment_4_individual.model.GymClass;

import java.util.List;

public record GymClassResponse(Long id,
                               String name,
                               String instructor,
                               String description,
                               String dayOfWeek,
                               String startTime,
                               int durationMinutes,
                               int maxParticipants,
                               List<BookingResponse> bookings) {

    public static GymClassResponse from(GymClass gymClass) {
        return new GymClassResponse(
                gymClass.getId(),
                gymClass.getName(),
                gymClass.getInstructor(),
                gymClass.getDescription(),
                gymClass.getDayOfWeek(),
                gymClass.getStartTime(),
                gymClass.getDurationMinutes(),
                gymClass.getMaxParticipants(),
                gymClass.getBookings()
                        .stream()
                        .map(BookingResponse::from)
                        .toList()
        );
    }
}
