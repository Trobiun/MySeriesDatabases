package com.sakagames.myseriesdatabases;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sakagames.myseriesdatabases.Fragments.MyTabsPagerAdapter;
import com.sakagames.myseriesdatabases.Fragments.StatisticsFragment;
import com.sakagames.myseriesdatabases.Fragments.TabFragment;
import com.sakagames.myseriesdatabases.database.Duration;
import com.sakagames.myseriesdatabases.database.SeriesDataSource;
import com.sakagames.myseriesdatabases.database.StatusWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements TabFragment.OnFragmentInteractionListener, StatisticsFragment.OnFragmentInteractionListener {

    //ATTRIBUTS STATICS
    public static final String DATABASE_NAME_ARG = "databaseName";
    public static final int REQUEST_CODE_PICKER_IMAGE_SERIE = 1;
    public static final int REQUEST_CODE_PICKER_IMAGE_SEASON = 2;
    public static final int REQUEST_CODE_PICKER_IMAGE_MOVIE = 3;

    //ATTRIBUTS
    public static String databaseName;
    private List<String> mDatabasesList;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private AppBarLayout mAppBar;
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private MyTabsPagerAdapter mPagerAdapter;

    private ImageButton imageButton;
    private Bitmap bitmapForSerie;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            databaseName = savedInstanceState.getString(DATABASE_NAME_ARG);
        }

        SeriesDataSource.newInstance(getApplicationContext()).open();
