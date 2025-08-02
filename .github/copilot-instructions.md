# Copilot Instructions for quiz-backend

## Project Overview
- Spring Boot (Java 17) backend for a quiz/reward application.
- Uses Maven for builds, MySQL for persistence, and Lombok for boilerplate reduction.
- Major domains: User, Quiz, Reward, Notification, Admin, each with model, repository, service, and controller layers.
- Entry point: `src/main/java/com/sasip/quizz/QuizzApplication.java` (uses `@SpringBootApplication` and `@EnableScheduling`).

- Standard Spring layering: Controller → Service (interface) → ServiceImpl (implementation) → Repository → Model.
- Each domain/table is mapped to:
  - a model class (in `model/`)
  - a DTO (in `dto/`)
  - a repository interface (in `repository/`)
  - a service interface (in `service/`)
  - a service implementation class (in `service/impl/`)
- Always keep service interfaces and their implementations in separate files and packages (`service/` and `service/impl/`).
- DTOs are used for all controller input/output. Never expose JPA entities directly.
- JPA repositories in `repository/` for DB access. Custom queries use Spring Data JPA conventions or `@Query`.
- Security via JWT (`JwtUtil` in `security/`).
- Firebase Admin SDK for push notifications (`PushNotificationServiceImpl`).
- Scheduling enabled for background jobs (e.g., quiz notifications).
- Exception handling: Custom exceptions (e.g., `UserNotFoundException`) in `exception/`.
- DTOs are used for all controller input/output. Never expose JPA entities directly.
- JPA repositories in `repository/` for DB access. Custom queries use Spring Data JPA conventions or `@Query`.
- Security via JWT (`JwtUtil` in `security/`).
- Firebase Admin SDK for push notifications (`PushNotificationServiceImpl`).
- Scheduling enabled for background jobs (e.g., quiz notifications).
- Exception handling: Custom exceptions (e.g., `UserNotFoundException`) in `exception/`.

## Controller Response & Error Handling
- All controller endpoints should return responses in the following format:
  ```json
  {
    "items": [ ... ],
    "status": "success" | "error",
    "message": "...optional message..."
  }
  ```
- On success, wrap data in the `items` array. On error, set `status: error` and provide a meaningful `message`.
- Use `ApiResponse<T>` or similar DTO for all responses. Avoid returning raw objects or collections.
- Add basic error handling in controllers: catch domain-specific and generic exceptions, return appropriate HTTP status and error message in the response format above.
- Example pattern:
  ```java
  @GetMapping("/active")
  public ResponseEntity<ApiResponse<List<RewardDetail>>> getActiveRewards(@RequestParam Long userId) {
      try {
          List<RewardDetail> rewards = rewardService.getActiveRewardsForUser(userId);
          return ResponseEntity.ok(new ApiResponse<>(rewards, "success", null));
      } catch (UserNotFoundException e) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new ApiResponse<>(Collections.emptyList(), "error", e.getMessage()));
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ApiResponse<>(Collections.emptyList(), "error", "Internal server error"));
      }
  }
  ```

## Developer Workflows
- **Build:** `./mvnw clean install` (or `mvnw.cmd` on Windows) from project root.
- **Run:** `./mvnw spring-boot:run` or run `QuizzApplication` from your IDE.
- **Test:** Tests in `src/test/java/com/sasip/quizz/`. Use `./mvnw test`.
- **Config:** Main config in `src/main/resources/application.properties` (or `.proprties.sample`).
- **Swagger/OpenAPI:** API docs at `/swagger-ui.html` when running.

## Integration Points
- **MySQL:** Connection in `application.properties`.
- **Firebase:** Service account JSON in `src/main/resources/`.
- **JWT:** Secret and expiration in `application.properties`.
- **Swagger:** Enabled via `springdoc-openapi-starter-webmvc-ui`.

## Conventions & Gotchas
- Use fully qualified class names if import collisions (e.g., Firebase `Notification` vs. model `NotificationEntity`).
- Use constructor injection (Lombok's `@RequiredArgsConstructor`) except for optional dependencies (`@Autowired`).
- All time fields use `java.time.LocalDateTime`.
- Use `RewardDetail` constructors as defined; do not add overloads unless needed by multiple call sites.
- For scheduled jobs, use `@Scheduled` in a service and register with `@EnableScheduling` in the main app class.
- For push notifications, use the builder pattern from `com.google.firebase.messaging.Notification`.

---

If you are unsure about a pattern, check for similar code in the `service/impl/` and `controller/` packages. When in doubt, prefer explicitness and follow the structure of existing features.
