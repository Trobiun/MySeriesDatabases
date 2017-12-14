package com.sakagames.myseriesdatabases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sakagames.myseriesdatabases.database.Duration;
import com.sakagames.myseriesdatabases.database.Season;
import com.sakagames.myseriesdatabases.database.SeasonOrMovie;
import com.sakagames.myseriesdatabases.database.Serie;
import com.sakagames.myseriesdatabases.database.SeriesDataSource;
import com.sakagames.myseriesdatabases.database.StatusWatch;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by robin on 10/08/17.
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    //ATTRIBUTS STATICS
    //DIALOGS SEASONS OR MOVIES
    private static AlertDialog.Builder mBuilder;

    private static Bitmap mPicture;

    //DIALOG SEASON
    private static AlertDialog mDialogSeason;
    private static EditText mTitleSeasonEditText;
    private static EditText mNumberEpisodesSeasonEditText;
    private static EditText mNumberEpisodesTotalSeasonEditText;
    private static EditText mNumberSeasonEditText;
    private static ToggleButton mAiringSeason;
    private static RadioGroup mStatusSeasonRadioGroup;
    private static ArrayAdapter<Duration> mDurationEpisodesSpinnerAdapter;
    private static Spinner mDurationEpisodesSeasonSpinner;
    private static ImageButton mPictureSeasonButton;

    //DIALOG MOVIE
    private static AlertDialog mDialogMovie;
    private static EditText mTitleMovieEditText;
    private static RadioGroup mStatusMovieRadioGroup;
    private static MyTimePicker mDurationMovieTimePicker;
    private static ImageButton mPictureMovieButton;


    //ATTRIBUTS
    private Context context;
    private StatusWatch mStatusWatch;

    //CONSTRUCTEUR
    public MyExpandableListAdapter(Context context, StatusWatch mStatusWatch) {
        this.context = context;
        this.mStatusWatch = mStatusWatch;
        initBuilder();
        initDialogSeason();
        initDialogMovie();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mDurationEpisodesSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return SeriesDataSource.getInstance(context).getSeriesList(mStatusWatch).size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return SeriesDataSource.getInstance(context).getSeriesList(mStatusWatch).get(listPosition).getSeasons().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return SeriesDataSource.getInstance(context).getSeriesList(mStatusWatch).get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        List<SeasonOrMovie> seasonOrMovies = SeriesDataSource.getInstance(context).getSeriesList(mStatusWatch).get(listPosition).getSeasonsList();
        return seasonOrMovies.get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int listPosition, boolean isLastChild,View convertView,final ViewGroup parent) {
        final Serie serie = (Serie)getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        ImageView listTitleImageView = (ImageView)convertView.findViewById(R.id.list_title_picture);
        listTitleImageView.setImageBitmap(serie.getPicture());
        TextView listTitleTextView = (TextView)convertView.findViewById(R.id.list_title);
        listTitleTextView.setText(serie.getTitre());
        TextView listSaisonTextView = (TextView)convertView.findViewById(R.id.list_title_saisons);
        listSaisonTextView.setText("" + getChildrenCount(listPosition));
        final Button listTitleButton = (Button)convertView.findViewById(R.id.list_title_add_saison);
        listTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPicture = null;
                PopupMenu popupMenu = new PopupMenu(context,listTitleButton);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_add_season_movie,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.add_season_popup :
                                mDialogSeason.setButton(AlertDialog.BUTTON_POSITIVE,context.getString(R.string.OK),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String title = "";
                                        if (mTitleSeasonEditText.getText() != null) {
                                            title = mTitleSeasonEditText.getText().toString();
                                        }
                                        if (mNumberSeasonEditText.getText().length() > 0 && mNumberEpisodesSeasonEditText.getText().length() > 0  && mNumberEpisodesTotalSeasonEditText.getText().length() > 0 && mDurationEpisodesSeasonSpinner.getSelectedItem() != null) {
                                            int numberSeason = Integer.valueOf(mNumberSeasonEditText.getText().toString());
                                            if (title.isEmpty()) {
                                                title = context.getString(R.string.season) + " n°";
                                                title += (numberSeason < 10) ? "0" + numberSeason : numberSeason;
                                            }
                                            StatusWatch status = null;
                                            switch (mStatusSeasonRadioGroup.getCheckedRadioButtonId()) {
                                                case R.id.status_season_watching:
                                                    status = StatusWatch.WATCHING;
                                                    break;
                                                case R.id.status_season_watched:
                                                    status = StatusWatch.WATCHED;
                                                    break;
                                                case R.id.status_season_stalled:
                                                    status = StatusWatch.STALLED;
                                                    break;
                                                case R.id.status_season_dropped:
                                                    status = StatusWatch.DROPPED;
                                                    break;
                                                case R.id.status_season_want_to_watch:
                                                    status = StatusWatch.WANT_TO_WATCH;
                                                    break;
                                            }
                                            if (status != null) {
                                                boolean isAiring = mAiringSeason.isChecked();
                                                int numberEpisodes = Integer.valueOf(mNumberEpisodesSeasonEditText.getText().toString());
                                                int numberEpisodesTotal = Integer.valueOf(mNumberEpisodesTotalSeasonEditText.getText().toString());
                                                String durationEpisodes = mDurationEpisodesSeasonSpinner.getSelectedItem().toString();
                                                SeriesDataSource.getInstance(context).insertSeason(serie.getID(),title,numberSeason,status,isAiring,numberEpisodes,numberEpisodesTotal,durationEpisodes,mPicture);
                                                notifyDataSetChanged();
                                            }
                                            else {
                                                Snackbar.make(parent,context.getString(R.string.dialog_status_error),Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            Snackbar.make(parent,R.string.dialog_season_error,Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                mDialogSeason.show();
                                break;
                            case R.id.add_movie_popup :
                                mDialogMovie.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (mTitleMovieEditText.getText() != null) {
                                            String title = mTitleMovieEditText.getText().toString();
                                            int hours = mDurationMovieTimePicker.getCurrentHour();
                                            int minutes = mDurationMovieTimePicker.getCurrentMinute();
                                            StatusWatch status = null;
                                            switch (mStatusMovieRadioGroup.getCheckedRadioButtonId()) {
                                                case R.id.status_movie_watching :
                                                    status = StatusWatch.WATCHING;
                                                    break;
                                                case R.id.status_movie_watched :
                                                    status = StatusWatch.WATCHED;
                                                    break;
                                                case R.id.status_movie_want_to_watch :
                                                    status = StatusWatch.WANT_TO_WATCH;
                                                    break;
                                                case R.id.status_movie_stalled :
                                                    status = StatusWatch.STALLED;
                                                    break;
                                                case R.id.status_movie_dropped :
                                                    status = StatusWatch.DROPPED;
                                            }
                                            if (status != null) {
                                                SeriesDataSource.getInstance(context).insertMovie(serie.getID(),title,status,hours,minutes,mPicture);
                                                notifyDataSetChanged();
                                            }
                                            else {
                                                Snackbar.make(parent,context.getString(R.string.dialog_status_error),Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            Snackbar.make(parent,R.string.dialog_season_error,Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                mDialogMovie.show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int listPosition, final int expandedListPosition, boolean b, View convertView, ViewGroup parent) {
        final SeasonOrMovie seasonOrMovie = (SeasonOrMovie)getChild(listPosition,expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item,null);
        }
        ImageView expandedListImageView = (ImageView)convertView.findViewById(R.id.expanded_list_picture);
        expandedListImageView.setImageBitmap(seasonOrMovie.getPicture());
        TextView expandedListTextView = (TextView)convertView.findViewById(R.id.expanded_list_title);
        expandedListTextView.setText(seasonOrMovie.getTitre());
        TextView expandedListTextViewEpisodes = (TextView)convertView.findViewById(R.id.expanded_list_episodes);
        if (seasonOrMovie instanceof Season) {
            Season season = (Season)seasonOrMovie;
            String textEpisodes = season.getNumberEpisodes() + "/" + season.getNumberEpisodesTotal();
            if (season.isAiring()) {
                textEpisodes += "+";
            }
            expandedListTextViewEpisodes.setText(/*context.getString(R.string.number_episodes) + */textEpisodes);
        }
        else {
            expandedListTextViewEpisodes.setText("1");
        }
        expandedListTextView.setTextColor(seasonOrMovie.getStatus().getColor());
        expandedListTextViewEpisodes.setTextColor(seasonOrMovie.getStatus().getColor());
        Button expandedListButton = (Button)convertView.findViewById(R.id.expanded_list_button);
        if (seasonOrMovie instanceof Season && seasonOrMovie.isWatching()) {
            expandedListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SeriesDataSource.getInstance(context).addEpisode(seasonOrMovie.getIDSerie(),seasonOrMovie.getID());
                    notifyDataSetChanged();
                }
            });
            expandedListButton.setVisibility(View.VISIBLE);
        }
        else {
            expandedListButton.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    private void initBuilder() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mBuilder = new AlertDialog.Builder(context,android.R.style.Theme_Material_Dialog_Alert);
//        }
//        else {
            mBuilder = new AlertDialog.Builder(context);
//        }
    }

    //DIALOG SEASON
    private void initDialogSeason() {
        mBuilder.setTitle(context.getString(R.string.add_season_dialog))
                .setCancelable(true)
                .setNegativeButton(context.getString(R.string.cancel),null);
        mDialogSeason = mBuilder.create();
        LayoutInflater inflater = mDialogSeason.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_season,null);
        mTitleSeasonEditText = (EditText)layout.findViewById(R.id.season_title_edit_text);
        mNumberSeasonEditText = (EditText)layout.findViewById(R.id.season_number_edit_text);
        mStatusSeasonRadioGroup = (RadioGroup)layout.findViewById(R.id.status_season_group);
        mAiringSeason = (ToggleButton)layout.findViewById(R.id.airing_season_toggle_button);
        mNumberEpisodesSeasonEditText = (EditText)layout.findViewById(R.id.number_episodes_season_edit_text);
        mNumberEpisodesTotalSeasonEditText = (EditText)layout.findViewById(R.id.number_episodes_total_season_edit_text);
        mDurationEpisodesSeasonSpinner = (Spinner)layout.findViewById(R.id.duration_episodes_spinner);
        mDurationEpisodesSpinnerAdapter = new ArrayAdapter<Duration>(context,android.R.layout.simple_spinner_dropdown_item,SeriesDataSource.getInstance(context).getDureesList());
        mDurationEpisodesSeasonSpinner.setAdapter(mDurationEpisodesSpinnerAdapter);
        mPictureSeasonButton = (ImageButton)layout.findViewById(R.id.season_picture_image_button);
        mDialogSeason.setView(layout);
    }

    //DIALOG MOVIE
    private void initDialogMovie() {
        mBuilder.setTitle(context.getString(R.string.add_movie_dialog))
                .setCancelable(true)
                .setNegativeButton(context.getString(R.string.cancel),null);
        mDialogMovie = mBuilder.create();
        LayoutInflater inflater = mDialogMovie.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_movie,null);
        mTitleMovieEditText = (EditText)layout.findViewById(R.id.movie_title_edit_text);
        mStatusMovieRadioGroup = (RadioGroup)layout.findViewById(R.id.status_movie_group);
        mDurationMovieTimePicker = (MyTimePicker)layout.findViewById(R.id.movie_duration_time_picker);
        mDurationMovieTimePicker.setIs24HourView(true);
        mDurationMovieTimePicker.setCurrentHour(0);
        mDurationMovieTimePicker.setCurrentMinute(0);
        mPictureMovieButton = (ImageButton)layout.findViewById(R.id.movie_picture_image_button);
        mDialogMovie.setView(layout);
    }


    //MUTATEURS
    public static void setBitmapSeason(Bitmap bitmap) {
        mPicture = bitmap;
        mPictureSeasonButton.setImageBitmap(mPicture);
    }

    public static void setBitmapMovie(Bitmap bitmap) {
        mPicture = bitmap;
        mPictureMovieButton.setImageBitmap(mPicture);
    }

}
