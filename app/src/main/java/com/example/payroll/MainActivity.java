package com.example.payroll;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;

import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Modals.UserListModal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import static com.example.payroll.Functions.GetTimeZone;

public class MainActivity extends AppCompatActivity {


    boolean disableBackButton;
    Button submit;
    TextView clickHere;
    Spinner spinnerTimezone;
    EditText companyName, deviceName, userCode, userPass;
    static ProgressDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        submit = findViewById(R.id.btn_main_submit);
        clickHere = findViewById(R.id.tv_main_clickherereg);
        spinnerTimezone = findViewById(R.id.spinner_main_zones);
        companyName = findViewById(R.id.et_main_company);
        deviceName = findViewById(R.id.et_main_device);
        userCode = findViewById(R.id.et_main_usercode);
        userPass = findViewById(R.id.et_main_pass);
        companyName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        submit.setBackgroundColor(Color.parseColor("#66BB66"));



        clickHere.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        submit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(companyName.getText().toString()) || TextUtils.isEmpty(deviceName.getText().toString()) || TextUtils.isEmpty(userCode.getText().toString()) || TextUtils.isEmpty(userPass.getText().toString())){
                Toast.makeText(MainActivity.this, "Fill up all the fields", Toast.LENGTH_SHORT).show();
            } else {

                if (!Functions.checkAllPermissions(MainActivity.this, MainActivity.this)) {

                    dialog = new ProgressDialog(MainActivity.this);
                    dialog.setMessage("Checking user details please wait...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    Connect runnable = new Connect(userCode.getText().toString(), companyName.getText().toString(), deviceName.getText().toString(), userPass.getText().toString(), spinnerTimezone.getSelectedItem().toString());
                    Thread myThread = new Thread(runnable);
                    myThread.start();
                } else {
                    Functions.checkAllPermissions(MainActivity.this, MainActivity.this);
                }

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(hasAllPermissionsGranted(grantResults)){
            // all permissions granted
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            // some permission are denied.
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        GetTimeZone(MainActivity.this, spinnerTimezone);
        Functions.checkAllPermissions(MainActivity.this, MainActivity.this);
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) !=   PackageManager.PERMISSION_GRANTED) {
                return;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=   PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        deviceName.setText(Build.MODEL);
        deviceName.setTextColor(Color.parseColor("#B8B8B8"));
    }

    class Connect implements Runnable{
        String userCode;
        String companyCode;
        String deviceName;
        String password;
        String timeZone;
        String inputLine;
        URL url;

        boolean returnBool;
        boolean userBoolean = false;
        public Connect(String userCode1, String companyCode1, String deviceName1, String password1, String timeZone1) {
            this.userCode = userCode1;
            this.companyCode = companyCode1;
            this.deviceName = deviceName1;
            this.password = password1;
            this.timeZone = timeZone1;
        }
        @Override
        public void run() {
            try {
                if (Functions.CheckForInternetConnection(MainActivity.this)) {
                    url = new URL("http://jvadsv.aiolosinnovativesolutions.com/?exst=" + userCode + "&cc=" + companyCode);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    inputLine = in.readLine();
                    in.close();
                    returnBool = true;
                } else {
                    returnBool = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                if (returnBool) {
                    if (inputLine.equals("CD")) {
                        userBoolean = false;
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "The company code you entered does not exist.", Toast.LENGTH_SHORT).show();
                    } else if(inputLine.equals("E")) {
                        userBoolean = true;
                    } else {
                        userBoolean = false;
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                    if (userBoolean) {
                        NewUser runnable = new NewUser(userCode, companyCode, deviceName,password, timeZone);
                        Thread myThread = new Thread(runnable);
                        myThread.start();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Cannot connect to server. Check internet connection and try again.", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
    class NewUser implements Runnable{
        UserListModal userListModal;
        String userCode;
        String password;
        String companyCode;
        String deviceName;
        String timeZone;
        String verificationStatus;
        int userExistCount = 0;
        public NewUser(String userCode1, String companyCode1, String deviceName1, String password1, String timeZone1) {
            this.userCode = userCode1;
            this.companyCode = companyCode1;
            this.deviceName = deviceName1;
            this.password = password1;
            this.timeZone = timeZone1;
        }
        @Override
        public void run() {
            verificationStatus = "NOT SET";
            try {
                userListModal  = new UserListModal(userCode);
                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                Cursor res = databaseHelper.checkNewUser(userListModal);
                if(res.getCount() > 0 ){
                    userExistCount ++;
                }
            } catch (Exception e) {
                System.err.println("Got an exception!");
                System.err.println(e.getMessage());
            }
            runOnUiThread(() -> {
                dialog.dismiss();
                if (userExistCount <= 0) {
                    try {
                        userListModal  = new UserListModal(-1, companyCode, deviceName, userCode, password, timeZone, verificationStatus);
                    } catch (Exception e) {
                        userListModal  = new UserListModal(-1, "0", "0", "0", "0", "0", verificationStatus);
                    }
                    DatabaseHelper databaseHelper;
                    databaseHelper = new DatabaseHelper(MainActivity.this);
                    databaseHelper.insertUserList(userListModal);
                    Toast.makeText(MainActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "User already exist", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click again to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}