package com.mehnaz.dailyshoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.home_toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle("Daily Shopping List");
        fab_button = findViewById(R.id.fab);

        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
    }

    private void customDialog() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data,null);

        AlertDialog dialog = mydialog.create();

        dialog.setView(myview);
        EditText type = myview.findViewById(R.id.etType);
        EditText amount =myview.findViewById(R.id.etAmount);
        EditText note =myview.findViewById(R.id.etNote);
        Button save =myview.findViewById(R.id.btnSave);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mType= type.getText().toString().trim();
                String mAmount= amount.getText().toString().trim();
                String mNote= note.getText().toString().trim();

                if(TextUtils.isEmpty(mType)){
                    type.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(mAmount)){
                    amount.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(mNote)){
                    note.setError("Required Field...");
                    return;
                }


            }
        });

        dialog.show();
    }
}