package se.edugrade._5_java_enterprice_assignment_4_individual.exception;

public class GymClassNotFoundException extends RuntimeException {
    public GymClassNotFoundException(Long id) {
        super("Gym class with id " + id + " not found");
    }
}
