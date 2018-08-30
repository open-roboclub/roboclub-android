package amu.roboclub.project;

import android.support.annotation.Keep;

import java.io.Serializable;
import java.util.List;

@Keep
public class Project implements Serializable {
    public String id;
    public String name;
    public String description;
    public String team;
    public String image;
    public String youtube;
    public boolean ongoing;

    public List<String> images;
    public List<Doc> docs;

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
