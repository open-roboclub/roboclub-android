package amu.roboclub.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class UpdateMapBuilder {
    private Map<String, Object> map = new HashMap<>();

    public UpdateMapBuilder addNonNullString(String key, String value) {
        if(key == null)
            return this;

        if(value != null && !TextUtils.isEmpty(value)) {
            map.put(key, value);
        } else {
            map.put(key, null);
        }

        return this;
    }

    public UpdateMapBuilder addNonNullNonEqualString(String key, String value, String comparator) {
        if(key == null)
            return this;

        if(comparator != null && (value == null || TextUtils.isEmpty(value))) {
            map.put(key, null);
        } else if(value != null && !value.equals(comparator) && !TextUtils.isEmpty(value)) {
            map.put(key, value);
        }

        return this;
    }

    public UpdateMapBuilder addNonEqualString(String key, String value, String comparator) {
        if(key == null)
            return this;

        if(value != null && !TextUtils.isEmpty(value) && !value.equals(comparator)) {
            map.put(key, value);
        }

        return this;
    }

    public Map<String, Object> build() {
        return map;
    }

}