package com.sakagames.myseriesdatabases.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sakagames.myseriesdatabases.R;
import com.sakagames.myseriesdatabases.database.Statistics;

import org.w3c.dom.Text;


public class StatisticsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    public static StatisticsFragment newInstance() {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout =  inflater.inflate(R.layout.fragment_statistics, container, false);

        TextView totalSeriesTextView = (TextView)layout.findViewById(R.id.statistics_series);
        totalSeriesTextView.setText("" + Statistics.getNumberOfSeries());

        TextView totalSeasonsTextView = (TextView)layout.findViewById(R.id.statistics_seasons);
        totalSeasonsTextView.setText("" + Statistics.getNumberOfSeasons());

        TextView totalMoviesTextView = (TextView)layout.findViewById(R.id.statistics_movies);
        totalMoviesTextView.setText("" + Statistics.getNumberOfMovies());

        TextView totalEpisodesTextView = (TextView)layout.findViewById(R.id.statistics_total_episodes_seasons);
        totalEpisodesTextView.setText("" + Statistics.getNumberTotalOfEpisodes());

        return layout;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
