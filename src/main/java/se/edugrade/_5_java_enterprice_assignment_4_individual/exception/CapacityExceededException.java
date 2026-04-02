package se.edugrade._5_java_enterprice_assignment_4_individual.exception;

public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(String className, int maxParticipants) {
        super("Gym class '" + className + "' is full (max " + maxParticipants + " participants)");
    }
}
