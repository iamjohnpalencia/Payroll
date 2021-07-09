package com.example.payroll.Settings;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.LoginActivity;
import com.example.payroll.R;

public class SettingsActivity extends AppCompatActivity {

    ImageView clearDb;
    LinearLayout lnProfile, lnClearDb, lnAbout;
    String userCode, userPass;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        clearDb = findViewById(R.id.im_settings_cleardb);
        clearDb.setImageResource(R.drawable.cleardb);

        lnProfile = findViewById(R.id.ln_settings_profile);
        lnClearDb = findViewById(R.id.ln_settings_cleardb);
        lnAbout = findViewById(R.id.ln_settings_about);

        userCode = getIntent().getStringExtra("SESSION_CODE");
        userPass = getIntent().getStringExtra("SESSION_PASS");

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    DatabaseHelper databaseHelper;
                    databaseHelper = new DatabaseHelper(this);
                    databaseHelper.DropTables();
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        lnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SettingsProfileActivity.class);
            intent.putExtra("SESSION_CODE", userCode);
            intent.putExtra("SESSION_PASS", userPass);
            startActivity(intent);
        });
        lnClearDb.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to clear data?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

        });
        lnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, BottomAboutActivity.class);
            startActivity(intent);
        });
    }
}
