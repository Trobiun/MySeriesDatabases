package com.sakagames.myseriesdatabases.database;

import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;

import java.sql.Time;

/**
 * Created by robin on 10/08/17.
 */

public class Season extends SeasonOrMovie {

    //ATTRIBUTS
    private int mNumberEpisodes;
    private int mNumberEpisodesTotal;
    private boolean mAiring;
    private Time mDurationEpisodes;


    //CONSTRUCTEUR
    public Season(long id, long idSerie, String title, int numberSeason, StatusWatch status, boolean isAiring, int numberEpisodes, int numberEpisodesTotal, Time durationEpisodes, Bitmap picture) {
        super(id,idSerie,title,numberSeason,status,picture);
        this.mAiring = isAiring;
        this.mNumberEpisodes = numberEpisodes;
        this.mNumberEpisodesTotal = numberEpisodesTotal;
        this.mDurationEpisodes = durationEpisodes;
    }


    //ACCESSEURS
	public long getID() {
		return super.getID();
	}

	public long getIDSerie() {
        return super.getIDSerie();
    }

    public String getTitre() {
        return super.getTitre();
    }

    public StatusWatch getStatus() {
        return mStatus;
    }

    public boolean isAiring() {
        return mAiring;
    }

    public int getNumberEpisodes() {
        return mNumberEpisodes;
    }

    public int getNumberEpisodesTotal() {
        return mNumberEpisodesTotal;
    }

    public Time getDurationEpisodes() {
        return mDurationEpisodes;
    }


    //MUTATEURS
    public void addEpisode() {
        if (mAiring && mNumberEpisodes == mNumberEpisodesTotal) {
            mNumberEpisodes++;
            mNumberEpisodesTotal++;
        }
        else {
            mNumberEpisodes++;
        }
    }
    
    public void finish() {
        mStatus = StatusWatch.WATCHED;
        mAiring = false;
    }

}