//        setTitle(databaseName.substring(0,databaseName.length() - 3));
        //DATABASES
        String []databases = getApplicationContext().databaseList();
        mDatabasesList = new ArrayList<>();
        for (String database : databases) {
            if (database.endsWith(".db")) {
                mDatabasesList.add(database);
            }
        }
        Collections.sort(mDatabasesList);

        //DRAWER
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        Drawable homeMenu = AppCompatResources.getDrawable(this,R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(homeMenu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationView = (NavigationView)findViewById(R.id.nav_drawer);
        Menu menuDatabases = mNavigationView.getMenu().findItem(R.id.nav_databases_item).getSubMenu();
        for (int i = 0; i < mDatabasesList.size(); i++) {
            menuDatabases.add(0,i,0,mDatabasesList.get(i).substring(0,mDatabasesList.get(i).length() - 3));
            menuDatabases.getItem(i).setCheckable(true);
        }
        Log.e("TAG",mDatabasesList.indexOf(databaseName) + "");
        mNavigationView.setNavigationItemSelectedListener(new NavigationViewItemSelectedListener());
        menuDatabases.findItem(mDatabasesList.indexOf(databaseName)).setChecked(true);
        mNavigationView.setCheckedItem(mDatabasesList.indexOf(databaseName));

         //TAB LAYOUT AND VIEW PAGER
        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mPager = (ViewPager)findViewById(R.id.view_pager);
        mPagerAdapter = new MyTabsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mPager);
        mTabLayout.getTabAt(0).setText(getString(R.string.tab_total));
        mTabLayout.getTabAt(1).setText(getString(R.string.tab_watching));
        mTabLayout.getTabAt(2).setText(getString(R.string.tab_watched));
        mTabLayout.getTabAt(3).setText(getString(R.string.tab_stalled));
        mTabLayout.getTabAt(4).setText(getString(R.string.tab_dropped));
        mTabLayout.getTabAt(5).setText(getString(R.string.tab_want_to_watch));
        LinearLayout tabsContainer = (LinearLayout)mTabLayout.getChildAt(0);
        for (int i = 1;i < mTabLayout.getTabCount(); i++) {
            LinearLayout item = (LinearLayout)tabsContainer.getChildAt(i);
            TextView tv = (TextView) item.getChildAt(1);
            switch (i) {
                case 1 :
                    tv.setTextColor(StatusWatch.WATCHING.getColor());
                    break;
                case 2 :
                    tv.setTextColor(StatusWatch.WATCHED.getColor());
                    break;
                case 3 :
                    tv.setTextColor(StatusWatch.STALLED.getColor());
                    break;
                case 4 :
                    tv.setTextColor(StatusWatch.DROPPED.getColor());
                    break;
                case 5 :
                    tv.setTextColor(StatusWatch.WANT_TO_WATCH.getColor());
                    break;
            }
        }
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        SeriesDataSource.getInstance(getApplicationContext()).actualiser();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_serie_fab :
                bitmapForSerie = null;
                AlertDialog.Builder builder;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    builder = new AlertDialog.Builder(MainActivity.this,android.R.style.Theme_Material_Dialog_Alert);
//                }
//                else {
                    builder = new AlertDialog.Builder(MainActivity.this);
//                }
                builder.setTitle(getString(R.string.add_serie))
                        .setNegativeButton(getString(R.string.cancel),null);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_serie,null);
                final EditText title = (EditText)layout.findViewById(R.id.serie_title_edit_text);
                imageButton = (ImageButton)layout.findViewById(R.id.serie_picture_image_button);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_CODE_PICKER_IMAGE_SERIE);
                    }
                });
                builder.setView(layout);
                builder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (title.getText() != null) {
                            SeriesDataSource.getInstance(getApplicationContext()).insertSerie(title.getText().toString(),bitmapForSerie);
                        }
                    }
                });
                builder.show();
        }
    }

    public void onClickPictureSeasons(View view) {
        if (view.getId() == R.id.season_picture_image_button) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICKER_IMAGE_SEASON);
        }
    }

    public void onClickPictureMovie(View view) {
        if (view.getId() == R.id.movie_picture_image_button) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICKER_IMAGE_MOVIE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.insert_options_menu:
                long start = System.currentTimeMillis();
                Random random = new Random();
                SeriesDataSource.getInstance(getApplicationContext()).insertDuration(0, 24, true);
                SeriesDataSource.getInstance(getApplicationContext()).insertDuration(0, 35, true);
                SeriesDataSource.getInstance(getApplicationContext()).insertDuration(0, 50, true);
                for (int i = 0; i < 25; i++) {
                    long idZ = SeriesDataSource.getInstance(getApplicationContext()).insertSerie("ZZZZ", null);
                    SeriesDataSource.getInstance(getApplicationContext()).insertSeason(idZ, "Season n°" + 10, 10, StatusWatch.WATCHING, true, 10, 24, "00:50:00", null);
                    String titleSerie = "Test ";
                    titleSerie += (i < 10) ? "0" + i : i;
                    long id = SeriesDataSource.getInstance(getApplicationContext()).insertSerie(titleSerie, null);
                    for (int j = 0; j < i; j++) {
                        int numeroSaison = (int) (j * Math.random() + 1);
                        String numeroSaisonString = (numeroSaison < 10) ? "0" + numeroSaison : "" + numeroSaison;
                        SeriesDataSource.getInstance(getApplicationContext()).insertSeason(id, "Season n°" + numeroSaisonString, numeroSaison, StatusWatch.randomEnum(StatusWatch.class), random.nextBoolean(), 10, 12, "00:50:00", null);
                    }
                    long idABC = SeriesDataSource.getInstance(getApplicationContext()).insertSerie("ABC", null);
                    SeriesDataSource.getInstance(getApplicationContext()).insertSeason(idABC, "Season n°0" + 1, 1, StatusWatch.WATCHED, true, 10, 12, "00:50:00", null);
                    SeriesDataSource.getInstance(getApplicationContext()).insertMovie(idABC, "Movie n°01", StatusWatch.WATCHED, 2, 48, null);
                    long idMonogatari = SeriesDataSource.getInstance(getApplicationContext()).insertSerie("Monogatari Series Seconde Season", null);
                    SeriesDataSource.getInstance(getApplicationContext()).insertSeason(idMonogatari, "Season n°0" + 1, 1, StatusWatch.WATCHED, true, 1, 12, "00:24:00", null);
                }
                long end = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "" + (end - start), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.read_options_menu:
                long start2 = System.currentTimeMillis();
                SeriesDataSource.getInstance(getApplicationContext()).actualiser();
                long end2 = System.currentTimeMillis();
//                Snackbar.make(findViewById(R.id.coordinator_layout),"Réussite de lecture", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "" + (end2 - start2), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.manage_durations_options_menu:
                //initialise le Builder et l'AlertDialog pour gérer les durées d'épisodess
                final AlertDialog.Builder builder;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
//                }
                builder.setTitle(getString(R.string.manage_durations));
                builder.setNegativeButton(getString(R.string.cancel), null);
                AlertDialog dialogManageDurations = builder.create();
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.manage_durations, null);

                final RadioGroup radioGroupDurations = (RadioGroup) layout.findViewById(R.id.durations_radio_group);
                final List<Duration> durations = SeriesDataSource.getInstance(getApplicationContext()).getDureesList();

                final ArrayList<RadioButton> buttons = new ArrayList<>();
                final RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                //ajoute tous les RadioButton nécessaires
                for (final Duration duration : durations) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(duration.getDuration().toString());
                    radioButton.setId(buttons.size());
                    buttons.add(radioButton);
                    //ajoute les RadioButton au radioGroup
                    radioGroupDurations.addView(radioButton, params);
                    //choisi la sélection du radioButton s'il est par défaut
                    if (duration.isDefaut()) {
                        radioButton.toggle();
                    }
                }
                //initialise le bouton pour ajouter une durée avec un AlertDialog
                Button addDurationButton = (Button) layout.findViewById(R.id.add_duration);
                addDurationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builderAddDuration;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            builderAddDuration = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//                        } else {
                            builderAddDuration = new AlertDialog.Builder(MainActivity.this);
