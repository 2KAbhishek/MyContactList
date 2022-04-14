package com.iam2kabhishek.mycontactlist;


import android.text.format.Time;

public class Contact {

    private int contactId;
    private String contactName;
    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private String phoneNumber;
    private String cellNumber;
    private String eMail;
    private Time birthday;

    public Contact() {
        contactId = -1;
        Time t = new Time();
        t.setToNow();
        birthday = t;
    }

    public int getContactID() {
        return contactId;
    }

    public void setContactID(int i) {
        contactId = i;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String s) {
        contactName = s;
    }

    public Time getBirthday() {
        return birthday;
    }

    public void setBirthday(Time t) {
        birthday = t;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String s) {
        streetAddress = s;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String s) {
        city = s;
    }

    public String getState() {
        return state;
    }

    public void setState(String s) {
        state = s;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String s) {
        zipcode = s;
    }

    public void setPhoneNumber(String s) {
        phoneNumber = s;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setCellNumber(String s) {
        cellNumber = s;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setMail(String s) {
        eMail = s;
    }

    public String getMail() {
        return eMail;
    }
}
