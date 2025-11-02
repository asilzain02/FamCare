package com.famcare.model;

import java.time.LocalDateTime;

public class Doctor {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private Integer experienceYears;
    private String phoneNumber;
    private String bio;
    private Boolean isAvailable;
    private LocalDateTime createdAt;

    // Constructors
    public Doctor() {
    }

    public Doctor(String username, String password, String email, String fullName, 
                  String specialization, String qualification) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.specialization = specialization;
        this.qualification = qualification;
        this.isAvailable = true;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", qualification='" + qualification + '\'' +
                '}';
    }
}