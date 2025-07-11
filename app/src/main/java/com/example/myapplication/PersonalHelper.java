package com.example.myapplication;

public class PersonalHelper {
    String firstname, surname;
    int age;
    float weight, height;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public PersonalHelper(String firstname, String surname, int age, float weight, float height) {
        this.firstname = firstname;
        this.surname = surname;
        this.age = age;
        this.weight = weight;
        this.height = height;
    }
}
