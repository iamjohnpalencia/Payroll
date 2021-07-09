package com.example.payroll;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Helpers.HttpDataHandler;
import com.example.payroll.Modals.UserListModal;
import com.example.payroll.Settings.ForgotPasswordActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {

    EditText userCode, userPass;
    Button btnLogin;
    TextView tvRegister, tvTime, tvDate, lastLogin, lastLocation ,fpass;
    private Double lnt, lat;
    static ProgressDialog dialog;
    private FusedLocationProviderClient fusedLocationClient;

//    private static final String TAG = "MainActivity";
    LocationRequest locationRequest;
    int LOCATION_REQUEST_CODE = 10001;



    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                lnt = location.getLongitude();
                lat = location.getLatitude();
//                Log.d(TAG, "onLocationResult: " + location.toString());
//                if(lat != null || lnt != null) {
//                   Functions.stopLocationUpdates(fusedLocationClient, locationCallback);
//                } else {
//                    lnt = location.getLongitude();
//                    lat = location.getLatitude();
//                    Log.d(TAG, "onLocationResult: " + location.toString());
//                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        userCode = findViewById(R.id.et_login_usercode);
        userPass = findViewById(R.id.et_login_pass);
        btnLogin = findViewById(R.id.btn_login_submit);
        tvRegister = findViewById(R.id.tv_login_register);
        tvTime = findViewById(R.id.tv_login_time);
        tvDate = findViewById(R.id.tv_login_date);
        lastLogin = findViewById(R.id.tv_login_lastlogin);
        btnLogin.setBackgroundColor(Color.parseColor("#66BB66"));


        fpass = findViewById(R.id.tv_login_forgotpass);

        lastLocation = findViewById(R.id.tv_login_location);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
        fpass.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            if (isEmpty(userCode.getText().toString()) || isEmpty(userPass.getText().toString())) {
                Toast.makeText(LoginActivity.this, "Fill up all the fields", Toast.LENGTH_SHORT).show();
            } else {



                if (!Functions.checkAllPermissions(LoginActivity.this, LoginActivity.this)) {
                    if (Functions.statusCheck(this, this, fusedLocationClient, locationRequest, locationCallback)) {
                        if (getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    lat = location.getLatitude();
                                    lnt = location.getLongitude();
                                    dialog = new ProgressDialog(LoginActivity.this  );
                                    dialog.setMessage("Checking user credentials.");
                                    dialog.setCanceledOnTouchOutside(false);
                                    if (!LoginActivity.this.isFinishing()){
                                        dialog.show();
                                    }
                                    LoginUser runnable = new LoginUser(userCode.getText().toString(), userPass.getText().toString(), lat.toString(), lnt.toString());
                                    Thread myThread = new Thread(runnable);
                                    myThread.start();

                                } else {
                                    Toast.makeText(LoginActivity.this, "Cannot find location. Please try again or try updating your google play service.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);
                        }
                    }
                } else {
                    Functions.checkAllPermissions(LoginActivity.this, LoginActivity.this);
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        Functions.checkAllPermissions(LoginActivity.this, LoginActivity.this);
        Functions.CountDownRunner runnable = new Functions.CountDownRunner(LoginActivity.this, tvTime, tvDate);
        Thread myThread = new Thread(runnable);
        myThread.start();

        updateLabels runnable1 = new updateLabels("");
        Thread myThread1 = new Thread(runnable1);
        myThread1.start();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           Functions.checkSettingsAndStartLocationUpdates(locationRequest, this, this, fusedLocationClient, locationCallback);
        } else {
           Functions.askLocationPermission(this, this, LOCATION_REQUEST_CODE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
       Functions.stopLocationUpdates(fusedLocationClient, locationCallback);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(hasAllPermissionsGranted(grantResults)){
            // all permissions granted
            Functions.checkSettingsAndStartLocationUpdates(locationRequest, this, this, fusedLocationClient, locationCallback);
//            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            //MAY BUG
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

    class LoginUser implements Runnable {
        UserListModal userListModal;
        String strUserCode;
        String strUserPass;
        String strLat;
        String strLnt;
        String fullDateFormat;
        boolean userExist = false;
        int userCount = 0;
        public LoginUser(String userCode, String userPass, String ltt, String lng) {
            strUserCode =  userCode ;
            strUserPass =  userPass;
            strLat = ltt;
            strLnt = lng;
        }
        public void run() {
            String [] userData;
            TextView tv_login_time =  LoginActivity.this.findViewById(R.id.tv_login_time);
            TextView tv_login_date =  LoginActivity.this.findViewById(R.id.tv_login_date);

            fullDateFormat = tv_login_time.getText().toString() + " - " + tv_login_date.getText().toString();
            userListModal  = new UserListModal(strUserCode, strUserPass);
            DatabaseHelper databaseHelper;
            databaseHelper = new DatabaseHelper(LoginActivity.this);
            userData = databaseHelper.checkUserExsit(userListModal, fullDateFormat, strLat, strLnt);

            for (String a: userData) {
                if (a!=null) userCount++;
            }

            runOnUiThread(() -> {

                userExist = userCount > 0;
                try {
                    if (userExist) {
                        GetFullAddress runnable = new GetFullAddress(userData[0],userData[1],userData[2],userData[3],userData[4],userData[5],userData[6]);
                        Thread myThread = new Thread(runnable);
                        myThread.start();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
    }

    class GetFullAddress implements Runnable {
        String[] userDetails = {};
        String strCompanyCode;
        String strDeviceName;
        String strUserCode;
        String strLogType;
        String strFUllDate;
        String strLat;
        String strLng;
        String response;

        boolean returnBool;

        public GetFullAddress ( String companyCode, String deviceName, String userCode, String logtype, String fullTime , String lat, String lnt) {
            strCompanyCode = companyCode;
            strDeviceName = deviceName;
            strUserCode =  userCode;
            strLogType =  logtype;
            strFUllDate =  fullTime;
            strLat = lat;
            strLng = lnt;
        }
        @Override
        public void run() {
            boolean hasInternet = Functions.CheckForInternetConnection(LoginActivity.this);
            if (hasInternet) {
                userDetails = new String[]{strCompanyCode, strDeviceName, strUserCode, strLogType, strFUllDate, strLat, strLng};
                try {
                    HttpDataHandler http = new HttpDataHandler();
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + strLat +"," + strLng +"&key=AIzaSyAZixCNiNBxTfwU02uqckYXdV92trbx86Y";
                    response = http.GetHTTPData(url, LoginActivity.this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                returnBool = true;
            } else {
                returnBool = false;
            }


            runOnUiThread(() -> {
                System.out.println(hasInternet);
                if (returnBool) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        String address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                        UpdateUserSettings runnable = new UpdateUserSettings(userDetails[0],userDetails[1],userDetails[2],userDetails[3],userDetails[4],userDetails[5],userDetails[6],address);
                        Thread myThread = new Thread(runnable);
                        myThread.start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Cannot connect to server. Check internet connection and try again.", Toast.LENGTH_SHORT).show();
                }
                if(dialog.isShowing())
                    dialog.dismiss();
            });
        }
    }

    class UpdateUserSettings implements Runnable{
        UserListModal userListModal;
        DatabaseHelper databaseHelper;
        String strCompanyName;
        String strDeviceName;
        String strUserCode;
        String strFullAddress;
        String strLogType;
        String strLogDesc;
        String strLat;
        String strLng;
        boolean seqVerStats;
        public UpdateUserSettings(String companyCode, String deviceName, String userCode, String logtype, String fullTime , String lat, String lnt, String fullAddress) {
            strCompanyName = companyCode;
            strDeviceName = deviceName;
            strUserCode = userCode;
            strLogType = logtype;
            strLogDesc = fullTime;
            strLat = lat;
            strLng = lnt;
            strFullAddress = fullAddress;
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            userListModal = new UserListModal(strUserCode);
            databaseHelper = new DatabaseHelper(LoginActivity.this);
            databaseHelper.updateUserSettings(userListModal, "1", false);
            userListModal = new UserListModal( -1, strCompanyName,strDeviceName,strUserCode,strFullAddress,strLogType,strLogDesc,strLng,strLat, "N/A");
            databaseHelper = new DatabaseHelper(LoginActivity.this);
            databaseHelper.insertUserLogs(userListModal);
            runOnUiThread(() -> {
                dialog.dismiss();
                seqVerStats = databaseHelper.secQuestionSetup(strUserCode);
                String sessionUserCode = userCode.getText().toString();
                String sessionUserPass = userPass.getText().toString();
                if (seqVerStats) {
                    Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, UserLogsActivity.class);
                    intent.putExtra("SESSION_CODE", sessionUserCode);
                    intent.putExtra("SESSION_PASS", sessionUserPass);
                    LoginActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, SecuritySetupActivity.class);
                    intent.putExtra("SESSION_CODE", sessionUserCode);
                    intent.putExtra("SESSION_PASS", sessionUserPass);
                    LoginActivity.this.startActivity(intent);
                }
                finish();
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

    class updateLabels implements Runnable {
        TextView lastAction;
        TextView lastAddress;
        String[] userData;
        DatabaseHelper databaseHelper;

        String lstAct;
        String lstAdd;
        String getUserCode;

        public updateLabels(String userCode) {
            this.getUserCode = userCode;
        }
        @Override
        public void run() {

            databaseHelper = new DatabaseHelper(LoginActivity.this);

            userData = databaseHelper.GetLastLogs(getUserCode);

            lstAdd = "Last Address: " + userData[0];
            lstAct = "Last Action: (" + userData[2] + ") "+ userData[1];

            runOnUiThread(() -> {

                lastAddress = findViewById(R.id.tv_login_location);
                lastAction = findViewById(R.id.tv_login_lastlogin);
                lastAddress.setText(lstAdd);
                lastAction.setText(lstAct);

            });
        }
    }

}
