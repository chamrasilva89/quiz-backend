package com.sasip.quizz.dto;

public class LeaderboardFilterRequest {
    private Integer alYear;
    private String district;
    private String school;
    private String month;

    // Getters and Setters
    public Integer getAlYear() {
        return alYear;
    }

    public void setAlYear(Integer alYear) {
        this.alYear = alYear;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
