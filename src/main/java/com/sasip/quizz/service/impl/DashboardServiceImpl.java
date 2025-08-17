package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.repository.*;
import com.sasip.quizz.service.DashboardService;
import com.sasip.quizz.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDailyStreakRepository userDailyStreakRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserQuizSubmissionRepository userQuizSubmissionRepository;

    @Autowired
    private ALYearRepository alYearRepository;

    @Override
    public DashboardDataResponseDTO getDashboardData() {
        logger.debug("Fetching dashboard data...");

        DashboardDataResponseDTO dashboardData = new DashboardDataResponseDTO();

        // 1. Registrations Data
        logger.debug("Fetching registrations data...");
        dashboardData.setRegistrations(getRegistrationsData());

        // 2. Students Data
        logger.debug("Fetching students data...");
        dashboardData.setStudents(getStudentData());

        // 3. Quizzes Data
        logger.debug("Fetching quizzes data...");
        dashboardData.setQuizzes(getQuizData());

        // 4. Questions Data
        logger.debug("Fetching questions data...");
        dashboardData.setQuestions(getQuestionsData());

        // 5. Completed Quizzes Data
        logger.debug("Fetching completed quizzes data...");
        dashboardData.setCompletedQuizzes(getCompletedQuizzesData());

        return dashboardData;
    }

    // Method to get active AL Years
    private List<ALYear> getActiveALYears() {
        logger.debug("Fetching active AL Years...");
        List<ALYear> activeALYears = alYearRepository.findAll().stream()
                .filter(alYear -> alYear.getStatus() != null && alYear.getStatus().equalsIgnoreCase("ACTIVE"))
                .collect(Collectors.toList());

        // Log if no active ALYears are found
        if (activeALYears.isEmpty()) {
            logger.warn("No active AL years found.");
        }

        return activeALYears;
    }

    private DashboardDataResponseDTO.RegistrationsData getRegistrationsData() {
        logger.debug("Fetching registrations data...");

        DashboardDataResponseDTO.RegistrationsData registrationsData = new DashboardDataResponseDTO.RegistrationsData();

        // Set color scheme for the chart
        registrationsData.setColors(Arrays.asList("#465fff", "#ff5c8d", "#00d2b3"));
        registrationsData.setCategories(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));

        List<ALYear> activeALYears = getActiveALYears();

        // Check if active AL Years exist
        if (activeALYears == null || activeALYears.isEmpty()) {
            logger.warn("No active AL years available for registration data.");
        }

        List<User> allUsers = userRepository.findAll();
        Map<Integer, Map<Integer, Long>> registrationsByYearAndMonth = new HashMap<>();

        for (User user : allUsers) {
            if (activeALYears.stream().anyMatch(alYear -> alYear.getYear().equals(user.getAlYear()))) {
                int alYear = user.getAlYear();
                Integer month = (user.getCreatedDate() != null) ? user.getCreatedDate().getMonthValue() : null;

                // Log if month is null
                if (month == null) {
                    logger.warn("User with ID {} has null or missing creation date.", user.getUserId());
                }

                registrationsByYearAndMonth.putIfAbsent(alYear, new HashMap<>());
                registrationsByYearAndMonth.get(alYear).putIfAbsent(month, 0L);

                if (month != null) {
                    registrationsByYearAndMonth.get(alYear).put(month, registrationsByYearAndMonth.get(alYear).get(month) + 1);
                }
            }
        }

        List<DashboardDataResponseDTO.RegistrationsData.SeriesData> seriesDataList = new ArrayList<>();

        for (Map.Entry<Integer, Map<Integer, Long>> entry : registrationsByYearAndMonth.entrySet()) {
            Integer year = entry.getKey();
            Map<Integer, Long> monthData = entry.getValue();

            DashboardDataResponseDTO.RegistrationsData.SeriesData seriesData = new DashboardDataResponseDTO.RegistrationsData.SeriesData();
            seriesData.setName(year + " A/L");

            List<Integer> monthlyData = new ArrayList<>(Collections.nCopies(12, 0));

            for (Map.Entry<Integer, Long> monthEntry : monthData.entrySet()) {
                monthlyData.set(monthEntry.getKey() - 1, monthEntry.getValue().intValue());
            }

            seriesData.setData(monthlyData);
            seriesDataList.add(seriesData);
        }

        registrationsData.setSeries(seriesDataList);

        return registrationsData;
    }

    private List<DashboardDataResponseDTO.StudentData> getStudentData() {
        logger.debug("Fetching student data...");

        List<ALYear> activeALYears = getActiveALYears();

        List<User> allUsers = userRepository.findAll();
        Map<String, Long> totalUsers = allUsers.stream()
                .filter(user -> activeALYears.stream().anyMatch(alYear -> alYear.getYear().equals(user.getAlYear())))
                .collect(Collectors.groupingBy(user -> user.getAlYear().toString(), Collectors.counting()));

        Map<String, Long> activeUsers = allUsers.stream()
                .filter(user -> "active".equalsIgnoreCase(user.getUserStatus()) &&
                        activeALYears.stream().anyMatch(alYear -> alYear.getYear().equals(user.getAlYear())))
                .collect(Collectors.groupingBy(user -> user.getAlYear().toString(), Collectors.counting()));

        List<DashboardDataResponseDTO.StudentData> studentsDataList = new ArrayList<>();

        totalUsers.forEach((alYear, total) -> {
            DashboardDataResponseDTO.StudentData studentData = new DashboardDataResponseDTO.StudentData();
            studentData.setAlYear(alYear);
            studentData.setAll(total.intValue());
            studentData.setActive(activeUsers.getOrDefault(alYear, 0L).intValue());
            studentsDataList.add(studentData);
        });

        return studentsDataList;
    }

