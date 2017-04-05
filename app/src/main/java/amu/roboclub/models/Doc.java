package amu.roboclub.models;

import java.io.Serializable;

public class Doc implements Serializable {

    public String name, url;

    public Doc() {}

    @Override
    public String toString() {
        return "Doc{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}