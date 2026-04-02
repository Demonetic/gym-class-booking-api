package se.edugrade._5_java_enterprice_assignment_4_individual.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.BookingResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.GymClassResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.exception.GymClassNotFoundException;
import se.edugrade._5_java_enterprice_assignment_4_individual.security.JwtAuthenticationFilter;
import se.edugrade._5_java_enterprice_assignment_4_individual.security.JwtUtil;
import se.edugrade._5_java_enterprice_assignment_4_individual.security.SecurityConfig;
import se.edugrade._5_java_enterprice_assignment_4_individual.service.BookingService;
import se.edugrade._5_java_enterprice_assignment_4_individual.service.GymClassService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({GymClassController.class, BookingController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class GymClassControllerTest {
    private static final Long EXISTING_ID = 1L;
    private static final Long MISSING_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymClassService gymClassService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("GET /classes without auth returns 200 OK")
    void getAllClasses_noAuth_shouldReturn200() throws Exception {
        GymClassResponse response = new GymClassResponse(
                EXISTING_ID,
                "Ogre Powerlifting",
                "Shrek",
                "Lift logs in the Swamp.",
                "Tuesday",
                "20:00",
                90,
                12,
                List.of()
        );

        Page<GymClassResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(gymClassService.findAllGymClasses(any())).thenReturn(page);

        mockMvc.perform(get("/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Ogre Powerlifting"));
    }

    @Test
    @DisplayName("GET /classes/{id} without auth returns 200 OK")
    void getClassById_noAuth_shouldReturn200() throws Exception {
        GymClassResponse response = new GymClassResponse(
                EXISTING_ID,
                "Donkey Cardio",
                "Donkey",
                "Non-stop cardio with loud motivation.",
                "Wednesday",
                "14:00",
                70,
                16,
                List.of()
        );

        when(gymClassService.findGymClassById(EXISTING_ID)).thenReturn(response);

        mockMvc.perform(get("/classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Donkey Cardio"));
    }

    @Test
    @DisplayName("POST /classes as ADMIN returns 201 Created")
    void createClass_asAdmin_shouldReturn201() throws Exception {
        GymClassResponse response = new GymClassResponse(
                EXISTING_ID,
                "Fiona Beast Mode",
                "Fiona",
                "Train like a warrior princess.",
                "Thursday",
                "12:00",
                120,
                10,
                List.of()
        );

        when(gymClassService.createGymClass(any())).thenReturn(response);

        mockMvc.perform(post("/classes")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Fiona Beast Mode",
                            "instructor": "Fiona",
                            "description": "Train like a warrior princess.",
                            "dayOfWeek": "Thursday",
                            "startTime": "12:00",
                            "durationMinutes": 120,
                            "maxParticipants": 10
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fiona Beast Mode"));
    }

    @Test
    @DisplayName("POST /classes without auth returns 401 Unauthorized")
    void createClass_noAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": "Pinocchio Nose Stretch",
                        "instructor": "Pinocchio",
                        "description": "A suspiciously intense flexibility session.-",
                        "dayOfWeek": "Friday",
                        "startTime": "08:00",
                        "durationMinutes": 60,
                        "maxParticipants": 20
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /classes as USER returns 403 Forbidden")
    void createClass_asUser_shouldReturn403() throws Exception {
        mockMvc.perform(post("/classes")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": "Ginger Sprint",
                        "instructor": "Gingerbread man",
                        "description": "Run like someone is trying to eat your legs again.",
                        "dayOfWeek": "Saturday",
                        "startTime": "13:00",
                        "durationMinutes": 20,
                        "maxParticipants": 10
                        }
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /classes/{id}/bookings as USER returns 201 Created")
    void createBooking_asUser_shouldReturn201() throws Exception {
        BookingResponse response = new BookingResponse(
                EXISTING_ID,
                "Donkey",
                "donkey@swamp.com",
                LocalDateTime.now(),
                EXISTING_ID
        );

        when(bookingService.createBooking(anyLong(), any())).thenReturn(response);

        mockMvc.perform(post("/classes/1/bookings")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "participantName": "Donkey",
                        "email": "donkey@swamp.com"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.participantName").value("Donkey"))
                .andExpect(jsonPath("$.gymClassId").value(1));
    }

    @Test
    @DisplayName("POST /classes with invalid body returns 400 Bad Request")
    void createClass_invalidBody_shouldReturn400()  throws Exception {
        mockMvc.perform(post("/classes")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": "",
                        "instructor": "",
                        "description": "This swamp workout has absolutely no valid input.",
                        "dayOfWeek": "",
                        "startTime": "",
                        "durationMinutes": 5,
                        "maxParticipants": 0
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /classes/{id} for missing class returns 404 Not Found")
    void getClassById_notFound_shouldReturn404() throws Exception {
        when(gymClassService.findGymClassById(MISSING_ID))
                .thenThrow(new GymClassNotFoundException(MISSING_ID));

        mockMvc.perform(get("/classes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
