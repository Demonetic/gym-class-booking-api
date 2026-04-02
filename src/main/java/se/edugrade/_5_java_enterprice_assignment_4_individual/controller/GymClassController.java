package se.edugrade._5_java_enterprice_assignment_4_individual.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.GymClassRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.GymClassResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.SpotsRemainingResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.service.GymClassService;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class GymClassController {
    private final GymClassService gymClassService;

    @GetMapping
    public ResponseEntity<Page<GymClassResponse>> getAllClasses(Pageable pageable) {
        return ResponseEntity.ok(gymClassService.findAllGymClasses(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymClassResponse> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(gymClassService.findGymClassById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GymClassResponse>> searchByInstructor(@RequestParam String instructor) {
        return ResponseEntity.ok(gymClassService.findByInstructor(instructor));
    }

    @PostMapping
    public ResponseEntity<GymClassResponse> createClass(@RequestBody @Valid GymClassRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gymClassService.createGymClass(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymClassResponse> updateClass(@PathVariable Long id,
                                                        @RequestBody @Valid GymClassRequest req) {
        return ResponseEntity.ok(gymClassService.updateGymClass(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        gymClassService.deleteGymClass(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/spots-remaining")
    public ResponseEntity<SpotsRemainingResponse> getSpotsRemaining(@PathVariable Long id) {
        return ResponseEntity.ok(gymClassService.getSpotsRemaining(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<GymClassResponse>> getAvailableClasses() {
        return ResponseEntity.ok(gymClassService.findAvailableClasses());
    }
}
