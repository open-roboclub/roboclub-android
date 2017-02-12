package amu.roboclub.models;

import java.io.Serializable;

public class Doc implements Serializable {
    public Doc() {}

    public String name, url;

    @Override
    public String toString() {
        return "Doc{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}