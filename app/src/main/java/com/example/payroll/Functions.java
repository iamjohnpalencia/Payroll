package com.example.payroll;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class Functions {


    public static ArrayAdapter<String> spinnerGlobalArray;


    public static void GetTimeZone (Context context, Spinner spinner) {
        int mSelectedIndex = 0;
        try {
            List<String> timezones7 = fetchTimeZones7(Functions.OffsetType.UTC);
            Collections.sort(timezones7);


            ArrayAdapter adapter= new ArrayAdapter(context,android.R.layout.simple_spinner_item,timezones7) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Cast the spinner collapsed item (non-popup item) as a text view
                    TextView tv = (TextView) super.getView(position, convertView, parent);
                    // Set the text color of spinner item
                    tv.setTextColor(Color.WHITE);
                    // Return the view
                    return tv;
                }
                @Override
                public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent){
                    // Cast the drop down items (popup items) as text view
                    TextView tv = (TextView) super.getDropDownView(position,convertView,parent);

                    // Set the text color of drop down items
                    tv.setTextColor(Color.WHITE);

                    // If this item is selected item
                    if(position == mSelectedIndex){
                        // Set spinner selected popup item's text color
                        tv.setTextColor(Color.WHITE);
                    }

                    // Return the modified view
                    return tv;
                }
            };
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum OffsetType {
        /**
         *
         */
        UTC
    }

    public static List<String> fetchTimeZones7(Functions.OffsetType type) {

        List<String> timezones = new ArrayList<>();
        String[] zoneIds = TimeZone.getAvailableIDs();

        long timestamp = new Date().getTime();

        for (String zoneId : zoneIds) {
            TimeZone curTimeZone = TimeZone.getTimeZone(zoneId);
            curTimeZone.useDaylightTime();
            String offset = formatOffset(curTimeZone.getOffset(timestamp));

            timezones.add("(" + type + offset + ") " + zoneId);
        }

        return timezones;
    }

    @SuppressLint("DefaultLocale")
    public static String formatOffset(int offset) {
        if (offset == 0) {
            return "+00:00";
        }
        long offsetInHours = TimeUnit.MILLISECONDS.toHours(offset);
        long offsetInMinutesFromHours = TimeUnit.HOURS.toMinutes(offsetInHours);
        long offsetInMinutes = TimeUnit.MILLISECONDS.toMinutes(offset);
        offsetInMinutes = Math.abs(offsetInMinutesFromHours - offsetInMinutes);
        return String.format("%+03d:%02d", offsetInHours, offsetInMinutes);
    }


    static class CountDownRunner implements Runnable{

        private final Activity activity;
        private final TextView tvTime;
        private final TextView tvDate;

        public CountDownRunner(Activity activity, TextView tvTime, TextView tvDate) {
            this.activity = activity;
            this.tvTime = tvTime;
            this.tvDate = tvDate;
        }
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    activity.runOnUiThread(() -> {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            //
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE" + "," + " MMMM dd" + "," + " yyyy");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
                            String strTime = timeFormat.format(calendar.getTime());
                            String strDate = dateFormat.format(calendar.getTime());

                            tvTime.setText(strTime);
                            tvDate.setText(strDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    //noinspection BusyWait
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


    static final int Permission_All = 123;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean checkAllPermissions(Context context, Activity activity) {
        boolean returnVal;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)!= PackageManager.PERMISSION_GRANTED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_WIFI_STATE)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_NUMBERS)
                ) {
                    ActivityCompat.requestPermissions(
                            activity, new String[]{

                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                    , Manifest.permission.ACCESS_FINE_LOCATION
                                    , Manifest.permission.INTERNET
                                    , Manifest.permission.ACCESS_NETWORK_STATE
                                    , Manifest.permission.ACCESS_WIFI_STATE
                                    , Manifest.permission.CAMERA
                                    , Manifest.permission.READ_PHONE_NUMBERS

                            }, Permission_All
                    );
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Grant those permission");
                    builder.setMessage("Please grant all permissions");
                    builder.setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(
                            activity, new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                    , Manifest.permission.ACCESS_FINE_LOCATION
                                    , Manifest.permission.INTERNET
                                    , Manifest.permission.ACCESS_NETWORK_STATE
                                    , Manifest.permission.ACCESS_WIFI_STATE
                                    , Manifest.permission.CAMERA
                                    , Manifest.permission.READ_PHONE_NUMBERS
                            }, Permission_All
                    ));
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
                returnVal = true;
            } else {
                returnVal = false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_WIFI_STATE)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)
                ) {
                    ActivityCompat.requestPermissions(
                            activity, new String[]{

                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                    , Manifest.permission.ACCESS_FINE_LOCATION
                                    , Manifest.permission.INTERNET
                                    , Manifest.permission.ACCESS_NETWORK_STATE
                                    , Manifest.permission.ACCESS_WIFI_STATE
                                    , Manifest.permission.CAMERA
                                    , Manifest.permission.READ_PHONE_STATE

                            }, Permission_All
                    );
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Grant those permission");
                    builder.setMessage("Please grant all permissions");
                    builder.setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(
                            activity, new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                    , Manifest.permission.ACCESS_FINE_LOCATION
                                    , Manifest.permission.INTERNET
                                    , Manifest.permission.ACCESS_NETWORK_STATE
                                    , Manifest.permission.ACCESS_WIFI_STATE
                                    , Manifest.permission.CAMERA
                                    , Manifest.permission.READ_PHONE_STATE
                            }, Permission_All
                    ));
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                returnVal = true;
            } else {
                returnVal = false;
            }
        }
        return returnVal;
    }

    public static boolean statusCheck(Context context, Activity activity, FusedLocationProviderClient fusedLocationClient, LocationRequest locationRequest, LocationCallback locationCallback) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Functions.buildAlertMessageNoGps(context, activity);
            return false;
        } else {
            startLocationUpdates(context, fusedLocationClient, locationRequest, locationCallback);
            return true;
        }
    }

    public static void buildAlertMessageNoGps(Context context, Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public static void startLocationUpdates(Context context, FusedLocationProviderClient fusedLocationClient, LocationRequest locationRequest, LocationCallback locationCallback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public static void stopLocationUpdates(FusedLocationProviderClient fusedLocationClient,LocationCallback locationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public static void checkSettingsAndStartLocationUpdates(LocationRequest locationRequest, Context context, Activity activity, FusedLocationProviderClient fusedLocationClient, LocationCallback locationCallback) {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(context);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            //Settings of device are satisfied and we can start location updates
            Functions.startLocationUpdates(context, fusedLocationClient, locationRequest, locationCallback);
        });
        locationSettingsResponseTask.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(activity, 1001);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void askLocationPermission(Context context, Activity activity, int LOCATION_REQUEST_CODE ) {
        final String TAG = "MainActivity";
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    public static boolean removeDirectory(File directory) {
        // System.out.println("removeDirectory " + directory);
        if (directory == null)
            return false;
        if (!directory.exists())
            return true;
        if (!directory.isDirectory())
            return false;
        String[] list = directory.list();
        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null) {
            for (String s : list) {
                File entry = new File(directory, s);

                if (entry.isDirectory()) {
                    if (!removeDirectory(entry))
                        return false;
                } else {
                    if (!entry.delete())
                        return false;
                }
            }
        }

        return directory.delete();
    }


    public static boolean CheckForInternetConnection(Context context) {
        boolean connected;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (connected) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                System.out.println(urlc.getResponseCode() == 200);
                connected = true;
            } catch (IOException e) {
                System.out.println("error1");
                connected = false;
            }
        } else {
            System.out.println("error1");
            connected = false;
        }
        return connected;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String FullDateFormat() {
        LocalDateTime ldt = LocalDateTime.now();
        // LocalDateTime ldt = LocalDateTime.now().plusDays();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a", Locale.ENGLISH);
        return formatter.format(ldt);
    }

    //Security Verification



}
