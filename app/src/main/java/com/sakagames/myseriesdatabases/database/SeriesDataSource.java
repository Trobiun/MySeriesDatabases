package com.sakagames.myseriesdatabases.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.widget.Toast;

import com.sakagames.myseriesdatabases.BitmapUtility;
import com.sakagames.myseriesdatabases.MainActivity;
import com.sakagames.myseriesdatabases.MyExpandableListAdapter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by robin on 10/08/17.
 */

public class SeriesDataSource {

    //ATTRIBUTS STATIC
    private static SeriesDataSource dataSource;

    //ATTRIBUTS
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context context;

    private HashMap<StatusWatch,MyExpandableListAdapter> mExpandableListAdapters;


    private LongSparseArray<Serie> seriesArray;
    private List<Duration> durationsList;


    //CONSTRUCTEUR
    public static SeriesDataSource getInstance(Context context) {
        if (dataSource == null) {
            dataSource = new SeriesDataSource(context);
        }
        return dataSource;
    }

    public static SeriesDataSource newInstance(Context context) {
        dataSource = new SeriesDataSource(context);
        return dataSource;
    }

    private SeriesDataSource(Context context) {
        this.context = context;
        dbHelper = new MySQLiteHelper(context,MainActivity.databaseName);
        seriesArray = new LongSparseArray<>();
        durationsList = new ArrayList<>();
        mExpandableListAdapters = new HashMap<>();
    }


    //ACCESSEURS
    public List<Serie> getSeriesList() {
        List<Serie> series = convertToList(seriesArray);
        Collections.sort(series);
        return series;
    }

    public List<Serie> getSeriesList(@Nullable StatusWatch statusWatch) {
        if (statusWatch == null) {
            return getSeriesList();
        }
        List<Serie> series = convertToList(seriesArray);
        Iterator<Serie> it = series.iterator();
        while (it.hasNext()) {
            Serie serie = it.next();
            if (!serie.statusWatchContains(statusWatch)) {
                it.remove();
            }
        }
        Collections.sort(series);
        return series;
    }

    public List<Duration> getDureesList() {
        return durationsList;
    }

    public List<Time> getTimesList() {
        List<Time> times = new ArrayList<>();
        for (Duration duration : durationsList) {
            times.add(duration.getDuration());
        }
        return times;
    }

    public List<String> getDureesStringList() {
        List<String> times = new ArrayList<>();
        for (Duration duration : durationsList) {
            times.add(duration.toString());
        }
        return times;
    }


    //MUTATEURS
    public void putExpandableListAdapter(StatusWatch status, MyExpandableListAdapter expandableListAdapter) {
        mExpandableListAdapters.put(status,expandableListAdapter);
    }


    public void open() {
        database = dbHelper.getWritableDatabase();
    }
	
    public void close() {
        dbHelper.close();
    }

    public void vacuum() {
        database.execSQL("VACUUM");
    }

