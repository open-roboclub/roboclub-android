package amu.roboclub.models;

public class Project {
    public String name, description, team, image;
    public boolean opened, ongoing;

    public Project(){}

    public Project(String name) {
        this.name = name;
    }

    public String getImage() {
        return "http://amuroboclub.in/" + image;
    }
}
