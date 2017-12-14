package com.sakagames.myseriesdatabases.database;

import android.support.annotation.NonNull;

import java.sql.Time;

/**
 * Created by robin on 10/08/17.
 */

public class Duration implements Comparable<Duration> {

    //ATTRIBUTS
    private int _id;
    private Time mDuration;
    private boolean mIsDefault;


    //CONSTRUCTEUR
    public Duration(int id, Time duration, boolean isDefault) {
        this._id = id;
        this.mDuration = duration;
        this.mIsDefault = isDefault;
    }

    public Duration(int id, String duration, boolean isDefault) {
        this(id,Time.valueOf(duration),isDefault);
    }


    //ACCESSEURS
    public int getID() {
        return _id;
    }

    public Time getDuration() {
        return mDuration;
    }

    public boolean isDefaut() {
        return mIsDefault;
    }

    @Override
    public String toString() {
        return mDuration.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Duration) {
            return this._id == ((Duration)o)._id || mDuration.equals(((Duration)o).mDuration);
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull Duration o) {
        return this.mDuration.compareTo(o.mDuration);
    }


    //MUTATEURS
    public void setDefault(boolean isDefault) {
        this.mIsDefault = isDefault;
    }
}