    public void actualiser() {
        seriesArray.clear();
        durationsList.clear();
        Cursor cursor = database.query(MySQLiteHelper.VIEW_ALL_SERIES,MySQLiteHelper.viewSeriesColumns,null,null,null,null,null);
        cursor.moveToFirst();
        Serie serie;
        while (!cursor.isAfterLast()) {
            if (serieIsNull(cursor)) {
                serie = cursorToSerie(cursor);
                seriesArray.put(serie.getID(),serie);
            }
            else {
                serie = seriesArray.get(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TABLE_SERIES_ID)));
            }
            if (!seasonIsNull(cursor)) {
                Season season = cursorToSeason(cursor);
                serie.addSeasonOrMovie(season);
            }
            if (!movieIsNull(cursor)) {
                Movie movie = cursorToMovie(cursor);
                serie.addSeasonOrMovie(movie);
            }
            cursor.moveToNext();
        }
        cursor.close();

        Cursor cursorDuree = database.query(MySQLiteHelper.TABLE_DURATIONS,MySQLiteHelper.tableDurationsColumns,null,null,null,null,MySQLiteHelper.TABLE_DURATIONS_DURATION);
        cursorDuree.moveToFirst();
        while (!cursorDuree.isAfterLast()) {
            Duration duration = cursorToDuration(cursorDuree);
            durationsList.add(duration);
            cursorDuree.moveToNext();
        }
        cursorDuree.close();
        notifyAllAdapters();
    }

    private boolean seasonIsNull(Cursor cursor) {
        return cursor.isNull(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_ID));
    }

    private boolean movieIsNull(Cursor cursor) {
        return cursor.isNull(cursor.getColumnIndex(MySQLiteHelper.TABLE_MOVIES_ID));
    }

    private boolean serieIsNull(Cursor cursor) {
        return seriesArray.get(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TABLE_SERIES_ID))) == null;
    }

    private Serie cursorToSerie(Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TABLE_SERIES_ID));
		String title = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_SERIES_TITLE));
        Serie newSerie = new Serie(id,title);
        if (!cursor.isNull(cursor.getColumnIndex(MySQLiteHelper.TABLE_SERIES_PICTURE))) {
            newSerie.setPicture(BitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.TABLE_SERIES_PICTURE))));
        }
        return newSerie;
    }

    private Movie cursorToMovie(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID));
        long idSerie = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID_SERIE));
        String title = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_TITLE));
        StatusWatch status = StatusWatch.getStatusWatch(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_STATUS)));
        int watchedTimes = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_WATCHED_TIMES));
        Bitmap picture = null;
        if (!cursor.isNull(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_PICTURE))) {
            picture = BitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_PICTURE)));
        }
        Time durationMovie = Time.valueOf(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_MOVIES_DURATION_MOVIE)));
        Movie movie = new Movie(id,idSerie,title,status,durationMovie,watchedTimes,picture);
        return movie;
    }

	private Season cursorToSeason(Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID));
        long idSerie = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID_SERIE));
        String title = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_TITLE));
        int numberSeason = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_NUMBER_SEASON));
        StatusWatch status = StatusWatch.getStatusWatch(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_STATUS)));
        Bitmap picture = null;
        if (!cursor.isNull(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_PICTURE))) {
            picture = BitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_PICTURE)));
        }
        boolean isAiring = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_AIRING)));
