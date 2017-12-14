package com.sakagames.myseriesdatabases;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sakagames.myseriesdatabases.database.Statistics;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Statistics.setContext(this);
        Statistics.actualiser();

        TextView totalSeriesTextView = (TextView)findViewById(R.id.statistics_series);
        totalSeriesTextView.setText("" + Statistics.getNumberOfSeries());

        TextView totalSeasonsTextView = (TextView)findViewById(R.id.statistics_seasons);
        totalSeasonsTextView.setText("" + Statistics.getNumberOfSeasons());

        TextView totalMoviesTextView = (TextView)findViewById(R.id.statistics_movies);
        totalMoviesTextView.setText("" + Statistics.getNumberOfMovies());

        TextView totalEpisodesTextView = (TextView)findViewById(R.id.statistics_total_episodes_seasons);
        totalEpisodesTextView.setText("" + Statistics.getNumberTotalOfEpisodes());


    }
}
