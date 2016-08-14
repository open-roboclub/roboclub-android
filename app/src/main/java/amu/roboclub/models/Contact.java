package amu.roboclub.models;

public class Contact {
    public String name, designation, imgUrl;
    public String[] links;

    public Contact(String name, String designation) {
        this.name = name;
        this.designation = designation;
    }

    public Contact(String name, String designation, String imgUrl) {
        this.name = name;
        this.designation = designation;
        this.imgUrl = imgUrl;
    }

    public Contact(String name, String designation, String imgUrl, String[] links) {
        this.name = name;
        this.designation = designation;
        this.imgUrl = imgUrl;
        this.links = links;
    }
}
