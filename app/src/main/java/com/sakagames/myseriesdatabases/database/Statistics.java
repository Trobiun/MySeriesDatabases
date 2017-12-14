package com.sakagames.myseriesdatabases.database;

import android.content.Context;

import java.util.List;

/**
 * Created by robin on 26/08/17.
 */

public class Statistics {

    private static Context context;
    private static List<Serie> series;


    public static void setContext(Context context) {
        Statistics.context = context;
    }

    public static void setSeriesList() {

    }

    public static void actualiser() {
        Statistics.series = SeriesDataSource.getInstance(Statistics.context).getSeriesList();
    }

    public static int getNumberOfSeries() {
        return Statistics.series.size();
    }

    private static int getNumberOfSeasonsOrMovies(Class<?> c) {
        int numberSeasonsOrMovies = 0;
        for (Serie serie : Statistics.series) {
            List<SeasonOrMovie> seasonsOrMovies = serie.getSeasonsList();
            for (SeasonOrMovie seasonOrMovie : seasonsOrMovies) {
                if (c.isInstance(seasonOrMovie)) {
                    numberSeasonsOrMovies++;
                }
            }
        }
        return numberSeasonsOrMovies;
    }

    public static int getNumberOfSeasonsAndMovies() {
        return getNumberOfSeasonsOrMovies(SeasonOrMovie.class);
    }

    public static int getNumberOfSeasons() {
        return getNumberOfSeasonsOrMovies(Season.class);
    }

    public static int getNumberOfMovies() {
        return getNumberOfSeasonsOrMovies(Movie.class);
    }

    public static int getNumberTotalOfEpisodes() {
        int numberTotalEpisodes = 0;
        for (Serie serie : Statistics.series) {
            List<SeasonOrMovie> seasonsOrMovies = serie.getSeasonsList();
            for (SeasonOrMovie seasonorMovie : seasonsOrMovies) {
                if (seasonorMovie instanceof Season) {
                    numberTotalEpisodes += ((Season)seasonorMovie).getNumberEpisodes();
                }
            }
        }
        return numberTotalEpisodes;
    }


}
