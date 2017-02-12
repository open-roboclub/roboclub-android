package amu.roboclub.models;

import java.io.Serializable;
import java.util.List;

public class Project implements Serializable {
    public String id, name, description, team, image, youtube;
    public boolean opened, ongoing;

    public List<String> images;
    public List<Doc> docs;

    public Project() { }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", team='" + team + '\'' +
                ", image='" + image + '\'' +
                ", youtube='" + youtube + '\'' +
                ", ongoing=" + ongoing +
                ", images=" + images +
                ", docs=" + docs +
                '}';
    }
}

class Doc implements Serializable {
    public Doc() {}

    String name, url;

    @Override
    public String toString() {
        return "Doc{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
