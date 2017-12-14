package com.sakagames.myseriesdatabases.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.sakagames.myseriesdatabases.MainActivity;
import com.sakagames.myseriesdatabases.MyExpandableListAdapter;
import com.sakagames.myseriesdatabases.R;
import com.sakagames.myseriesdatabases.database.Season;
import com.sakagames.myseriesdatabases.database.SeasonOrMovie;
import com.sakagames.myseriesdatabases.database.Serie;
import com.sakagames.myseriesdatabases.database.SeriesDataSource;
import com.sakagames.myseriesdatabases.database.StatusWatch;

/**
 * Created by robin on 25/08/17.
 */

public class TabFragment extends Fragment {

    private static final String ARG_PARAM1 = "status";

    private StatusWatch mStatus;

    protected MyExpandableListAdapter mExpandableListAdapter;
    protected ExpandableListView mExpandableListView;
    protected OnFragmentInteractionListener mListener;

    public TabFragment() {

    }

    public static TabFragment newInstance(StatusWatch statusWatch) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1,statusWatch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_PARAM1,mStatus);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStatus = (StatusWatch)getArguments().getSerializable(ARG_PARAM1);
        }
       /* if (savedInstanceState != null) {
            mStatus = (StatusWatch)savedInstanceState.getSerializable(ARG_PARAM1);
        }*/
        mExpandableListAdapter = new MyExpandableListAdapter(getContext(),mStatus);
        Log.e("ERROR","LOL");
        SeriesDataSource.getInstance(getContext()).putExpandableListAdapter(mStatus,mExpandableListAdapter);
    }

    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_tab,container,false);
        mExpandableListView = (ExpandableListView)layout.findViewById(R.id.expandable_list_view);
        initExpandableListView();
        return layout;
    }

    protected void initExpandableListView() {
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(), dataSource.getSeriesList().get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
            }
        });
        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(),dataSource.getSeriesList().get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();

            }
        });
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(getContext(),SeriesDataSource.getInstance(getContext()).getSeriesList().get(groupPosition) + " -> " + SeriesDataSource.getInstance(getContext()).getSeriesList().get(groupPosition).getSeasonsList().get(childPosition).getTitre(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                final int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(),view);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_series,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_serie:

                                    break;
                                case R.id.delete_serie:
                                    Serie serie = (Serie)mExpandableListAdapter.getGroup(groupPosition);
                                    SeriesDataSource.getInstance(getContext()).deleteSerie(serie.getID());
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
                else {
                    final int childPosition = ExpandableListView.getPackedPositionChild(id);
                    final PopupMenu popupMenu = new PopupMenu(getActivity(),view);
                    final SeasonOrMovie seasonOrMovie = (SeasonOrMovie)mExpandableListAdapter.getChild(groupPosition,childPosition);
                    if (seasonOrMovie instanceof Season) {
                        if (seasonOrMovie.isWatching()) {
                            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_seasons_watching,popupMenu.getMenu());
                        }
                        else {
                            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_seasons,popupMenu.getMenu());
                        }
                    }
                    else {
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_movies,popupMenu.getMenu());
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.finish_season :
                                    SeriesDataSource.getInstance(getContext()).finishSeason(seasonOrMovie.getIDSerie(),seasonOrMovie.getID());
                                case R.id.edit_season:

                                    break;
                                case R.id.delete_season:
                                    SeriesDataSource.getInstance(getContext()).deleteSeason(seasonOrMovie.getIDSerie(),seasonOrMovie.getID());
                                    break;
                                case R.id.edit_movie :
                                    Toast.makeText(getContext(), "edit movie was clicked", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.delete_movie :
                                    SeriesDataSource.getInstance(getContext()).deleteMovie(seasonOrMovie.getIDSerie(),seasonOrMovie.getID());
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
                return false;
            }
        });
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
            mListener = (OnFragmentInteractionListener)context;

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
