package com.sakagames.myseriesdatabases.database;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by robin on 10/08/17.
 */

public class Serie implements Comparable<Serie> {

    //ATTRIBUTS
	private long _id;
    private String mTitre;
	private LongSparseArray<SeasonOrMovie> seasons;
    private Bitmap mPicture;

    private List<StatusWatch> mStatus;
	
    //CONSTRUCTEUR
    public Serie(long id, String titre) {
		this._id = id;
        this.mTitre = titre;
        seasons = new LongSparseArray<>();
        mStatus = new ArrayList<>();
    }


    //ACCESSEURS
	public long getID() {
		return _id;
	}
	
    public String getTitre() {
        return mTitre;
    }

    public boolean statusWatchContains(StatusWatch status) {
        return mStatus.contains(status);
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public LongSparseArray<SeasonOrMovie> getSeasons() {
        return seasons;
    }

    public List<SeasonOrMovie> getSeasonsList() {
        List<SeasonOrMovie> saisonsList = SeriesDataSource.convertToList(seasons);
        Collections.sort(saisonsList);
        return saisonsList;
    }

    public String toString() {
        return mTitre;
    }


    //MUTATEURS
    public void setPicture(Bitmap picture) {
        this.mPicture = picture;
    }

    private void increaseStatus(StatusWatch status) {
        mStatus.add(status);
    }

    private void decreaseStatus(StatusWatch status) {
        mStatus.remove(status);
    }

    public void addSeasonOrMovie(SeasonOrMovie seasonOrMovie) {
        increaseStatus(seasonOrMovie.getStatus());
        seasons.put(seasonOrMovie.getID(),seasonOrMovie);
    }

    public void finishSeason(long idSeason) {
        SeasonOrMovie season = seasons.get(idSeason);
        if (season instanceof Season) {
            decreaseStatus(season.getStatus());
            ((Season)season).finish();
            increaseStatus(season.getStatus());
        }
    }

    public void deleteSeasonOrMovie(long idSeasonOrMovie) {
        SeasonOrMovie seasonOrMovie = seasons.get(idSeasonOrMovie);
        decreaseStatus(seasonOrMovie.getStatus());
        seasons.remove(idSeasonOrMovie);
    }

    @Override
    public int compareTo(@NonNull Serie serie) {
        return this.mTitre.compareTo(serie.mTitre);
    }
}
