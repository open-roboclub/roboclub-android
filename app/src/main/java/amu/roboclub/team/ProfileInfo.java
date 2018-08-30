package amu.roboclub.team;

import java.util.HashMap;
import java.util.List;

public class ProfileInfo {
    public String about, batch, cv;
    public List<String> interests;
    public List<HashMap<String, String>> projects;

    @Override
    public String toString() {
        return "ProfileInfo{" +
                "about='" + about + '\'' +
                ", batch='" + batch + '\'' +
                ", cv='" + cv + '\'' +
                ", interests=" + interests +
                ", projects=" + projects +
                '}';
    }
}
