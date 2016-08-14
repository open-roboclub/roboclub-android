package amu.roboclub.models;

public class Project {
    public String title, about, team, imgUrl;
    public boolean opened;

    public Project(String title) {
        this.title = title;
    }

    public Project(String title, String team) {
        this.title = title;
        this.team = team;
    }

    public Project(String title, String team, String about) {
        this.title = title;
        this.team = team;
        this.about = about;
    }

    public Project(String title, String team, String about, String imgUrl) {
        this.title = title;
        this.team = team;
        this.about = about;
        this.imgUrl = imgUrl;
    }
}
