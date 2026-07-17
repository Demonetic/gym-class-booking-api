# Gym Class Booking API with JWT Security

A RESTful API for managing gym classes and bookings, built with Spring Boot and secured using JWT authentication with role-based access control.

---

## Features

- Full CRUD functionality for gym classes
- Booking system with capacity control
- Search gym classes by instructor
- Pagination and sorting support
- Input validation with Bean Validation
- Global exception handling with standardised error responses
- Spring Security with:
  - JWT authentication
  - Role-based authorisation (USER / ADMIN)
- In-memory H2 database
- Swagger UI for API testing
- Tests with `@WebMvcTest`, `@DataJpaTest`, Mockito, and `@SpringBootTest`

---

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT (JJWT 0.12.6)
- H2 Database
- Bean Validation (Hibernate Validator)
- SpringDoc OpenAPI (Swagger)
- JUnit 5 + Mockito

---

## Project Structure

```text
gym-class-booking-api/
├── pom.xml
├── generated-requests.http
├── README.md
└── src/
    ├── main/
    │   ├── java/.../_5_java_enterprice_assignment_4_individual/
    │   │   ├── controller/
    │   │   ├── service/
    │   │   ├── repository/
    │   │   ├── model/
    │   │   ├── dto/
    │   │   ├── exception/
    │   │   └── security/
    │   └── resources/
    │       ├── application.properties
    │       └── data.sql
    └── test/
        ├── controller/
        │   └── GymClassControllerTest.java
        ├── repository/
        │   └── GymRepositoryTest.java
        ├── service/
        │   └── GymClassServiceTest.java
        └── integration/
            └── GymIntegrationTest.java
```

---

## API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/auth/register` | Register new user | Public |
| POST | `/auth/login` | Login and receive JWT | Public |
| GET | `/classes` | Get all classes (paginated) | Public |
| GET | `/classes/{id}` | Get class by ID | Public |
| GET | `/classes/search?instructor=` | Search classes by instructor | Public |
| POST | `/classes` | Create class | ADMIN |
| PUT | `/classes/{id}` | Update class | ADMIN |
| DELETE | `/classes/{id}` | Delete class | ADMIN |
| POST | `/classes/{id}/bookings` | Create booking | USER / ADMIN |
| GET | `/classes/{id}/bookings` | Get bookings for class | Public |
| DELETE | `/bookings/{id}` | Delete booking | ADMIN |
| GET | `/classes/{id}/spots-remaining` | Get remaining spots | Public |
| GET | `/classes/available` | Get available classes | Public |

---

## Authentication

The API uses **JWT Authentication**.

### Users

| Username | Password | Role |
|----------|----------|------|
| admin | password | ADMIN |
| user | password | USER |

- `admin` is seeded in `data.sql`
- `user` is created via `POST /auth/register`

---

## Running the application

```bash
mvn spring-boot:run
```

### Swagger UI
http://localhost:8080/swagger-ui.html

### H2 Console
http://localhost:8080/h2-console

---

## Testing

- Controller tests are written using `@WebMvcTest`
- Repository tests use `@DataJpaTest`
- Service tests use Mockito with `@ExtendWith(MockitoExtension.class)`
- Integration tests use `@SpringBootTest` with real JWT tokens

Run tests:

```bash
mvn test
```

---

## Example Requests

See: [generated-requests.http](generated-requests.http)

---
