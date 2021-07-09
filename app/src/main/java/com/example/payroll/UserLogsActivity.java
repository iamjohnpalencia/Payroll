package com.example.payroll;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.location.Location;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Helpers.HttpDataHandler;
import com.example.payroll.Modals.FillingModal;
import com.example.payroll.Modals.UserListModal;
import com.example.payroll.Settings.SettingsActivity;
import com.example.payroll.Tabs.TabbedActivityLogs;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;


@RequiresApi(api = Build.VERSION_CODES.O)
public class UserLogsActivity extends AppCompatActivity {

    TextView tvTime, tvDate, tvLastLog, tvLastLoc;
    Button timeIn, timeOut;
    String userCode, userPass;
    String LogTypeInOut;
    static ProgressDialog dialog;
    private Double lnt, lat;
    private FusedLocationProviderClient fusedLocationClient;
    BottomNavigationView bottomNavigationView;
    LocationRequest locationRequest;
    int LOCATION_REQUEST_CODE = 10001;
    private String currentPhotoPath;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                lnt = location.getLongitude();
                lat = location.getLatitude();
//                Log.d(TAG, "onLocationResult: " + location.toString());
//                if(lat != null || lnt != null) {
//                    Functions.stopLocationUpdates(fusedLocationClient, locationCallback);
//                } else {
//                    lnt = location.getLongitude();
//                    lat = location.getLatitude();
//                    Log.d(TAG, "onLocationResult: " + location.toString());
//                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_logs);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        tvTime = findViewById(R.id.tv_login_time);
        tvDate = findViewById(R.id.tv_userlogs_date);
        timeIn = findViewById(R.id.btn_fpass_save);
        timeOut = findViewById(R.id.btn_fpass_cancel);
        tvLastLog = findViewById(R.id.tv_userlogs_lastlogin);
        tvLastLoc = findViewById(R.id.tv_userlogs_lastlocation);

        userCode = getIntent().getStringExtra("SESSION_CODE");
        userPass = getIntent().getStringExtra("SESSION_PASS");

//        System.out.println(userPass);

        updateLabels runnable = new updateLabels(userCode);
        Thread myThread = new Thread(runnable);
        myThread.start();

        getQuestions getQuestions = new getQuestions();
        Thread myThread1 = new Thread(getQuestions);
        myThread1.start();

        DisplayImages();

