package com.sasip.quizz.dto;

// import lombok.Data; // <--- Remove this line if you're writing getters/setters manually

public class ForgotPasswordConfirmRequest {
    private String phone;
    private String newPassword;
    private String otp;

    // Constructors (optional, but good practice)
    public ForgotPasswordConfirmRequest() {
    }

    public ForgotPasswordConfirmRequest(String phone, String newPassword, String otp) {
        this.phone = phone;
        this.newPassword = newPassword;
        this.otp = otp;
    }

    // Getters
    public String getPhone() {
        return phone;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getOtp() {
        return otp;
    }

    // Setters
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}