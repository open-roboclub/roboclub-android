package amu.roboclub.team;

import androidx.annotation.Keep;

import java.util.HashMap;

@Keep
public class Profile {
    public String name, position, thumbnail, uid;
    public HashMap<String, String> links;
    public ProfileInfo profile_info;
    public int rank;
    public boolean adminOverride = false;

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", links=" + links +
                ", profile_info=" + profile_info +
                ", rank=" + rank +
                '}';
    }
}
