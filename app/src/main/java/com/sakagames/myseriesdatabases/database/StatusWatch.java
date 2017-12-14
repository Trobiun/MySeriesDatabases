package com.sakagames.myseriesdatabases.database;

import android.graphics.Color;

import java.security.SecureRandom;

/**
 * Created by robin on 19/08/17.
 */

public enum StatusWatch {

    WATCHING("watching",Color.parseColor("#8DEA43")),
    WATCHED("watched",Color.parseColor("#6F99E4")),
    STALLED("stalled",Color.parseColor("#FC9F3C")),
    DROPPED("dropped",Color.parseColor("#D93D48")),
    WANT_TO_WATCH("wantToWatch",Color.parseColor("#CCCC00"));

    private String value;
    private int color;

    StatusWatch(String value, int color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return this.value;
    }

    public int getColor() {
        return this.color;
    }

    public static StatusWatch getStatusWatch(String value) {
        for(StatusWatch v : values()) {
            if(v.getValue().equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

    private static final SecureRandom random = new SecureRandom();
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    @Override
    public String toString() {
        return this.value;
    }
}
