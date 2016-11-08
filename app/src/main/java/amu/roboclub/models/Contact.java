package amu.roboclub.models;

import java.util.HashMap;

public class Contact {
    public String name, position, thumbnail;
    public HashMap<String, String> links;

    public Contact() {
    }

    public Contact(String name, String position) {
        this.name = name;
        this.position = position;
    }
}
