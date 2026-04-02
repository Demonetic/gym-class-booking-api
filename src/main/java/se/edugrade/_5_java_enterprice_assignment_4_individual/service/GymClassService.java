package se.edugrade._5_java_enterprice_assignment_4_individual.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.GymClassRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.GymClassResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.SpotsRemainingResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.exception.GymClassNotFoundException;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.GymClass;
import se.edugrade._5_java_enterprice_assignment_4_individual.repository.BookingRepository;
import se.edugrade._5_java_enterprice_assignment_4_individual.repository.GymClassRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymClassService {
    private static final Logger log = LoggerFactory.getLogger(GymClassService.class);

    private final GymClassRepository gymClassRepository;
    private final BookingRepository bookingRepository;

    public Page<GymClassResponse> findAllGymClasses(Pageable pageable) {
        return gymClassRepository.findAll(pageable).map(GymClassResponse::from);
    }

    public GymClassResponse findGymClassById(Long id) {
        GymClass gymClass = getGymClassByIdOrThrow(id);
        return GymClassResponse.from(gymClass);
    }

    @Transactional
    public GymClassResponse createGymClass(GymClassRequest req) {
        GymClass gymClass = new GymClass(
                req.name(),
                req.instructor(),
                req.description(),
                req.dayOfWeek(),
                req.startTime(),
                req.durationMinutes(),
                req.maxParticipants()
        );

        GymClass saved = gymClassRepository.save(gymClass);
        log.info("Created GymClass: {} (id={})", saved.getName(), saved.getId());
        return GymClassResponse.from(saved);
    }

    @Transactional
    public GymClassResponse updateGymClass(Long id, GymClassRequest req) {
        GymClass gymClass = getGymClassByIdOrThrow(id);
        gymClass.setName(req.name());
        gymClass.setInstructor(req.instructor());
        gymClass.setDescription(req.description());
        gymClass.setDayOfWeek(req.dayOfWeek());
        gymClass.setStartTime(req.startTime());
        gymClass.setDurationMinutes(req.durationMinutes());
        gymClass.setMaxParticipants(req.maxParticipants());
        log.info("Updated GymClass: {} (id={})", gymClass.getName(), gymClass.getId());
        return GymClassResponse.from(gymClassRepository.save(gymClass));
    }

    @Transactional
    public void deleteGymClass(Long id) {
        getGymClassByIdOrThrow(id);
        gymClassRepository.deleteById(id);
        log.info("Deleted GymClass id={}", id);
    }

    public List<GymClassResponse> findByInstructor(String instructor) {
        return gymClassRepository.findByInstructorIgnoreCase(instructor).stream()
                .map(GymClassResponse::from)
                .toList();
    }

    public List<GymClassResponse> findByDayOfWeek(String dayOfWeek) {
        return gymClassRepository.findByDayOfWeek(dayOfWeek).stream()
                .map(GymClassResponse::from)
                .toList();
    }

    public SpotsRemainingResponse getSpotsRemaining(Long id) {
        GymClass gymClass = getGymClassByIdOrThrow(id);

        long bookedCount = bookingRepository.countByGymClassId(id);
        long spotsRemaining = gymClass.getMaxParticipants() - bookedCount;
        return new SpotsRemainingResponse(spotsRemaining);
    }

    public List<GymClassResponse> findAvailableClasses() {
        return gymClassRepository.findAll().stream()
                .filter(gymClass -> bookingRepository.countByGymClassId(gymClass.getId()) < gymClass.getMaxParticipants())
                .map(GymClassResponse::from)
                .toList();
    }

    private GymClass getGymClassByIdOrThrow(Long id) {
        return gymClassRepository.findById(id).orElseThrow(() -> new GymClassNotFoundException(id));
    }
}
