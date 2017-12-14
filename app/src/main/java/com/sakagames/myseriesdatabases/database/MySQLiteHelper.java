package com.sakagames.myseriesdatabases.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by robin on 03/08/17.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    //ATTRIBUTS
//    private static final String DATABASE_NAME = "Series.db";
    private static final int DATABASE_VERSION = 1;
	
    public static final String TABLE_SERIES = "Series";
    public static final String TABLE_SERIES_ID = "ser_id";
    public static final String TABLE_SERIES_TITLE = "ser_Title";
    public static final String TABLE_SERIES_NB_SEASONS = "ser_NbSeasons";
    public static final String TABLE_SERIES_PICTURE = "ser_Picture";

    public static final String[] tableSeriesColumns = {
            MySQLiteHelper.TABLE_SERIES_ID,
            MySQLiteHelper.TABLE_SERIES_TITLE,
            MySQLiteHelper.TABLE_SERIES_NB_SEASONS,
            MySQLiteHelper.TABLE_SERIES_PICTURE };

    public static final String TABLE_DURATIONS = "Durations";
    public static final String TABLE_DURATIONS_ID = "dur_id";
    public static final String TABLE_DURATIONS_DURATION = "dur_Duration";
    public static final String TABLE_DURATIONS_DEFAULT = "dur_IsDefault";

    public static final String[] tableDurationsColumns = {
            MySQLiteHelper.TABLE_DURATIONS_ID,
            MySQLiteHelper.TABLE_DURATIONS_DURATION,
            MySQLiteHelper.TABLE_DURATIONS_DEFAULT };

    public static final String TABLE_SEASONS_OR_MOVIES = "SeasonsOrMovies";
    public static final String TABLE_SEASONS_OR_MOVIES_ID = "som_id";
    public static final String TABLE_SEASONS_OR_MOVIES_ID_SERIE = "som_idSerie";
    public static final String TABLE_SEASONS_OR_MOVIES_TITLE = "som_Title";
    public static final String TABLE_SEASONS_OR_MOVIES_NUMBER_SEASON = "som_NumberSeason";
    public static final String TABLE_SEASONS_OR_MOVIES_STATUS = "som_Status";
    public static final String TABLE_SEASONS_OR_MOVIES_WATCHED_TIMES = "som_WatchedTimes";
    public static final String TABLE_SEASONS_OR_MOVIES_PICTURE = "som_Picture";

    public static final String[] tableSeasonsOrMoviesColumns = {
            TABLE_SEASONS_OR_MOVIES_ID,
            TABLE_SEASONS_OR_MOVIES_ID_SERIE,
            TABLE_SEASONS_OR_MOVIES_TITLE,
            TABLE_SEASONS_OR_MOVIES_NUMBER_SEASON,
            TABLE_SEASONS_OR_MOVIES_STATUS,
            TABLE_SEASONS_OR_MOVIES_WATCHED_TIMES,
            TABLE_SEASONS_OR_MOVIES_PICTURE };

    public static final String TABLE_SEASONS = "Seasons";
    public static final String TABLE_SEASONS_ID = "sea_id";
    public static final String TABLE_SEASONS_AIRING = "sea_Airing";
    public static final String TABLE_SEASONS_NUMBER_EPISODES = "sea_NumberEpisodes";
    public static final String TABLE_SEASONS_NUMBER_EPISODES_TOTAL = "sea_NumberEpisodesTotal";
    public static final String TABLE_SEASONS_DURATION_EPISODES = "sea_DurationEpisodes";

    public static final String[] tableSaisonsColumns = {
            TABLE_SEASONS_ID,
            TABLE_SEASONS_NUMBER_EPISODES,
            TABLE_SEASONS_NUMBER_EPISODES_TOTAL,
            TABLE_SEASONS_DURATION_EPISODES };

    public static final String TABLE_MOVIES = "Movies";
    public static final String TABLE_MOVIES_ID = "mov__id";
    public static final String TABLE_MOVIES_DURATION_MOVIE = "mov_DurationMovie";

    public static final String[] tableMoviesColumns = {
            TABLE_MOVIES_ID,
            TABLE_MOVIES_DURATION_MOVIE };


    public static final String VIEW_ALL_SERIES = "AllSeries";

    public static final String[] viewSeriesColumns = {
            TABLE_SERIES_ID,
            TABLE_SERIES_TITLE,
            TABLE_SERIES_NB_SEASONS,
            TABLE_SERIES_PICTURE,
            TABLE_SEASONS_OR_MOVIES_ID,
            TABLE_SEASONS_OR_MOVIES_ID_SERIE,
            TABLE_SEASONS_OR_MOVIES_NUMBER_SEASON,
            TABLE_SEASONS_OR_MOVIES_TITLE,
            TABLE_SEASONS_OR_MOVIES_STATUS,
            TABLE_SEASONS_OR_MOVIES_WATCHED_TIMES,
            TABLE_SEASONS_OR_MOVIES_PICTURE,
            TABLE_SEASONS_ID,
            TABLE_SEASONS_AIRING,
            TABLE_SEASONS_NUMBER_EPISODES,
            TABLE_SEASONS_NUMBER_EPISODES_TOTAL,
            TABLE_SEASONS_DURATION_EPISODES,
            TABLE_MOVIES_ID,
            TABLE_MOVIES_DURATION_MOVIE };


    private static final String CREATE_TABLE_SERIES = "CREATE TABLE " +
            TABLE_SERIES + "(" +
            TABLE_SERIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TABLE_SERIES_TITLE +" TEXT," +
            TABLE_SERIES_NB_SEASONS + " INT DEFAULT 0, " +
            TABLE_SERIES_PICTURE + " BLOB, " +
            "UNIQUE (" + TABLE_SERIES_TITLE + ")" +
            ");";
			
    private static final String CREATE_TABLE_DURATIONS = "CREATE TABLE " +
            TABLE_DURATIONS + "(" +
            TABLE_DURATIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TABLE_DURATIONS_DURATION + " TEXT," +
            TABLE_DURATIONS_DEFAULT + " BOOLEAN, " +
            "UNIQUE (" + TABLE_DURATIONS_DURATION + ")" +
            ");";

    public static final String CREATE_TABLE_SEASONS_OR_MOVIES = "CREATE TABLE " +
            TABLE_SEASONS_OR_MOVIES + "(" +
            TABLE_SEASONS_OR_MOVIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TABLE_SEASONS_OR_MOVIES_ID_SERIE + " INT, " +
            TABLE_SEASONS_OR_MOVIES_TITLE + " TEXT, " +
            TABLE_SEASONS_OR_MOVIES_NUMBER_SEASON + " INT, " +
            TABLE_SEASONS_OR_MOVIES_STATUS + " TEXT, " +
            TABLE_SEASONS_OR_MOVIES_WATCHED_TIMES + " INT DEFAULT 0," +
            TABLE_SEASONS_OR_MOVIES_PICTURE + " BLOB, " +
            "FOREIGN KEY (" + TABLE_SEASONS_OR_MOVIES_ID_SERIE + ") REFERENCES " + TABLE_SERIES + "(" + TABLE_SERIES_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
            "UNIQUE (" + TABLE_SEASONS_OR_MOVIES_ID_SERIE + "," + TABLE_SEASONS_OR_MOVIES_NUMBER_SEASON + ")" +
            ");";
			
    private static final String CREATE_TABLE_SEASONS = "CREATE TABLE " +
            TABLE_SEASONS + "(" +
            TABLE_SEASONS_ID + " INTEGER, " +
            TABLE_SEASONS_AIRING + " BOOLEAN, " +
            TABLE_SEASONS_NUMBER_EPISODES + " INT, " +
            TABLE_SEASONS_NUMBER_EPISODES_TOTAL + " INT, " +
            TABLE_SEASONS_DURATION_EPISODES + " TEXT, " +
            "FOREIGN KEY (" + TABLE_SEASONS_ID + ") REFERENCES " + TABLE_SEASONS_OR_MOVIES + "(" + TABLE_SEASONS_OR_MOVIES_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
            "FOREIGN KEY (" + TABLE_SEASONS_DURATION_EPISODES + ") REFERENCES " + TABLE_DURATIONS + "(" + TABLE_DURATIONS_DURATION + ") ON UPDATE CASCADE, " +
            "CHECK (" + TABLE_SEASONS_NUMBER_EPISODES + " <= " + TABLE_SEASONS_NUMBER_EPISODES_TOTAL + ")" +
            ");";

    private static final String CREATE_TABLE_MOVIES = "CREATE TABLE " +
            TABLE_MOVIES + "(" +
            TABLE_MOVIES_ID + " INTEGER PRIMARY KEY, " +
            TABLE_MOVIES_DURATION_MOVIE + " TEXT, " +
            "FOREIGN KEY (" + TABLE_MOVIES_ID + ") REFERENCES " + TABLE_SEASONS_OR_MOVIES + "(" + TABLE_SEASONS_OR_MOVIES_ID + ") ON UPDATE CASCADE ON DELETE CASCADE " +
            ");";

    private static final String CREATE_VIEW_SERIES_SAISONS_FILMS = "CREATE VIEW " +
            VIEW_ALL_SERIES + " " +
            "AS SELECT * FROM ((" +
            TABLE_SERIES + " LEFT OUTER JOIN " +
            TABLE_SEASONS_OR_MOVIES + " ON " + TABLE_SERIES_ID + " = " + TABLE_SEASONS_OR_MOVIES_ID_SERIE +
            ") LEFT OUTER JOIN " +
            TABLE_SEASONS + " ON " + TABLE_SEASONS_ID + " = " + TABLE_SEASONS_OR_MOVIES_ID +
            ") LEFT OUTER JOIN " +
            TABLE_MOVIES + " ON " + TABLE_MOVIES_ID + " = " + TABLE_SEASONS_OR_MOVIES_ID +
            " GROUP BY " +
            TABLE_SERIES_ID + ", " + TABLE_SEASONS_OR_MOVIES_ID +";";


    //CONSTRUCTEUR
    public MySQLiteHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }
	
	
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SERIES);
        db.execSQL(CREATE_TABLE_DURATIONS);
        db.execSQL(CREATE_TABLE_SEASONS_OR_MOVIES);
        db.execSQL(CREATE_TABLE_SEASONS);
        db.execSQL(CREATE_TABLE_MOVIES);
        db.execSQL(CREATE_VIEW_SERIES_SAISONS_FILMS);
    }
	
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEASONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEASONS_OR_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DURATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIES);
        db.execSQL("DROP VIEW IF EXISTS " + VIEW_ALL_SERIES);
        onCreate(db);
    }

}
