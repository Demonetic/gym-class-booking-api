package se.edugrade._5_java_enterprice_assignment_4_individual.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GymIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }

    private void register(String username, String password) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated());
    }

    private Long createClass(String token, String name, String instructor, int maxParticipants) throws Exception {
        MvcResult result = mockMvc.perform(post("/classes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "name": "%s",
                                "instructor": "%s",
                                "description": "Created in integration test",
                                "dayOfWeek": "Monday",
                                "startTime": "09:00",
                                "durationMinutes": 60,
                                "maxParticipants": %d
                                }
                                """.formatted(name, instructor, maxParticipants)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @DisplayName("Register -> login -> create booking with real JWT -> verify round-trip")
    void registerLoginAndCreateBookingAndVerify() throws Exception {
        String username = "puss_in_boots";
        String password = "puss123";

        register(username, password);

        String adminToken = login("admin", "password");
        Long classId = createClass(adminToken, "Ogre Roar Rowing", "Shrek", 5);

        String userToken = login(username, password);

        mockMvc.perform(post("/classes/" + classId + "/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "participantName": "Puss in Boots",
                                "email": "puss@boots.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.participantName").value("Puss in Boots"))
                .andExpect(jsonPath("$.gymClassId").value(classId));

        mockMvc.perform(get("/classes/" + classId + "/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].participantName").value("Puss in Boots"))
                .andExpect(jsonPath("$[0].email").value("puss@boots.com"));
    }

    @Test
    @DisplayName("Login as admin -> create class with real JWT -> verify 201 and GET 200")
    void loginAdminCreateClassAndVerify() throws Exception {
        String adminToken = login("admin", "password");

        MvcResult result = mockMvc.perform(post("/classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": "Donkey Gallop Intervals",
                        "instructor": "Donkey",
                        "description": "Wild interval training with chaotic pacing and endless motivation.",
                        "dayOfWeek": "Thursday",
                        "startTime": "18:00",
                        "durationMinutes": 60,
                        "maxParticipants": 10
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Donkey Gallop Intervals"))
                .andReturn();

        Long classId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(get("/classes/" + classId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Donkey Gallop Intervals"))
                .andExpect(jsonPath("$.instructor").value("Donkey"));
    }

    @Test
    @DisplayName("POST /classes without token returns 401")
    void createClass_noToken_returns401() throws Exception {
        mockMvc.perform(post("/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "name": "Gingy Crumb Burner",
                        "instructor": "Gingerbread Man",
                        "description": "A tiny but furious calorie-burning session.",
                        "dayOfWeek": "Tuesday",
                        "startTime": "10:00",
                        "durationMinutes": 45,
                        "maxParticipants": 8
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /classes/{id}/bookings on full class returns 409")
    void createBooking_fullClass_returns409() throws Exception {
        String adminToken = login("admin", "password");

        register("user2", "password123");
        register("user3", "password123");

        String userToken1 = login("user2", "password123");
        String userToken2 = login("user3", "password123");

        Long classId = createClass(adminToken, "Shield Slam", "Fiona", 1);

        mockMvc.perform(post("/classes/" + classId + "/bookings")
                .header("Authorization", "Bearer " + userToken1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "participantName": "Dragon",
                        "email": "dragon@swamp.com"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/classes/" + classId + "/bookings")
                .header("Authorization", "Bearer " + userToken2)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "participantName": "Big Bad Wolf",
                        "email": "wolf@storybook.com"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("DELETE class -> GET returns 404")
    void deleteClassAndVerify() throws Exception {
        String adminToken = login("admin", "password");
        Long classId = createClass(adminToken, "Dragon Tail Endurance", "Dragon", 6);

        mockMvc.perform(delete("/classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/classes/" + classId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /classes/{id}/spots-remaining returns correct number")
    void getSpotsRemaining_returnsCorrectNumber() throws Exception {
        String adminToken = login("admin", "password");

        register("user4", "password123");
        String userToken = login("user4", "password123");

        Long classId = createClass(adminToken, "Pinocchio Plank Hold", "Pinocchio", 4);

        mockMvc.perform(post("/classes/" + classId + "/bookings")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "participantName": "Three Blind Mice",
                        "email": "mice@storybook.com"
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/classes/" + classId + "/spots-remaining"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spotsRemaining").value(3));
    }
}
