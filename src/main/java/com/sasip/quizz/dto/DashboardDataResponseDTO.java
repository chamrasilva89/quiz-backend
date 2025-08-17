package com.sasip.quizz.dto;

import java.util.List;

public class DashboardDataResponseDTO {

    private RegistrationsData registrations;
    private List<StudentData> students;
    private List<QuizData> quizzes;
    private QuestionsData questions;
    private CompletedQuizzesData completedQuizzes;

    // Getter and Setter for registrations
    public RegistrationsData getRegistrations() {
        return registrations;
    }

    public void setRegistrations(RegistrationsData registrations) {
        this.registrations = registrations;
    }

    // Getter and Setter for students
    public List<StudentData> getStudents() {
        return students;
    }

    public void setStudents(List<StudentData> students) {
        this.students = students;
    }

    // Getter and Setter for quizzes
    public List<QuizData> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizData> quizzes) {
        this.quizzes = quizzes;
    }

    // Getter and Setter for questions
    public QuestionsData getQuestions() {
        return questions;
    }

    public void setQuestions(QuestionsData questions) {
        this.questions = questions;
    }

    // Getter and Setter for completed quizzes
    public CompletedQuizzesData getCompletedQuizzes() {
        return completedQuizzes;
    }

    public void setCompletedQuizzes(CompletedQuizzesData completedQuizzes) {
        this.completedQuizzes = completedQuizzes;
    }

    // Inner class for Registrations Data
    public static class RegistrationsData {
        private List<String> colors;
        private List<String> categories;
        private List<SeriesData> series;

        // Getters and Setters
        public List<String> getColors() {
            return colors;
        }

        public void setColors(List<String> colors) {
            this.colors = colors;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public List<SeriesData> getSeries() {
            return series;
        }

        public void setSeries(List<SeriesData> series) {
            this.series = series;
        }

        // Inner class for Series Data
        public static class SeriesData {
            private String name;
            private List<Integer> data;

            // Getters and Setters
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<Integer> getData() {
                return data;
            }

            public void setData(List<Integer> data) {
                this.data = data;
            }
        }
    }

    // Inner class for Student Data
    public static class StudentData {
        private String alYear;
        private int all;
        private int active;

        // Getters and Setters
        public String getAlYear() {
            return alYear;
        }

        public void setAlYear(String alYear) {
            this.alYear = alYear;
        }

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }
    }

    // Inner class for Quiz Data
    public static class QuizData {
        private String alYear;
        private int all;

        // Getters and Setters
        public String getAlYear() {
            return alYear;
        }

        public void setAlYear(String alYear) {
            this.alYear = alYear;
        }

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }
    }

    // Inner class for Questions Data
    public static class QuestionsData {
        private int total;
        private int lastWeek;
        private int lastMonth;

        // Getters and Setters
        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getLastWeek() {
            return lastWeek;
        }

        public void setLastWeek(int lastWeek) {
            this.lastWeek = lastWeek;
        }

        public int getLastMonth() {
            return lastMonth;
        }

        public void setLastMonth(int lastMonth) {
            this.lastMonth = lastMonth;
        }
    }

    // Inner class for Completed Quizzes Data
    public static class CompletedQuizzesData {
        private List<String> colors;
        private List<String> categories;
        private List<SeriesData> series;

        // Getters and Setters
        public List<String> getColors() {
            return colors;
        }

        public void setColors(List<String> colors) {
            this.colors = colors;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public List<SeriesData> getSeries() {
            return series;
        }

        public void setSeries(List<SeriesData> series) {
            this.series = series;
        }

        // Inner class for Series Data
        public static class SeriesData {
            private String name;
            private List<Integer> data;

            // Getters and Setters
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<Integer> getData() {
                return data;
            }

            public void setData(List<Integer> data) {
                this.data = data;
            }
        }
    }
}
