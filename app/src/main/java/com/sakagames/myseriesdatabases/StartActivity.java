package com.sakagames.myseriesdatabases;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        String[] databases = getApplicationContext().databaseList();

        if (databases.length == 0) {
            AlertDialog.Builder builder;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder = new AlertDialog.Builder(StartActivity.this,android.R.style.Theme_Material_Dialog_Alert);
//            }
//            else {
                builder = new AlertDialog.Builder(StartActivity.this);
            //}
            builder.setTitle(getString(R.string.no_database_found));
            builder.setCancelable(false);
            final AlertDialog databaseDialog = builder.create();
            View layout = databaseDialog.getLayoutInflater().inflate(R.layout.create_database,null);
            final EditText databaseNameEditText = (EditText)layout.findViewById(R.id.database_edit_text);
            databaseDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            databaseDialog.setView(layout);
            databaseDialog.show();
            databaseDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (databaseNameEditText.getText().toString().trim().length() > 0) {
                        MainActivity.databaseName = databaseNameEditText.getText().toString().trim() + ".db";
                        databaseDialog.dismiss();
                        Intent intent = new Intent(StartActivity.this,MainActivity.class);
                        startActivityForResult(intent,0);
                        finish();
                    }
                    else {
                        Toast.makeText(StartActivity.this, "Field is required", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            List<String> databasesList = new ArrayList<>();
            for (String database : databases) {
                if (database.endsWith(".db")) {
                    databasesList.add(database);
                }
            }
            Collections.sort(databasesList);
            MainActivity.databaseName = databasesList.get(0);
            Intent intent = new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