private List<DashboardDataResponseDTO.QuizData> getQuizData() {
    logger.debug("Fetching quiz data...");

    // Get the list of active AL years
    List<ALYear> activeALYears = getActiveALYears();
    
    // Fetch all quizzes
    List<Quiz> quizzes = quizRepository.findAll();

    Map<String, Integer> quizCountsByAlYear = new HashMap<>();

    // Iterate through the quizzes and filter out those with null 'al_year'
    quizzes.forEach(quiz -> {
        if (quiz.getAlYear() != null && activeALYears.stream().anyMatch(alYear -> alYear.getYear().equals(Integer.valueOf(quiz.getAlYear())))) {
            String alYear = quiz.getAlYear();
            quizCountsByAlYear.put(alYear, quizCountsByAlYear.getOrDefault(alYear, 0) + 1);
        } else {
            logger.warn("Quiz ID {} has null or invalid al_year. Skipping this quiz.", quiz.getQuizId());
        }
    });

    // Build the response
    List<DashboardDataResponseDTO.QuizData> quizDataList = new ArrayList<>();

    quizCountsByAlYear.forEach((alYear, count) -> {
        DashboardDataResponseDTO.QuizData quizData = new DashboardDataResponseDTO.QuizData();
        quizData.setAlYear(alYear);
        quizData.setAll(count);
        quizDataList.add(quizData);
    });

    return quizDataList;
}


    private DashboardDataResponseDTO.QuestionsData getQuestionsData() {
        logger.debug("Fetching questions data...");

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime lastWeekDate = currentDate.minusWeeks(1);
        LocalDateTime lastMonthDate = currentDate.minusMonths(1);

        long totalQuestions = questionRepository.count();

        long lastWeekQuestions = questionRepository.countByCreatedAtAfter(lastWeekDate);

        long lastMonthQuestions = questionRepository.countByCreatedAtAfter(lastMonthDate);

        DashboardDataResponseDTO.QuestionsData questionsData = new DashboardDataResponseDTO.QuestionsData();
        questionsData.setTotal((int) totalQuestions);
        questionsData.setLastWeek((int) lastWeekQuestions);
        questionsData.setLastMonth((int) lastMonthQuestions);

        return questionsData;
    }

    private DashboardDataResponseDTO.CompletedQuizzesData getCompletedQuizzesData() {
        logger.debug("Fetching completed quizzes data...");

        DashboardDataResponseDTO.CompletedQuizzesData completedQuizzesData = new DashboardDataResponseDTO.CompletedQuizzesData();

        completedQuizzesData.setColors(Arrays.asList("#465FFF", "#9CB9FF", "#9CE9FF"));
        completedQuizzesData.setCategories(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));

        List<DashboardDataResponseDTO.CompletedQuizzesData.SeriesData> seriesDataList = new ArrayList<>();

        Map<String, int[]> monthlyCounts = new HashMap<>();
        monthlyCounts.put(QuizType.SASIP.name(), new int[12]);
        monthlyCounts.put(QuizType.MYQUIZ.name(), new int[12]);
        monthlyCounts.put(QuizType.DYNAMIC.name(), new int[12]);

        List<UserQuizSubmission> allSubmissions = userQuizSubmissionRepository.findAll();

        for (UserQuizSubmission submission : allSubmissions) {
            if (submission.getStartTime() != null) {
                int month = submission.getStartTime().getMonthValue() - 1;

                Optional<Quiz> quiz = quizRepository.findById(Long.valueOf(submission.getQuizId()));

                quiz.ifPresent(q -> {
                    String alYear = q.getAlYear();

                    if (q.getQuizType() == QuizType.SASIP) {
                        monthlyCounts.get(QuizType.SASIP.name())[month]++;
                    } else if (q.getQuizType() == QuizType.MYQUIZ) {
                        monthlyCounts.get(QuizType.MYQUIZ.name())[month]++;
                    } else if (q.getQuizType() == QuizType.DYNAMIC) {
                        monthlyCounts.get(QuizType.DYNAMIC.name())[month]++;
                    }
                });
            }
        }

        for (Map.Entry<String, int[]> entry : monthlyCounts.entrySet()) {
            DashboardDataResponseDTO.CompletedQuizzesData.SeriesData seriesData = new DashboardDataResponseDTO.CompletedQuizzesData.SeriesData();
            seriesData.setName(entry.getKey());
            seriesData.setData(Arrays.asList(Arrays.stream(entry.getValue()).boxed().toArray(Integer[]::new)));
            seriesDataList.add(seriesData);
        }

        completedQuizzesData.setSeries(seriesDataList);

        return completedQuizzesData;
    }

        @Override
