package com.example.nathan.myday;

public class RegisterDetails {
    private String uid;
    private String email;
    private String firstname;
    private String lastname;
    private String gender;
    private String dob;
    private String nationality;

    public RegisterDetails() {

    }

    public RegisterDetails(String uid, String email, String firstname, String lastname, String gender, String dob, String nationality) {
        this.uid = uid;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.dob = dob;
        this.nationality = nationality;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getNationality() {
        return nationality;
    }


}
