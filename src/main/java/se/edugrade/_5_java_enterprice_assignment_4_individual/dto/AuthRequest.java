package se.edugrade._5_java_enterprice_assignment_4_individual.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank String username,
                          @NotBlank String password) {
}
