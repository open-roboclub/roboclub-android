package amu.roboclub.team;

import androidx.annotation.Keep;

import java.util.HashMap;
import java.util.List;

@Keep
public class ProfileInfo {
    public String about;
    public String batch;
    public String cv;
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
