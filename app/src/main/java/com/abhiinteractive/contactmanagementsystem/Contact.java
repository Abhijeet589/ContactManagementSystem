package com.abhiinteractive.contactmanagementsystem;

public class Contact {

    String name, email, phone, website;
    int sync;

    public Contact(String name, String email, String phone, String website, int sync) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.sync = sync;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public int getSync() {
        return sync;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }
}
