package amu.roboclub.project;

import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public class Doc implements Serializable {

    public String name;
    public String url;

    @Override
    public String toString() {
        return "Doc{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}