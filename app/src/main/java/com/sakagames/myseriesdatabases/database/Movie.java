package com.sakagames.myseriesdatabases.database;

import android.graphics.Bitmap;

import java.sql.Time;

/**
 * Created by robin on 10/08/17.
 */

public class Movie extends SeasonOrMovie {

    //ATTRIBUTS
    private Time mDurationMovie;


    //CONSTRUCTEUR
    public Movie(long id, long idSerie, String titreFilm, StatusWatch status, Time durationMovie, int watchedTimes, Bitmap picture) {
        super(id,idSerie,titreFilm,null,status,watchedTimes,picture);
        this.mDurationMovie = durationMovie;
    }

    public Movie(long id, long idSerie, String titreFilm, StatusWatch status, Time durationMovie, Bitmap picture) {
        this(id,idSerie,titreFilm,status,durationMovie,0,picture);
    }


    //ACCESSEURS
    public String getTitreFilm() {
        return super.getTitre();
    }

    public Time getDurationMovie() {
        return mDurationMovie;
    }
}