public List<QuizDetailsDTO> getActiveQuizzes() {
    // Fetch all active quizzes
    List<Quiz> quizzes = quizRepository.findAll()
            .stream()
            .filter(quiz -> quiz.getAlYear() != null)  // Ensure that quiz has an AL Year
            .sorted(Comparator.comparing(Quiz::getAlYear)) // Sort quizzes by AL Year
            .collect(Collectors.toList());

    List<QuizDetailsDTO> quizDetailsList = new ArrayList<>();

    for (Quiz quiz : quizzes) {
        Integer alYearInteger = null;
        String alYearString = quiz.getAlYear(); // Get the alYear as String

        try {
            alYearInteger = Integer.parseInt(alYearString);  // Parse the String to Integer for local use if needed
        } catch (NumberFormatException e) {
            System.err.println("Invalid alYear format for quiz ID: " + quiz.getQuizId());
            continue;  // Skip this quiz if alYear is not a valid integer
        }

        // Get active students for this quiz (pass alYear as String)
        long activeStudents = userRepository.countUsersByAlYearAndUserStatus(alYearString, "active");

        // Get completed students for this quiz (this assumes quizId is Long, no changes needed)
        long completedStudents = userQuizSubmissionRepository.countCompletedSubmissionsByQuizId(quiz.getQuizId());

        // Create DTO for this quiz
        QuizDetailsDTO quizDetails = new QuizDetailsDTO(
                quiz.getQuizId(),
                quiz.getQuizName(),
                alYearInteger, // Store AL Year as Integer for local processing
                activeStudents,
                completedStudents
        );
        quizDetailsList.add(quizDetails);
    }

    return quizDetailsList;
}

}
