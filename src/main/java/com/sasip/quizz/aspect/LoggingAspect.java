package com.sasip.quizz.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class LoggingAspect {

    private final LogService logService;
    private final ObjectMapper objectMapper;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String source = joinPoint.getTarget().getClass().getSimpleName();
        String action = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

        String params = summarizeParams(joinPoint.getArgs());
        String newValue = summarizeResponse(result);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = (auth != null && auth.getName() != null) ? auth.getName() : "system";
        String previousValue = null;
        String entity = inferEntityNameFromUri(uri);

        logService.log(
            "INFO",
            source,
            action,
            method + " " + uri + " called with params: " + params,
            performedBy,
            previousValue,
            newValue,
            entity,
            "API"
        );
    }

    private String summarizeParams(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .map(arg -> {
                    try {
                        return objectMapper.writeValueAsString(arg);
                    } catch (Exception e) {
                        return arg.toString();
                    }
                })
                .collect(Collectors.joining(", "));
    }

private String summarizeResponse(Object result) {
    if (result == null) return "No response";
    try {
        if (result instanceof ApiResponse<?> apiResponse) {
            if (apiResponse.getError() != null) {
                return "Error: " + apiResponse.getError() + ", Status: " + apiResponse.getStatus();
            } else {
                String dataString = objectMapper.writeValueAsString(apiResponse.getData());
                return "Status: " + apiResponse.getStatus() + ", Data: " + dataString;
            }
        }
        return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
        return result.toString();
    }
}


    private String inferEntityNameFromUri(String uri) {
        if (uri.contains("rewards/winners")) return "RewardWinner";
        if (uri.contains("user")) return "User";
        if (uri.contains("quiz")) return "Quiz";
        if (uri.contains("question")) return "Question";
        return "Unknown";
    }
}
