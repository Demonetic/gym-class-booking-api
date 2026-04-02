package se.edugrade._5_java_enterprice_assignment_4_individual.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Booking with id " + id + " not found");
    }
}
