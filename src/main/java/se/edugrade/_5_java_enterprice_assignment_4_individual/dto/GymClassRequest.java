package se.edugrade._5_java_enterprice_assignment_4_individual.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GymClassRequest(@NotBlank String name,
                              @NotBlank String instructor,
                              String description,
                              @NotBlank String dayOfWeek,
                              @NotBlank String startTime,
                              @Min(15) @Max(120) int durationMinutes,
                              @Min(1) @Max(50) int maxParticipants) {
}
