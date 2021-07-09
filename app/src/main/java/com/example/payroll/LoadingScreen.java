package com.example.payroll;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.payroll.Helpers.DatabaseHelper;

import java.util.Objects;


public class LoadingScreen extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    String [] dataSet;
    String logType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        databaseHelper = new DatabaseHelper(this);
        Objects.requireNonNull(getSupportActionBar()).hide();


    }

    @Override
    protected void onStart() {
        super.onStart();
        try {

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::CheckUserSettings, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CheckUserSettings() {
        TestJoinMethod1 t1 = new TestJoinMethod1();
        t1.start();
        try{
            t1.join();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    class TestJoinMethod1 extends Thread {
        public void run() {
            DatabaseHelper databaseHelper;
            databaseHelper = new DatabaseHelper(LoadingScreen.this);
            dataSet =  databaseHelper.getSettings();
            logType = dataSet[0];
            runOnUiThread(() -> {
                try {
                    boolean seqVerStats = databaseHelper.secQuestionSetup(dataSet[1]);
                    String sessionUserPass = databaseHelper.getPassword(dataSet[1]);
                    String sessionUserCode = dataSet[1];
                        if (logType.equals("LOGIN")) {
                            if (seqVerStats) {
                                Intent intent = new Intent(LoadingScreen.this, UserLogsActivity.class);
                                intent.putExtra("SESSION_CODE", sessionUserCode);
                                intent.putExtra("SESSION_PASS", sessionUserPass);
                                startActivity(intent);
                                Toast.makeText(LoadingScreen.this, "Welcome!", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(LoadingScreen.this, SecuritySetupActivity.class);
                                intent.putExtra("SESSION_CODE", sessionUserCode);
                                intent.putExtra("SESSION_PASS", sessionUserPass);
                                LoadingScreen.this.startActivity(intent);
                            }
                        } else if (logType.equals("N/A")) {
                            Intent intent = new Intent(LoadingScreen.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoadingScreen.this, LoginActivity.class);
                            startActivity(intent);
                        }

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }



}
