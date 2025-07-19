package com.sasip.quizz.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasip.quizz.model.User; // Assuming you have this for updates
import com.sasip.quizz.repository.UserRepository;
import com.sasip.quizz.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j // Using Slf4j for internal logging
public class LoggingAspect {

    private final LogService logService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    // Use ThreadLocal to store the state between Before and After advice
    private final ThreadLocal<String> previousState = new ThreadLocal<>();

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) && " +
            "(execution(public * *(..)) && (@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)))")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String method = request.getMethod();

            // Only capture previous state for PUT or PATCH methods
            if ("PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
                Object entityToLog = findEntityById(joinPoint);
                if (entityToLog != null) {
                    previousState.set(objectMapper.writeValueAsString(entityToLog));
                }
            }
        } catch (Exception e) {
            log.error("Error in @Before logging aspect while capturing previous state: {}", e.getMessage());
            previousState.remove(); // Clean up on error
        }
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String source = joinPoint.getTarget().getClass().getSimpleName();
            String action = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

            // Retrieve previous value from ThreadLocal, default to an empty string
            String previousValue = previousState.get() != null ? previousState.get() : "";

            // The new value is the request body for POST/PUT, or the response for others
            String newValue;
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
                 newValue = summarizeParams(joinPoint.getArgs());
            } else {
                 newValue = summarizeResponse(result);
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String performedBy = (auth != null && auth.getName() != null) ? auth.getName() : "system";

            String entity = inferEntityNameFromUri(uri);
            String actionDescription = inferActionDescription(action, method);

            logService.log(
                    "INFO",
                    source,
                    action,
                    actionDescription,
                    actionDescription, // Details can be the same as description
                    performedBy,
                    previousValue,
                    newValue,
                    entity,
                    method
            );
        } catch (Exception e) {
            // IMPORTANT: Catch all exceptions to prevent the main API call from failing
            log.error("Failed to execute logging aspect after method completion: {}", e.getMessage(), e);
        } finally {
            // IMPORTANT: Always clear the ThreadLocal to prevent memory leaks
            previousState.remove();
        }
    }

    private Object findEntityById(JoinPoint joinPoint) {
        // This is an example for a user update. You'd need to expand this logic.
        String uri = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI();
        if (uri.contains("/users/")) { // e.g., PUT /api/users/{id}
            Long userId = extractIdFromArgs(joinPoint.getArgs());
            if (userId != null) {
                return userRepository.findById(userId).orElse(null);
            }
        }
        // Add logic for other entities like Module here if needed
        return null;
    }
    
    private Long extractIdFromArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg instanceof Long)
                .map(arg -> (Long) arg)
                .findFirst()
                .orElse(null);
    }

    private String summarizeParams(Object[] args) {
        if (args == null || args.length == 0) return "No parameters";
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .map(this::serializeObject)
                .collect(Collectors.joining(", "));
    }

    private String summarizeResponse(Object result) {
        if (result == null) return "No response";
        return serializeObject(result);
    }
    
    private String serializeObject(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize object for logging: {}", obj.getClass().getSimpleName());
            return "[ERROR: Could not serialize object]";
        }
    }

    private String inferEntityNameFromUri(String uri) {
        if (uri.contains("/user")) return "User";
        if (uri.contains("/quiz")) return "Quiz";
        if (uri.contains("/module")) return "Module"; // Added this
        if (uri.contains("/notification")) return "Notification";
        return "Unknown";
    }

    private String inferActionDescription(String action, String method) {
        switch (method.toUpperCase()) {
            case "POST": return action + " - Create";
            case "PUT":
            case "PATCH": return action + " - Update";
            case "DELETE": return action + " - Delete";
            default: return action + " - Unknown action";
        }
    }
}