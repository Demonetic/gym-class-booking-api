package se.edugrade._5_java_enterprice_assignment_4_individual.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.BookingRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.BookingResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/classes/{id}/bookings")
    public ResponseEntity<BookingResponse> createBooking(@PathVariable Long id,
                                                         @RequestBody @Valid BookingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(id, req));
    }

    @GetMapping("/classes/{id}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookings(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.findBookingsByGymClassId(id));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
