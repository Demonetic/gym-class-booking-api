package se.edugrade._5_java_enterprice_assignment_4_individual.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BookingRequest(@NotBlank String participantName,
                             @NotBlank @Email String email) {
}