        timeIn.setOnClickListener(this::onClickBtnImageIn);
        timeOut.setOnClickListener(this::onClickBtnImageOut);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        Functions.checkAllPermissions(UserLogsActivity.this, UserLogsActivity.this);
        Functions.CountDownRunner runnable = new Functions.CountDownRunner(UserLogsActivity.this, tvTime, tvDate);
        Thread myThread = new Thread(runnable);
        myThread.start();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Functions.checkSettingsAndStartLocationUpdates(locationRequest, this, this, fusedLocationClient, locationCallback);
        } else {
            Functions.askLocationPermission(this, this , LOCATION_REQUEST_CODE);
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
    boolean connected;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClickBtnImageIn(View v) {

        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String fileName = "TimeIn";
        LogTypeInOut = "IN";
        if (!Functions.checkAllPermissions(UserLogsActivity.this, UserLogsActivity.this)) {

            getConnected getConnected = new getConnected();
            Thread myThread = new Thread(getConnected);
            myThread.start();

            if(Functions.statusCheck(this, this, fusedLocationClient, locationRequest, locationCallback)){
                if (getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            try {
                                if (connected) {
                                    lat = location.getLatitude();
                                    lnt = location.getLongitude();
                                } else {
                                    getUsersLastLocation getUsersLastLocation = new getUsersLastLocation();
                                    Thread myThread1 = new Thread(getUsersLastLocation);
                                    myThread1.start();
                                }
                                File imageFile = File.createTempFile(fileName, "jpg", storageDirectory);
                                currentPhotoPath = imageFile.getAbsolutePath();
                                Uri imageUri = FileProvider.getUriForFile(UserLogsActivity.this, "com.example.payroll.fileProvider", imageFile);
                                openSomeActivityForResult(imageUri);
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Functions.checkSettingsAndStartLocationUpdates(locationRequest, this, this, fusedLocationClient, locationCallback);
                            } else {
                                Functions.askLocationPermission(this, this , LOCATION_REQUEST_CODE);
                            }
                        }
                    });
                } else {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA}, 1);
                }
            }
        } else {
            Functions.checkAllPermissions(UserLogsActivity.this, UserLogsActivity.this);
        }
    }

    class getConnected implements Runnable{
        @Override
        public void run() {
            connected = Functions.CheckForInternetConnection(UserLogsActivity.this);
        }
    }
    class getUsersLastLocation implements Runnable {

        String [] userLoc;
        @Override
        public void run() {
            DatabaseHelper databaseHelper;
            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            userLoc = databaseHelper.getUserLastLoc();
            lnt = Double.parseDouble(userLoc[0]);
            lat = Double.parseDouble(userLoc[1]);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClickBtnImageOut(View v) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String fileName = "timeout";
        LogTypeInOut = "OUT";
        if (!Functions.checkAllPermissions(UserLogsActivity.this, UserLogsActivity.this)) {

            getConnected getConnected = new getConnected();
            Thread myThread = new Thread(getConnected);
            myThread.start();

            if(Functions.statusCheck(this, this, fusedLocationClient, locationRequest, locationCallback)){
                if (getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            try {
                                if (connected) {
                                    lat = location.getLatitude();
                                    lnt = location.getLongitude();
                                } else {
                                    getUsersLastLocation getUsersLastLocation = new getUsersLastLocation();
                                    Thread myThread1 = new Thread(getUsersLastLocation);
                                    myThread1.start();
                                }
                                File imageFile = File.createTempFile(fileName, "jpg", storageDirectory);
                                currentPhotoPath = imageFile.getAbsolutePath();
                                Uri imageUri = FileProvider.getUriForFile(UserLogsActivity.this, "com.example.payroll.fileProvider", imageFile);
                                openSomeActivityForResult(imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Functions.checkSettingsAndStartLocationUpdates(locationRequest, this, this, fusedLocationClient, locationCallback);
                            } else {
                                Functions.askLocationPermission(this, this , LOCATION_REQUEST_CODE);
                            }
                        }
                    });
                } else {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA}, 1);
                }
            }
        } else {
            Functions.checkAllPermissions(UserLogsActivity.this, UserLogsActivity.this);
        }
    }

    public void openSomeActivityForResult(Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        someActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    doSomeOperations();
                }
            });

    private void doSomeOperations() {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String strLat = String.valueOf(lat);
        String strLnt = String.valueOf(lnt);
        String userCode = this.userCode;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        Bitmap convImage = getResizedBitmap(bitmap, 500);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        convImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byte1 = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byte1, Base64.DEFAULT);
        dialog = new ProgressDialog(UserLogsActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        if (!UserLogsActivity.this.isFinishing()){
            dialog.show();
        }
        Functions.removeDirectory(storageDirectory);
        GetFullAddress(strLnt,strLat,userCode,encoded,LogTypeInOut);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void GetFullAddress(String lng, String lat, String userCode, String encoded, String logType) {
        LocateUser t1 = new LocateUser(lng,lat,userCode,encoded,logType);
        t1.start();
        try{
            t1.join();
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    class LocateUser extends Thread {
        String[] userData;
        String[] userDetails;
        public LocateUser(String lng, String lat, String userCode, String encoded, String logType) {
            userDetails = new String[]{lng, lat, userCode, encoded, logType};
        }
        public void run() {
            userData = new String[]{userDetails[0], userDetails[1], userDetails[2], userDetails[3], userDetails[4]};

            String response;
            boolean hasInternetCon;

            if (connected) {
                HttpDataHandler http = new HttpDataHandler();
                String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +userDetails[1]+ "," +userDetails[0]+ "&key=AIzaSyAZixCNiNBxTfwU02uqckYXdV92trbx86Y";
                System.out.println(url);
                response = http.GetHTTPData(url,UserLogsActivity.this);
                hasInternetCon = true;
            } else {
                hasInternetCon = false;
                response = tvLastLoc.getText().toString().replace("Last Address: ","");
            }

            String finalResponse = response;
            boolean finalHasInternetCon = hasInternetCon;
            runOnUiThread(() -> {
                System.out.println(hasInternetCon);
                try {
                    JSONObject jsonObject;
                    String address;
                    if (finalHasInternetCon) {
                        jsonObject = new JSONObject(finalResponse);
                        address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                    } else {
                        address = finalResponse;
                    }

                    GetUserDetailsThread(userData[0],userData[1],userData[2],userData[3],userData[4],address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        }
    }

    private void GetUserDetailsThread(String longitude, String latitude, String userCode, String image, String logtype, String address) {

        UserLogsActivity.GetUserDetails t1 = new UserLogsActivity.GetUserDetails(longitude,latitude,userCode,image,logtype,address );
        t1.start();
        try{
            t1.join();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    class GetUserDetails extends Thread {
        String[] userData;
        UserListModal userListModal;
        String stUserCode;
        String stLong;
        String stLat;
        String stImage;
        String stLog;
        String stAddress;
        Calendar calendar = Calendar.getInstance();
        String strTime;
        String getTimeDate;

        public GetUserDetails(String longitude, String latitude, String userCode, String image, String logtype, String address) {
            stLong = longitude;
            stLat = latitude;
            stUserCode = userCode;
            stImage = image;
            stLog = logtype;
            stAddress = address;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {
            if (connected) {
                HttpDataHandler http = new HttpDataHandler();
                getTimeDate = "http://jvadsv.aiolosinnovativesolutions.com/getdatetime.php";
                strTime = "LIVE: " + http.GetHTTPData(getTimeDate, UserLogsActivity.this);
            } else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
                strTime = "OFF: " +  timeFormat.format(calendar.getTime());
            }

            userListModal  = new UserListModal(stUserCode);
            DatabaseHelper databaseHelper;
            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            userData = databaseHelper.getUserDetails(userListModal);

            userListModal = new UserListModal( -1, userData[0],userData[1],stUserCode,stAddress,stLog,strTime,stLong,stLat,stImage);
            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            databaseHelper.insertUserLogs(userListModal);

            runOnUiThread(() -> {
                DisplayImages();
                updateLabels runnable = new updateLabels(userCode);
                Thread myThread = new Thread(runnable);
                myThread.start();
                dialog.dismiss();
            });
        }
    }



    private void DisplayImages() {
        DisplayImageThread t1 = new DisplayImageThread();
        t1.start();
        try{
            t1.join();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    class DisplayImageThread extends Thread {
        @Override
        public void run() {
            super.run();
            DatabaseHelper databaseHelper;
            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            JSONObject jsonObject;
            JSONArray jsonArray = null;
            try {
                jsonObject = databaseHelper.retrieveJsonImages(userCode);
                jsonArray = jsonObject.getJSONArray("MyArray");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LinearLayout gallery = UserLogsActivity.this.findViewById(R.id.gallery);
            LayoutInflater inflater = LayoutInflater.from(UserLogsActivity.this);
            JSONArray finalJsonArray = jsonArray;
            runOnUiThread(() -> {
                gallery.removeAllViews();
                for (int i = 0; i < finalJsonArray.length(); i++) {
                    JSONObject obj;
                    try {
                        obj = finalJsonArray.getJSONObject(i);
                        View view = inflater.inflate(R.layout.multipleimageview, gallery, false);
                        ImageView imageView = view.findViewById(R.id.im_multiple_image);
                        TextView textLog = view.findViewById(R.id.tv_multiple_text);
                        String A = obj.getString("image");
                        String B = obj.getString("logtype");
                        byte[] decodedString = Base64.decode(A, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageView.setImageBitmap(decodedByte);
                        textLog.setText(B);
                        gallery.addView(view);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private void loginUser(String userCode, String latT, String lonGi) {
        LoginUserThread t1 = new LoginUserThread(userCode, latT, lonGi);
        t1.start();
        try{
            t1.join();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    class LoginUserThread extends Thread {
        UserListModal userListModal;
        String strUserCode;
        String strLat;
        String strLnt;
        String fullDateFormat;
        boolean userExist = false;
        int userCount = 0;
        public LoginUserThread(String userCode, String ltt, String lng) {
            strUserCode =  userCode ;
            strLat = ltt;
            strLnt = lng;
        }

        public void run() {
            String [] userData;
            TextView tv_login_time =  findViewById(R.id.tv_login_time);
            TextView tv_login_date =  findViewById(R.id.tv_userlogs_date);
            fullDateFormat = tv_login_time.getText().toString() + " - " + tv_login_date.getText().toString();
            userListModal  = new UserListModal(strUserCode);
            DatabaseHelper databaseHelper;
            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            userData = databaseHelper.GetUserDetails(userListModal, fullDateFormat, strLat, strLnt);

            for (String a: userData) {
                if (a!=null) userCount++;
            }

            runOnUiThread(() -> {
                userExist = userCount > 0;
                try {
                    if (userExist) {
                         GetFullAddress1(userData[0],userData[1],userData[2],userData[3],userData[4],userData[5],userData[6]);
                    } else {
                        Toast.makeText(UserLogsActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private void GetFullAddress1(String companyCode, String deviceName, String userCode, String logtype, String fullTime , String lat, String lnt) {
        GetFullAddressThread getFullAddressThread = new GetFullAddressThread(companyCode, deviceName, userCode, logtype, fullTime, lat, lnt);
        getFullAddressThread.start();
        try {
            getFullAddressThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class GetFullAddressThread extends Thread {
        String[] userDetails = {};
        String strCompanyCode;
        String strDeviceName;
        String strUserCode;
        String strLogType;
        String strFUllDate;
        String strLat;
        String strLng;
        String response;
        boolean hasInternetCon = false;
        public GetFullAddressThread ( String companyCode, String deviceName, String userCode, String logtype, String fullTime , String lat, String lnt) {
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
            super.run();
            userDetails = new String[]{strCompanyCode, strDeviceName, strUserCode, strLogType, strFUllDate, strLat, strLng};

            try {
                if(Functions.CheckForInternetConnection(UserLogsActivity.this)){
                    hasInternetCon = true;
                    HttpDataHandler http = new HttpDataHandler();
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + strLat + "," + strLng + "&key=AIzaSyAZixCNiNBxTfwU02uqckYXdV92trbx86Y";
                    response = http.GetHTTPData(url, UserLogsActivity.this);
                } else {
                    hasInternetCon = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            runOnUiThread(() -> {
                if (hasInternetCon) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        String address = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                        UpdateUserSettings(userDetails[0],userDetails[1],userDetails[2],userDetails[3],userDetails[4],userDetails[5],userDetails[6],address);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    response = tvLastLoc.getText().toString().replace("Last Address: ","");
                    UpdateUserSettings(userDetails[0],userDetails[1],userDetails[2],userDetails[3],userDetails[4],userDetails[5],userDetails[6],response);
                }
            });
        }
    }
    private void UpdateUserSettings(String companyCode, String deviceName, String userCode, String logtype, String fullTime, String lat, String lnt, String fullAddress) {
        UpdateUserSettingsThread t1 = new UpdateUserSettingsThread(companyCode, deviceName, userCode, logtype, fullTime, lat, lnt, fullAddress);
        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class UpdateUserSettingsThread extends Thread{
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

        public UpdateUserSettingsThread(String companyCode, String deviceName, String userCode, String logtype, String fullTime , String lat, String lnt, String fullAddress) {
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
            super.run();
            userListModal = new UserListModal(strUserCode);
            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            databaseHelper.updateUserSettings(userListModal, "1", true);
            userListModal = new UserListModal( -1, strCompanyName,strDeviceName,strUserCode,strFullAddress,strLogType,strLogDesc,strLng,strLat, "N/A");
            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            databaseHelper.insertUserLogs(userListModal);
            runOnUiThread(() -> {
                dialog.dismiss();
                Intent intent = new Intent(UserLogsActivity.this, LoginActivity.class);
                UserLogsActivity.this.startActivity(intent);
                finish();
            });
        }
    }
    //BOTTOM NAV

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        int id = item.getItemId();
        if (id == R.id.navigation_settings) {
            Intent intent = new Intent(UserLogsActivity.this, SettingsActivity.class);
            intent.putExtra("SESSION_CODE", userCode);
            intent.putExtra("SESSION_PASS", userPass);
            startActivity(intent);
            return true;
        } else if (id == R.id.navigation_logs) {
//            Intent intent = new Intent(UserLogsActivity.this, BottomLogsActivity.class);
            Intent intent = new Intent(UserLogsActivity.this, TabbedActivityLogs.class);
            intent.putExtra("SESSION_CODE", userCode);
            startActivity(intent);
            return true;
        } else if (id == R.id.navigation_sync) {
            dialog = new ProgressDialog(UserLogsActivity.this);
            dialog.setMessage("Syncing please do not close application until we finish operation.");
            dialog.setCanceledOnTouchOutside(false);
            if (!UserLogsActivity.this.isFinishing()) {
                dialog.show();
            }
            GetJson runnable = new GetJson();
            Thread myThread = new Thread(runnable);
            myThread.start();

            try {
                myThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        } else if (id == R.id.navigation_filling) {
            Intent intent = new Intent(UserLogsActivity.this, FillingsActivity.class);
            intent.putExtra("SESSION_CODE", userCode);
            startActivity(intent);
            return true;
        } else if (id == R.id.navigation_logout) {
            LogOut();
            return true;
        } else {
            return false;
        }
    };


    public void LogOut() {
        if (!Functions.checkAllPermissions(UserLogsActivity.this, UserLogsActivity.this)) {
            if(Functions.statusCheck(this, this, fusedLocationClient, locationRequest, locationCallback)){
                if (getApplicationContext().checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lnt = location.getLongitude();
                            dialog = new ProgressDialog(UserLogsActivity.this);
                            dialog.setMessage("Logging out. Please wait.");
                            dialog.setCanceledOnTouchOutside(false);
                            if (!UserLogsActivity.this.isFinishing()) {
                                dialog.show();
                            }
                            String lats = Double.toString(lat);
                            String lngs = Double.toString(lnt);
                            loginUser(userCode, lats, lngs);
                        } else {
                            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Functions.checkSettingsAndStartLocationUpdates(locationRequest, this, this, fusedLocationClient, locationCallback);
                            } else {
                                Functions.askLocationPermission(this, this , LOCATION_REQUEST_CODE);
                            }
                        }
                    });
                } else {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA}, 1);
                }
            }
        } else {
            Functions.checkAllPermissions(UserLogsActivity.this, UserLogsActivity.this);
        }
    }

    class GetJson implements Runnable {
        JSONObject jsonObject;
        JSONArray jsonArray;

        boolean returnThis;

        @Override
        public void run() {
            try {

                if (Functions.CheckForInternetConnection(UserLogsActivity.this)) {
                    DatabaseHelper databaseHelper;
                    databaseHelper = new DatabaseHelper(UserLogsActivity.this);
                    jsonObject = databaseHelper.SyncUserData(userCode);
                    jsonArray = jsonObject.getJSONArray("MyUserData");
                    returnThis = true;
                } else {
                    returnThis = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {

                if (returnThis) {
                    SyncToCloud runnable = new SyncToCloud(jsonArray,jsonObject);
                    Thread myThread = new Thread(runnable);
                    myThread.start();
                } else {
                    dialog.dismiss();
                    Toast.makeText(UserLogsActivity.this, "Cannot connect to server. Check internet connection and try again.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    class SyncToCloud implements Runnable {
        JSONArray getJsonArray;
        JSONObject getJsonObject;

        public SyncToCloud (JSONArray jsonArray, JSONObject jsonObject) {
            this.getJsonArray = jsonArray;
            this.getJsonObject = jsonObject;
        }

        @Override
        public void run() {

            for (int i = 0; i < getJsonArray.length(); i++) {
                JSONObject objects = getJsonArray.optJSONObject(i);
                try {

                    URL url = new URL("http://jvadsv.aiolosinnovativesolutions.com/inserttologs.php");
                    URLConnection con = url.openConnection();
                    HttpURLConnection http = (HttpURLConnection)con;
                    http.setRequestMethod("POST"); // PUT is another valid option
                    http.setDoOutput(true);

                    String id =  (String) objects.get("id");
                    String image = (String) objects.get("image");

                    Map<String,String> arguments = new HashMap<>();
                    arguments.put("id", id);
                    arguments.put("company", (String) objects.get("company"));
                    arguments.put("device", (String) objects.get("device"));
                    arguments.put("user", (String) objects.get("user"));
                    arguments.put("address", (String) objects.get("address"));
                    arguments.put("type", (String) objects.get("type"));
                    arguments.put("desc", (String) objects.get("desc"));
                    arguments.put("longitude", (String) objects.get("longitude"));
                    arguments.put("latitude", (String) objects.get("latitude"));
                    arguments.put("image", image);
                    arguments.put("date", (String) objects.get("date"));

                    StringJoiner sj = new StringJoiner("&");
                    for(Map.Entry<String,String> entry : arguments.entrySet())
                        sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                                + URLEncoder.encode(entry.getValue(), "UTF-8"));
                    byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                    int length = out.length;
                    http.setFixedLengthStreamingMode(length);
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    http.connect();

                    try(OutputStream os = http.getOutputStream()) {
                        os.write(out);
                    }

                    try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {

                DatabaseHelper databaseHelper;
                databaseHelper = new DatabaseHelper(UserLogsActivity.this);

                for (int i = 0; i < getJsonArray.length(); i++) {
                    JSONObject objects = getJsonArray.optJSONObject(i);
                    try {
                        String id = (String) objects.get("id");
                        databaseHelper.UpdateLogsLocal(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                GetJsonFilling getJsonFilling = new GetJsonFilling();
                Thread myThread = new Thread(getJsonFilling);
                myThread.start();

            });
        }
    }

    class GetJsonFilling implements Runnable {
        JSONObject jsonObject;
        JSONArray jsonArray;

        boolean returnThis;

        @Override
        public void run() {
            try {

                if (Functions.CheckForInternetConnection(UserLogsActivity.this)) {
                    DatabaseHelper databaseHelper;
                    databaseHelper = new DatabaseHelper(UserLogsActivity.this);
                    jsonObject = databaseHelper.SyncUserDataFilling(userCode);
                    jsonArray = jsonObject.getJSONArray("MyUserData");
                    returnThis = true;
                } else {
                    returnThis = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {

                if (returnThis) {
                    SyncToCloudFilling runnable = new SyncToCloudFilling(jsonArray,jsonObject);
                    Thread myThread = new Thread(runnable);
                    myThread.start();
                } else {
                    dialog.dismiss();
                    Toast.makeText(UserLogsActivity.this, "Cannot connect to server. Check internet connection and try again.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    class SyncToCloudFilling implements Runnable {
        JSONArray getJsonArray;
        JSONObject getJsonObject;

        public SyncToCloudFilling (JSONArray jsonArray, JSONObject jsonObject) {
            this.getJsonArray = jsonArray;
            this.getJsonObject = jsonObject;
        }

        @Override
        public void run() {

            for (int i = 0; i < getJsonArray.length(); i++) {
                JSONObject objects = getJsonArray.optJSONObject(i);
                try {

                    URL url = new URL("http://jvadsv.aiolosinnovativesolutions.com/inserttofilling.php");
                    URLConnection con = url.openConnection();
                    HttpURLConnection http = (HttpURLConnection)con;
                    http.setRequestMethod("POST"); // PUT is another valid option
                    http.setDoOutput(true);
                    Map<String,String> arguments = new HashMap<>();
                    arguments.put("flaID", (String) objects.get("flaID"));
                    arguments.put("flaType", (String) objects.get("flaType"));
                    arguments.put("flaFromDate", (String) objects.get("flaFromDate"));
                    arguments.put("flaToDate", (String) objects.get("flaToDate"));
                    arguments.put("flaReason", (String) objects.get("flaReason"));
                    arguments.put("flaRemarks", (String) objects.get("flaRemarks"));
                    arguments.put("flaUser", (String) objects.get("flaUser"));
                    arguments.put("flaStatus", (String) objects.get("flaStatus"));
                    arguments.put("flaRegDate", (String) objects.get("flaRegDate"));

                    StringJoiner sj = new StringJoiner("&");
                    for(Map.Entry<String,String> entry : arguments.entrySet())
                        sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                                + URLEncoder.encode(entry.getValue(), "UTF-8"));
                    byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                    int length = out.length;
                    http.setFixedLengthStreamingMode(length);
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    http.connect();

                    try(OutputStream os = http.getOutputStream()) {
                        os.write(out);
                    }

                    try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                dialog.dismiss();
                DatabaseHelper databaseHelper;
                databaseHelper = new DatabaseHelper(UserLogsActivity.this);

                for (int i = 0; i < getJsonArray.length(); i++) {
                    JSONObject objects = getJsonArray.optJSONObject(i);
                    try {
                        String id = (String) objects.get("flaID");
                        databaseHelper.UpdateFillingLocal(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(UserLogsActivity.this, "Complete", Toast.LENGTH_SHORT).show();

            });
        }
    }
    class getQuestions implements Runnable {

        boolean HasInternet = false;
        @Override
        public void run() {
            String url = "http://jvadsv.aiolosinnovativesolutions.com/fetchdata.php?usercode=" + userCode + "&tofetch=filling";
            HasInternet = Functions.CheckForInternetConnection(UserLogsActivity.this);
            if (HasInternet) {
                StringRequest request = new StringRequest(url, this::parseJsonData, error -> Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show());
                request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue rQueue = Volley.newRequestQueue(UserLogsActivity.this);
                rQueue.add(request);
            }
        }

        private void parseJsonData(String s) {
            try {

                FillingModal fillingModal;
                DatabaseHelper databaseHelper;
                JSONObject object = new JSONObject(s);
                JSONArray fruitsArray = object.getJSONArray("fla");

                for(int i = 0; i < fruitsArray.length(); ++i) {
                    JSONObject obj = fruitsArray.getJSONObject(i);
                    String type = obj.getString("Type");
                    String from  = obj.getString("From");
                    String to  = obj.getString("To");
                    String reason  = obj.getString("Reason");
                    String remarks  = obj.getString("Remarks");
                    String usercode  = obj.getString("Usercode");
                    String stats  = obj.getString("Status");
                    String regdate = obj.getString("LocalRegDate");

                    fillingModal = new FillingModal(type,from,to,reason,remarks,usercode,stats);
                    databaseHelper = new DatabaseHelper(UserLogsActivity.this);
                    databaseHelper.FetchData(fillingModal, regdate);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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

            databaseHelper = new DatabaseHelper(UserLogsActivity.this);
            userData = databaseHelper.GetLastLogs(getUserCode);

            lstAdd = "Last Address: " + userData[0];
            lstAct = "Last Action: (" + userData[2] + ") "+ userData[1];

            runOnUiThread(() -> {

                lastAddress = findViewById(R.id.tv_userlogs_lastlocation);
                lastAction = findViewById(R.id.tv_userlogs_lastlogin);
                lastAddress.setText(lstAdd);
                lastAction.setText(lstAct);

            });
        }
    }
}
