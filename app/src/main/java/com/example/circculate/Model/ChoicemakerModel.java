package com.example.circculate.Model;

public class ChoicemakerModel {
    private String userName;
    private String userId;
    private String firstName, lastName, relationShip, phoneNumber, address1, address2, city, province, zipCode, location, emailAddress, isAppointed;
    public ChoicemakerModel(){}

    public ChoicemakerModel(String userName, String userId, String firstName, String lastName, String relationShip, String phoneNumber, String address1, String address2, String city, String province, String zipCode, String location, String emailAddress, String isAppointed) {
        this.userName = userName;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.relationShip = relationShip;
        this.phoneNumber = phoneNumber;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.location = location;
        this.emailAddress = emailAddress;
        this.isAppointed = isAppointed;
    }

    public ChoicemakerModel(String userName, String userId, String firstName, String lastName, String relationShip, String phoneNumber, String address1, String city, String province, String zipCode, String location, String emailAddress, String isAppointed) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.relationShip = relationShip;
        this.phoneNumber = phoneNumber;
        this.address1 = address1;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.location = location;
        this.emailAddress = emailAddress;
        this.isAppointed = isAppointed;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getIsAppointed() {
        return isAppointed;
    }

    public void setIsAppointed(String isAppointed) {
        this.isAppointed = isAppointed;
    }
}