//        boolean isAiring = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_AIRING)) > 0;
		int numberEpisodes = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES));
        int numberEpisodesTotal = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES_TOTAL));
        Time durationEpisodes = Time.valueOf(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_SEASONS_DURATION_EPISODES)));
        return new Season(id,idSerie,title,numberSeason,status,isAiring,numberEpisodes,numberEpisodesTotal,durationEpisodes,picture);
	}

	private Duration cursorToDuration(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.TABLE_DURATIONS_ID));
        Time timeDuree = Time.valueOf(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TABLE_DURATIONS_DURATION)));
        boolean parDefaut = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.TABLE_DURATIONS_DEFAULT)) == 1;
        return new Duration(id,timeDuree,parDefaut);
    }

    private static final String TIME_SEPARATOR = ":";

    public static String hoursAndMinutesToTime(int hour, int minutes) {
        return String.format(Locale.getDefault(),"%02d" + TIME_SEPARATOR + "%02d" + TIME_SEPARATOR + "%02d", hour,minutes,0);
    }

    public long insertDuration(int hours, int minutes, boolean isDefault) {
        String time = hoursAndMinutesToTime(hours,minutes);
        ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.TABLE_DURATIONS_DURATION,time);
        newValues.put(MySQLiteHelper.TABLE_DURATIONS_DEFAULT,isDefault);
        if (isDefault) {
            database.execSQL("UPDATE " + MySQLiteHelper.TABLE_DURATIONS + " SET " + MySQLiteHelper.TABLE_DURATIONS_DEFAULT + " = 'false' WHERE " + MySQLiteHelper.TABLE_DURATIONS_DEFAULT + " = 'true'");
            for (Duration duration : durationsList) {
                duration.setDefault(false);
            }
        }
        long rowID = -1;
        try {
            rowID = database.insertOrThrow(MySQLiteHelper.TABLE_DURATIONS,null,newValues);
            if (rowID > -1) {
                Duration duration = new Duration((int)rowID,Time.valueOf(time),isDefault);
                durationsList.add(duration);
                Collections.sort(durationsList);
                notifyAllAdapters();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowID;
    }

    public long insertSerie(String titre, Bitmap bitmap) {
        ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.TABLE_SERIES_TITLE,titre);
        newValues.put(MySQLiteHelper.TABLE_SERIES_NB_SEASONS,0);
        if (bitmap != null) {
            newValues.put(MySQLiteHelper.TABLE_SERIES_PICTURE,BitmapUtility.getBitmapAsByteArray(bitmap,context));
        }
        long rowID = -1;
        try {
            rowID = database.insertOrThrow(MySQLiteHelper.TABLE_SERIES,null,newValues);
            if (rowID > -1) {
                Serie serie = new Serie(rowID,titre);
                if (bitmap != null) {
                    serie.setPicture(BitmapUtility.resize(bitmap,context));
                }
                seriesArray.put(serie.getID(),serie);
                notifyAllAdapters();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rowID;
    }

    public long insertSeason(long idSerie, String title, int numberSeason, StatusWatch status, boolean isAiring, int numberEpisodes, int numberEpisodesTotal, String durationEpisodes,@Nullable Bitmap bitmap) {
        long rowID = -1;
        if (idSerie > -1) {
            ContentValues newValuesSom = new ContentValues();
            newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID_SERIE,idSerie);
            newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_TITLE,title);
            newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_NUMBER_SEASON,numberSeason);
            newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_STATUS,status.toString());
            if (bitmap != null) {
                newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_PICTURE,BitmapUtility.getBitmapAsByteArray(bitmap,context));
            }
            try {
                rowID = database.insertOrThrow(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES, null, newValuesSom);
                if (rowID > -1)  {
                    if (numberEpisodesTotal < numberEpisodes) {
                        numberEpisodesTotal = numberEpisodes;
                    }
                    ContentValues newValuesSea = new ContentValues();
                    newValuesSea.put(MySQLiteHelper.TABLE_SEASONS_ID,rowID);
                    newValuesSea.put(MySQLiteHelper.TABLE_SEASONS_AIRING,String.valueOf(isAiring));
                    newValuesSea.put(MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES,numberEpisodes);
                    newValuesSea.put(MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES_TOTAL,numberEpisodesTotal);
                    newValuesSea.put(MySQLiteHelper.TABLE_SEASONS_DURATION_EPISODES,durationEpisodes);
                    try {
                        database.insertOrThrow(MySQLiteHelper.TABLE_SEASONS,null,newValuesSea);
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    database.execSQL("UPDATE " + MySQLiteHelper.TABLE_SERIES + " SET " + MySQLiteHelper.TABLE_SERIES_NB_SEASONS + " = " + MySQLiteHelper.TABLE_SERIES_NB_SEASONS + " + 1 WHERE " + MySQLiteHelper.TABLE_SERIES_ID + " = " + idSerie);
                    Season seasonAdd = new Season(rowID,idSerie,title,numberSeason,status,isAiring,numberEpisodes,numberEpisodesTotal,Time.valueOf(durationEpisodes),BitmapUtility.resize(bitmap,context));
                    /*if (bitmap != null) {
                        seasonAdd.setPicture(BitmapUtility.resize(bitmap));
                    }*/
                    seriesArray.get(idSerie).addSeasonOrMovie(seasonAdd);

                    notifyAllAdapters();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return rowID;
    }

    public void addEpisode(long idSerie, long idSaison) {
        if (idSerie > -1 && idSaison > -1) {
            SeasonOrMovie seasonOrMovie = seriesArray.get(idSerie).getSeasons().get(idSaison);
            if (seasonOrMovie instanceof  Season) {
                Season season = (Season)seasonOrMovie;
                if (season.isAiring() && season.getNumberEpisodes() == season.getNumberEpisodesTotal()) {
                    database.execSQL("UPDATE " + MySQLiteHelper.TABLE_SEASONS + " SET " + MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES_TOTAL + " = " + MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES_TOTAL + " + 1 WHERE " + MySQLiteHelper.TABLE_SEASONS_ID + " = " + idSaison);
                }
                if (!season.isAiring() && season.getNumberEpisodes() == season.getNumberEpisodesTotal()) {
//                    Toast.makeText(context, "Opération impossible", Toast.LENGTH_SHORT).show();
                }
                else {
                    database.execSQL("UPDATE " + MySQLiteHelper.TABLE_SEASONS + " SET " + MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES + " = " + MySQLiteHelper.TABLE_SEASONS_NUMBER_EPISODES + " + 1 WHERE " + MySQLiteHelper.TABLE_SEASONS_ID + " = " + idSaison);
                    season.addEpisode();
                    notifyAllAdapters();
                }
            }
        }
    }

    public void finishSeason(long idSerie, long idSeason) {
        if (idSerie > -1 && idSeason > -1) {
            Serie serie = seriesArray.get(idSerie);
            ContentValues updateValuesSom = new ContentValues();
            updateValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_STATUS,StatusWatch.WATCHED.toString());
//            database.execSQL("UPDATE " + MySQLiteHelper.TABLE_SEASONS_OR_MOVIES + " SET " + MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_STATUS + " = ? WHERE " + MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID + " = ?",new String[] { StatusWatch.WATCHED.toString(), String.valueOf(idSeason) });
            database.update(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES,updateValuesSom,MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID + " = ?",new String[] { String.valueOf(idSeason) });
            ContentValues updateValuesSea = new ContentValues();
            updateValuesSea.put(MySQLiteHelper.TABLE_SEASONS_AIRING,String.valueOf(false));
            database.update(MySQLiteHelper.TABLE_SEASONS,updateValuesSea,MySQLiteHelper.TABLE_SEASONS_ID + " = ?",new String[] { String.valueOf(idSeason) });
            serie.finishSeason(idSeason);
            notifyAllAdapters();
        }
    }

    public long insertMovie(long idSerie, String titre, StatusWatch status, int hours, int minutes,@Nullable Bitmap bitmap) {
        long rowID = -1;
        if (idSerie > -1) {
            String durationMovie = hoursAndMinutesToTime(hours,minutes);
            ContentValues newValuesSom = new ContentValues();
            newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID_SERIE,idSerie);
            newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_TITLE,titre);
            newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_STATUS,status.toString());
            if (status.equals(StatusWatch.WATCHED)) {
                newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_WATCHED_TIMES,1);
            }
            if (bitmap != null) {
                newValuesSom.put(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_PICTURE,BitmapUtility.getBitmapAsByteArray(bitmap,context));
            }
            try {
                rowID = database.insertOrThrow(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES, null, newValuesSom);
                if (rowID > -1 && idSerie > -1) {
                    ContentValues newValuesMov = new ContentValues();
                    newValuesMov.put(MySQLiteHelper.TABLE_MOVIES_ID,rowID);
                    newValuesMov.put(MySQLiteHelper.TABLE_MOVIES_DURATION_MOVIE,durationMovie);
                    try {
                        database.insertOrThrow(MySQLiteHelper.TABLE_MOVIES,null,newValuesMov);
                        Movie movieAdd = new Movie(rowID,idSerie,titre,status,Time.valueOf(durationMovie),BitmapUtility.resize(bitmap,context));
                        seriesArray.get(idSerie).addSeasonOrMovie(movieAdd);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    notifyAllAdapters();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return rowID;
    }

    public void changeDefaultDuration(String durationString) {
        for (Duration duration : durationsList) {
            duration.setDefault(false);
        }
        database.execSQL("UPDATE " + MySQLiteHelper.TABLE_DURATIONS + " SET " + MySQLiteHelper.TABLE_DURATIONS_DEFAULT + " = 'false' WHERE " + MySQLiteHelper.TABLE_DURATIONS_DEFAULT + " = 'true'");
        database.execSQL("UPDATE " + MySQLiteHelper.TABLE_DURATIONS + " SET " + MySQLiteHelper.TABLE_DURATIONS_DEFAULT + " = 'true' WHERE " + MySQLiteHelper.TABLE_DURATIONS_DURATION + " = '" + durationString + "'");
        if (durationsList.size() > 0) {
            durationsList.get(durationsList.indexOf(new Duration(-1,durationString,true))).setDefault(true);
        }
    }

    public int deleteSerie(long idSerie) {
        int rowsDeleted = 0;
        if (idSerie > -1) {
            rowsDeleted = database.delete(MySQLiteHelper.TABLE_SERIES,MySQLiteHelper.TABLE_SERIES_ID + " = ?",new String[]{ String.valueOf(idSerie) });
            if (rowsDeleted > 0) {
                seriesArray.delete(idSerie);
                notifyAllAdapters();
                vacuum();
            }
        }
        return rowsDeleted;
    }

    public int deleteSeason(long idSerie, long idSeason) {
        int rowsDeleted = 0;
        if (idSerie > -1) {
            if (idSeason > -1) {
                rowsDeleted = database.delete(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES,MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID + " = ?", new String[]{ String.valueOf(idSeason) });
                database.delete(MySQLiteHelper.TABLE_SEASONS,MySQLiteHelper.TABLE_SEASONS_ID + " = ?",new String[]{ String.valueOf(idSeason) });
                if (rowsDeleted > 0) {
                    seriesArray.get(idSerie).deleteSeasonOrMovie(idSeason);
                    notifyAllAdapters();
                    vacuum();
                }
            }
        }
        return rowsDeleted;
    }

    public int deleteMovie(long idSerie, long idMovie) {
        int rowsDeleted = 0;
        if (idSerie > -1) {
            if (idMovie > -1) {
                rowsDeleted = database.delete(MySQLiteHelper.TABLE_SEASONS_OR_MOVIES,MySQLiteHelper.TABLE_SEASONS_OR_MOVIES_ID + " = ?", new String[]{ String.valueOf(idMovie) });
                database.delete(MySQLiteHelper.TABLE_MOVIES,MySQLiteHelper.TABLE_MOVIES_ID + " = ?", new String[]{ String.valueOf(idMovie) });
                if (rowsDeleted > 0) {
                    seriesArray.get(idSerie).deleteSeasonOrMovie(idMovie);
                    notifyAllAdapters();
                    vacuum();
                }
            }
        }
        return rowsDeleted;
    }

    public int deleteDuration(String duration) {
        int rowsDeleted = database.delete(MySQLiteHelper.TABLE_DURATIONS,MySQLiteHelper.TABLE_DURATIONS_DURATION + " = ?", new String[]{ duration });
        if (rowsDeleted > 0) {
            durationsList.remove(new Duration(-1,Time.valueOf(duration),false));
            notifyAllAdapters();
            vacuum();
        }
        return rowsDeleted;
    }

    private void notifyAllAdapters() {
        for (StatusWatch statusWatch : StatusWatch.values()) {
            notifyAdapter(statusWatch);
        }
        notifyAdapter(null);
    }

    private void notifyAdapter(StatusWatch statusWatch) {
        if (mExpandableListAdapters.get(statusWatch) != null) {
            mExpandableListAdapters.get(statusWatch).notifyDataSetChanged();
        }
        else {
            if (statusWatch != null) {
                Log.e("TAG","mExpandableListAdapters.get(" + statusWatch.toString() + ") == null");
            }
            else {
                Log.e("TAG","mExpandableListAdapters.get(null) == null");
            }
        }
    }


    private static String[] concat(String[] A, String[] B) {
        int aLen = A.length;
        int bLen = B.length;
        String[] C= new String[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    public static <C> List<C> convertToList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) {
            return null;
        }
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++) {
            arrayList.add(sparseArray.valueAt(i));
        }
        return arrayList;
    }

}