//                        }
                        builderAddDuration.setTitle(getString(R.string.add_duration))
                                .setNegativeButton(getString(R.string.cancel), null);
                        AlertDialog dialogAddDuration = builderAddDuration.create();
                        LayoutInflater inflaterAddDuration = dialogAddDuration.getLayoutInflater();
                        final View layoutAddDuration = inflaterAddDuration.inflate(R.layout.dialog_duration, null);
                        final TimePicker timePicker = (TimePicker) layoutAddDuration.findViewById(R.id.duration_time_picker);
                        timePicker.setIs24HourView(true);
                        timePicker.setCurrentHour(0);
                        timePicker.setCurrentMinute(0);
                        final ToggleButton durationDefault = (ToggleButton) layoutAddDuration.findViewById(R.id.default_duration_toggle_button);
                        dialogAddDuration.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = timePicker.getCurrentHour();
                                int minute = timePicker.getCurrentMinute();
                                long rowID = SeriesDataSource.getInstance(getApplicationContext()).insertDuration(hour, minute, durationDefault.isChecked());
                                if (rowID > -1) {
                                    RadioButton newRadioButton = new RadioButton(MainActivity.this);
                                    newRadioButton.setText(SeriesDataSource.hoursAndMinutesToTime(hour, minute));
                                    newRadioButton.setId(buttons.size());
                                    buttons.add(newRadioButton);
                                    radioGroupDurations.addView(newRadioButton, params);
                                    if (durationDefault.isChecked()) {
                                        radioGroupDurations.check(newRadioButton.getId());
                                    }
                                }
                            }
                        });
                        dialogAddDuration.setView(layoutAddDuration);
                        dialogAddDuration.show();
                    }
                });
                //initialise le bouton pour supprimer une durée sélectionnée par le RadioGroup
                Button deleteDuration = (Button) layout.findViewById(R.id.delete_duration);
                deleteDuration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (buttons.size() > 0) {
                            SeriesDataSource.getInstance(getApplicationContext()).deleteDuration(buttons.get(radioGroupDurations.getCheckedRadioButtonId()).getText().toString());
                            durations.remove(new Duration(0, buttons.get(radioGroupDurations.getCheckedRadioButtonId()).getText().toString(), false));
                            radioGroupDurations.removeView(buttons.get(radioGroupDurations.getCheckedRadioButtonId()));
                        }
                    }
                });
                dialogManageDurations.setView(layout);
                dialogManageDurations.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.set_default_duration), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (buttons.size() > 0 && radioGroupDurations.getCheckedRadioButtonId() > -1) {
                            SeriesDataSource.getInstance(getApplicationContext()).changeDefaultDuration(buttons.get(radioGroupDurations.getCheckedRadioButtonId()).getText().toString());
                        }
                    }
                });
                dialogManageDurations.show();
                return true;
            case R.id.statistics_options_menu:
                Intent intent = new Intent(MainActivity.this,StatisticsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(DATABASE_NAME_ARG,databaseName);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseName != null) {
            SeriesDataSource.getInstance(getApplicationContext()).open();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (databaseName != null) {
            SeriesDataSource.getInstance(getApplicationContext()).close();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE_PICKER_IMAGE_SERIE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmapForSerie = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                imageButton.setImageBitmap(bitmapForSerie);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (OutOfMemoryError e) {
//                e.printStackTrace();
                Snackbar.make(findViewById(R.id.serie_picture_image_button),"Fichier trop volumineux",Snackbar.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE_PICKER_IMAGE_SEASON && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                Bitmap bitmapForSeason = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                MyExpandableListAdapter.setBitmapSeason(bitmapForSeason);
            }
            catch (IOException e) {

            }
        }
        if (requestCode == REQUEST_CODE_PICKER_IMAGE_MOVIE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                Bitmap bitmapForMovie = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                MyExpandableListAdapter.setBitmapMovie(bitmapForMovie);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class NavigationViewItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          switch (item.getItemId()) {
              case R.id.nav_databases_create :
                  AlertDialog.Builder builder;
                  //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                  //    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                  //} else {
                      builder = new AlertDialog.Builder(MainActivity.this);
                  //}
                  builder.setTitle("Create database");
                  builder.setNegativeButton(getString(R.string.cancel),null);
                  AlertDialog createDatabaseDialog = builder.create();
                  LayoutInflater inflater = createDatabaseDialog.getLayoutInflater();
                  final View layout = inflater.inflate(R.layout.create_database,null);
				  final EditText databaseEditText = (EditText)layout.findViewById(R.id.database_edit_text);
                  createDatabaseDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.OK), new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          //EditText databaseEditText = (EditText)layout.findViewById(R.id.database_edit_text);
                          if (databaseEditText.getText().toString().length() > 0) {
                              SeriesDataSource.getInstance(getApplicationContext()).close();
                              MainActivity.databaseName = databaseEditText.getText().toString().trim() + ".db";
                              recreate();
//                          Toast.makeText(MainActivity.this, MainActivity.databaseName, Toast.LENGTH_SHORT).show();
                          }
                          else {
                              Toast.makeText(MainActivity.this, "MERDE", Toast.LENGTH_SHORT).show();
                          }
                      }
                  });
                  createDatabaseDialog.setView(layout);
                  createDatabaseDialog.show();
                  return true;
              case R.id.nav_settings :

                  return true;
              case R.id.nav_about :

                  return true;
              //if a database is selected
              default :
                  SeriesDataSource.getInstance(getApplicationContext()).close();
                  MainActivity.databaseName = mDatabasesList.get(item.getItemId());
                  recreate();
                  mPagerAdapter.init();
                  mPagerAdapter.notifyDataSetChanged();
//                  item.setChecked(true);
//                  mNavigationView.setCheckedItem(item.getItemId());
//                  mNavigationView.invalidate();
          }
          return true;
      }
  }

}