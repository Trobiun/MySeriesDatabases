package com.sakagames.myseriesdatabases.database;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.sql.Time;

/**
 * Created by robin on 15/08/17.
 */

public class SeasonOrMovie implements Comparable<SeasonOrMovie> {

    //ATTRIBUTS
    protected long _id;
    protected long _idSerie;
    protected String mTitre;
    protected Integer mNumberSeason;
    protected StatusWatch mStatus;
    protected int mWatchedTimes;
    protected Bitmap mPicture;


    //CONSTRUCTEUR
    public SeasonOrMovie(long id, long idSerie, String title, Integer numberSeason, StatusWatch status, int watchedTimes, Bitmap picture) {
        this._id = id;
        this._idSerie = idSerie;
        this.mTitre = title;
        this.mNumberSeason = numberSeason;
        this.mStatus = status;
        this.mWatchedTimes = watchedTimes;
        this.mPicture = picture;
    }

    public SeasonOrMovie(long id, long idSerie, String title, int numberSeason, StatusWatch status, Bitmap picture) {
        this(id,idSerie,title,numberSeason,status,0,picture);
    }


    //ACCESSEURS
    public long getID() {
        return _id;
    }

    public long getIDSerie() {
        return _idSerie;
    }

    public String getTitre() {
        return mTitre;
    }

    public int getNumberSeason() {
        return mNumberSeason;
    }

    public StatusWatch getStatus() {
        return mStatus;
    }

    public boolean isWatching() {
        return mStatus.equals(StatusWatch.WATCHING);
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    @Override
    public int compareTo(@NonNull SeasonOrMovie seasonOrMovie) {
        return this.mTitre.compareTo(seasonOrMovie.mTitre);
    }


    //MUTATEURS
    public void setPicture(Bitmap picture) {
        this.mPicture = picture;
    }

}
