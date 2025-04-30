package com.hotelmgmt.models.user;

import java.time.LocalDate;

public class Guest extends User {
    private String passportNumber;
    private LocalDate dateOfBirth;
    private String nationality;
    private String preferences;

    public Guest(String username, String password, String firstName, String lastName,
                String email, String phoneNumber, String passportNumber,
                LocalDate dateOfBirth, String nationality) {
        super(username, password, firstName, lastName, email, phoneNumber, UserRole.GUEST);
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
    }

    // Getters and Setters
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
} 