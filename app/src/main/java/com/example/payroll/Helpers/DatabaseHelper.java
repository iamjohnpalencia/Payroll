package com.example.payroll.Helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.payroll.Functions;
import com.example.payroll.Modals.FillingModal;
import com.example.payroll.Modals.UserListModal;
import com.example.payroll.Modals.UserSettingsModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String USER_SETTINGS_TABLE = "usersettings";
    public static final String COLUMN_SETTINGS_ID = "id";
    public static final String COLUMN_SETTINGS_LOGGED = "logged";
    public static final String COLUMN_SETTINGS_USERCODE = "usercode";
    public static final String COLUMN_SETTINGS_LOGDATE = "logetLogTypegdate";

    public static final String USER_LIST_TABLE = "userlist";
    public static final String COLUMN_LIST_ID = "id";
    public static final String COLUMN_LIST_COMPANY = "companyname";
    public static final String COLUMN_LIST_DEVICE = "devicename";
    public static final String COLUMN_LIST_USER = "usercode";
    public static final String COLUMN_LIST_PASS = "userpass";
    public static final String COLUMN_LIST_TIMEZONE = "timezone";
    public static final String COLUMN_LIST_REGDATE = "regdate";
    public static final String COLUMN_LIST_VERIFICATION = "verificationstatus";

    public static final String USER_LOGS_TABLE = "userlogs";
    public static final String COLUMN_LOGS_ID = "id";
    public static final String COLUMN_LOGS_COMPANY = "companyname";
    public static final String COLUMN_LOGS_DEVICE = "devicename";
    public static final String COLUMN_LOGS_USER = "usercode";
    public static final String COLUMN_LOGS_FULLADDRESS = "fulladdress";
    public static final String COLUMN_LOGS_TYPE = "logtype";
    public static final String COLUMN_LOGS_DESC = "logdesc";
    public static final String COLUMN_LOGS_LONG = "longitude";
    public static final String COLUMN_LOGS_LAT = "latitude";
    public static final String COLUMN_LOGS_IMAGE = "image";
    public static final String COLUMN_LOGS_SYNC = "sync";
    public static final String COLUMN_LOGS_REGDATE = "regdate";

    public static final String USER_FL_TABLE = "tblfl";
    public static final String COLUMN_FL_ID = "flid";
    public static final String COLUMN_FL_TYPE = "fl_type";
    public static final String COLUMN_FL_DATE_FROM = "fl_datefrom";
    public static final String COLUMN_FL_DATE_TO = "fl_dateto";
    public static final String COLUMN_FL_REASON = "fl_reason";
    public static final String COLUMN_FL_REMARKS = "fl_remarks";
    public static final String COLUMN_FL_USER = "fl_usercode";
    public static final String COLUMN_FL_STATUS = "fl_status";
    public static final String COLUMN_FL_SYNC = "fl_sync";
    public static final String COLUMN_FL_REGDATE = "fl_regdate";

    public static final String USER_FLA_TABLE = "tblfla";
    public static final String COLUMN_FLA_ID = "flid";
    public static final String COLUMN_FLA_TYPE = "fl_type";
    public static final String COLUMN_FLA_DATE_FROM = "fl_datefrom";
    public static final String COLUMN_FLA_DATE_TO = "fl_dateto";
    public static final String COLUMN_FLA_REASON = "fl_reason";
    public static final String COLUMN_FLA_REMARKS = "fl_remarks";
    public static final String COLUMN_FLA_USER = "fl_usercode";
    public static final String COLUMN_FLA_STATUS = "fl_status";
    public static final String COLUMN_FLA_REGDATE = "fl_regdate";





    public DatabaseHelper(Context context) {
        super(context, "hrbuddy", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSettingsTable(db);
        createLogsTable(db);
        createUserListTable(db);
        createUserFillingTable(db);
        createUserFillingTableApprove(db);
    }

    private void createSettingsTable(SQLiteDatabase db){
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + USER_SETTINGS_TABLE + " (" + COLUMN_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COLUMN_SETTINGS_LOGGED + " TEXT, " + COLUMN_SETTINGS_USERCODE + " TEXT , " + COLUMN_SETTINGS_LOGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ) ";
        db.execSQL(createTableStatement);
    }
    private void createLogsTable(SQLiteDatabase db){
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + USER_LOGS_TABLE + " (" + COLUMN_LOGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COLUMN_LOGS_COMPANY + " TEXT, " + COLUMN_LOGS_DEVICE + " TEXT, " + COLUMN_LOGS_USER + " TEXT , "
                + COLUMN_LOGS_FULLADDRESS + " TEXT , " + COLUMN_LOGS_TYPE + " TEXT , " + COLUMN_LOGS_DESC + " TEXT , " + COLUMN_LOGS_LONG + " TEXT , "
                + COLUMN_LOGS_LAT + " TEXT , " + COLUMN_LOGS_IMAGE + " TEXT, " + COLUMN_LOGS_SYNC + " TEXT, " + COLUMN_LOGS_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ) ";
        db.execSQL(createTableStatement);
    }
    private void createUserListTable(SQLiteDatabase db){
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + USER_LIST_TABLE + " (" + COLUMN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COLUMN_LIST_COMPANY + " TEXT, " + COLUMN_LIST_DEVICE + " TEXT, " + COLUMN_LIST_USER+ " TEXT , "
                + COLUMN_LIST_PASS + " TEXT , " + COLUMN_LIST_TIMEZONE + " TEXT ," + COLUMN_LIST_VERIFICATION + " TEXT , " + COLUMN_LIST_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ) ";
        db.execSQL(createTableStatement);
    }
    private void createUserFillingTable(SQLiteDatabase db){
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + USER_FL_TABLE + " (" + COLUMN_FL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COLUMN_FL_TYPE + " TEXT, " + COLUMN_FL_DATE_FROM + " TEXT, " + COLUMN_FL_DATE_TO+ " TEXT , "
                + COLUMN_FL_REASON + " TEXT , " + COLUMN_FL_REMARKS + " TEXT ," + COLUMN_FL_USER + " TEXT, " + COLUMN_FL_STATUS + " TEXT, " + COLUMN_FL_SYNC + " TEXT , " + COLUMN_FL_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ) ";
        db.execSQL(createTableStatement);
    }
    private void createUserFillingTableApprove(SQLiteDatabase db){
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + USER_FLA_TABLE + " (" + COLUMN_FLA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COLUMN_FLA_TYPE + " TEXT, " + COLUMN_FLA_DATE_FROM + " TEXT, " + COLUMN_FLA_DATE_TO+ " TEXT , "
                + COLUMN_FLA_REASON + " TEXT , " + COLUMN_FLA_REMARKS + " TEXT ," + COLUMN_FLA_USER + " TEXT, "
                + COLUMN_FLA_STATUS + " TEXT , " + COLUMN_FLA_REGDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ) ";
        db.execSQL(createTableStatement);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > 1) {
            createUserFillingTable(db);
            createUserFillingTableApprove(db);
        }
    }


    public String [] getSettings() {
        String [] userData;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + USER_SETTINGS_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() < 1) {
            cursor.close();
            UserSettingsModal userSettingsModal;
            userSettingsModal = new UserSettingsModal("N/A","N/A");
            userData = new String[]{"N/A","N/A"};
            insertSettings(userSettingsModal);
        } else {
            cursor.moveToFirst();
            String usLogged = cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_LOGGED));
            String usUserCode = cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_USERCODE));
            userData = new String[]{usLogged, usUserCode};
            cursor.close();
        }

        return  userData;
    }

    public void insertUserList(UserListModal userListModal) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LIST_COMPANY, userListModal.getCompanyName());
        contentValues.put(COLUMN_LIST_DEVICE, userListModal.getDeviceName());
        contentValues.put(COLUMN_LIST_USER, userListModal.getUserCode());
        contentValues.put(COLUMN_LIST_PASS, userListModal.getUserPass());
        contentValues.put(COLUMN_LIST_TIMEZONE, userListModal.getTimezone());
        contentValues.put(COLUMN_LIST_VERIFICATION, userListModal. getVerStatus());
        DB.insert(USER_LIST_TABLE, null, contentValues);
    }

    private void insertSettings(UserSettingsModal userSettingsModal) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SETTINGS_LOGGED, userSettingsModal.getUsLogged());
        contentValues.put(COLUMN_SETTINGS_USERCODE, userSettingsModal.getUsUserCode());
        DB.insert(USER_SETTINGS_TABLE, null, contentValues);
    }


    public Cursor checkNewUser (UserListModal userListModal)
    {
        SQLiteDatabase DB = this.getReadableDatabase();
        String querySelect  = "SELECT * FROM " + USER_LIST_TABLE + " WHERE " + COLUMN_LIST_USER+ " = '" + userListModal.getUserCode() + "' ";
        return DB.rawQuery(querySelect, null);
    }

    //LOGIN methods


    public String [] checkUserExsit(UserListModal userListModal, String fullTimeFormat, String lat, String lnt) {
        String [] userData = {};
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT * FROM " + USER_LIST_TABLE + " WHERE usercode = '" + userListModal.getUserCode() + "' AND userpass = '" + userListModal.getUserPass() + "' ";
        Cursor cursor = db.rawQuery(querySelect, null);

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String ulLogged = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_COMPANY));
            String ulDevice = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_DEVICE));
            String ulUserCode = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_USER));
            userData = new String[]{ulLogged, ulDevice, ulUserCode, "LOGIN", fullTimeFormat, lat, lnt};
        }
        cursor.close();

        return  userData;
    }

    public String [] GetUserDetails(UserListModal userListModal, String fullTimeFormat, String lat, String lnt) {
        String [] userData = {};
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT * FROM " + USER_LIST_TABLE + " WHERE usercode = '" + userListModal.getUserCode() + "' ";
        Cursor cursor = db.rawQuery(querySelect, null);

        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String ulLogged = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_COMPANY));
            String ulDevice = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_DEVICE));
            String ulUserCode = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_USER));
            userData = new String[]{ulLogged, ulDevice, ulUserCode, "LOGOUT", fullTimeFormat, lat, lnt};
        }
        cursor.close();

        return  userData;
    }



    public void updateUserSettings(UserListModal userListModal, String settingsID, boolean logout) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (logout) {
            contentValues.put(COLUMN_SETTINGS_LOGGED,"N/A");
            contentValues.put(COLUMN_SETTINGS_USERCODE, "N/A");
        } else {
            contentValues.put(COLUMN_SETTINGS_LOGGED, "LOGIN");
            contentValues.put(COLUMN_SETTINGS_USERCODE, userListModal.getUserCode());
        }
        String querySelect = "SELECT * FROM " + USER_SETTINGS_TABLE ;
        Cursor cursor = DB.rawQuery(querySelect, null);
        if (cursor.getCount() > 0) {
            DB.update(USER_SETTINGS_TABLE, contentValues, "id=?" , new String[]{settingsID});
        }
        cursor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertUserLogs(UserListModal userListModal) {
        String fullDate = Functions.FullDateFormat();
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LOGS_COMPANY, userListModal.getCompanyName());
        contentValues.put(COLUMN_LOGS_DEVICE, userListModal.getDeviceName());
        contentValues.put(COLUMN_LOGS_USER, userListModal.getUserCode());
        contentValues.put(COLUMN_LOGS_FULLADDRESS, userListModal.getFullAddress());
        contentValues.put(COLUMN_LOGS_TYPE, userListModal.getLogType());
        contentValues.put(COLUMN_LOGS_DESC, userListModal.getLogDesc());
        contentValues.put(COLUMN_LOGS_LONG, userListModal.getLnt());
        contentValues.put(COLUMN_LOGS_LAT, userListModal.getLat());
        contentValues.put(COLUMN_LOGS_IMAGE, userListModal.getImage());
        contentValues.put(COLUMN_LOGS_SYNC, "UNSYNCED");
        contentValues.put(COLUMN_LOGS_REGDATE, fullDate);
        DB.insert(USER_LOGS_TABLE, null, contentValues);

    }

    //FILLING

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertFL(FillingModal fillingModal) {
        String fullDate = Functions.FullDateFormat();
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FL_TYPE, fillingModal.getFlType());
        contentValues.put(COLUMN_FL_DATE_FROM, fillingModal.getFlDateFrom());
        contentValues.put(COLUMN_FL_DATE_TO, fillingModal.getFlDateTo());
        contentValues.put(COLUMN_FL_REASON, fillingModal.getFlReason());
        contentValues.put(COLUMN_FL_REMARKS, fillingModal.getFlRemarks());
        contentValues.put(COLUMN_FL_USER, fillingModal.getFlUserCode());
        contentValues.put(COLUMN_FL_STATUS, fillingModal.getFlStatus());
        contentValues.put(COLUMN_FL_SYNC, "UNSYNCED");
        contentValues.put(COLUMN_FL_REGDATE, fullDate);
        DB.insert(USER_FL_TABLE, null, contentValues);
    }

    public String [] getUserInfo(String userCode, String logType, String logDesc) {
        String [] userData = {};
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT " + COLUMN_LOGS_COMPANY + ", " + COLUMN_LOGS_DEVICE + ", " + COLUMN_LOGS_FULLADDRESS +
                ", " + COLUMN_LOGS_LONG + ", " + COLUMN_LOGS_LAT + " FROM " + USER_LOGS_TABLE + " WHERE " + COLUMN_LOGS_USER + " = '" + userCode + "' ORDER BY " + COLUMN_LOGS_ID + " DESC LIMIT 1 ";
        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String ulComp = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_COMPANY));
            String ulDevice = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_DEVICE));
            String ulFullAdd = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_FULLADDRESS));
            String ulLong = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_LONG));
            String ulLat = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_LAT));

            userData = new String[]{ulComp, ulDevice,userCode, ulFullAdd,logType,logDesc,ulLong,ulLat, "N/A"};
        }
        cursor.close();
        return  userData;
    }

   // SELECT companyname, devicename, fulladdress, longitude, latitude FROM userlogs ORDER BY id DESC LIMIT 1


    public String getPassword(String userCode) {
        String userPass = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT " + COLUMN_LIST_PASS + " FROM " + USER_LIST_TABLE + " WHERE usercode = '" + userCode + "' ";
        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            userPass = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_PASS));
        }
        cursor.close();
        return userPass;
    }

    //LOGS ACTIVITY
    public String[] getUserDetails(UserListModal userListModal) {
        String [] userData = {};
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT * FROM " + USER_LIST_TABLE + " WHERE usercode = '" + userListModal.getUserCode() + "' ";
        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String ulLogged = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_COMPANY));
            String ulDevice = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_DEVICE));
            userData = new String[]{ulLogged, ulDevice };
        }
        cursor.close();
        return  userData;
    }

    public JSONObject retrieveJsonImages(String userCode) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();

        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT " + COLUMN_LOGS_IMAGE + ", " + COLUMN_LOGS_TYPE + " FROM " + USER_LOGS_TABLE + " WHERE " + COLUMN_LOGS_TYPE + " IN ('IN','OUT') AND " + COLUMN_LOGS_USER + " = '" + userCode  + "' ORDER BY " + COLUMN_LIST_ID + " DESC LIMIT 6";
        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                String ulImage = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_IMAGE));
                String ulType = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TYPE));
                obj.put("image", ulImage);
                obj.put("logtype", ulType);
                array.put(obj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        jsonObject.put("MyArray" , array);
        return jsonObject;
    }

    public String[] GetLastLogs(String userCode) {
        String [] userData;
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect;
        if (userCode.equals("")) {
            querySelect  = "SELECT " + COLUMN_LOGS_FULLADDRESS + "," + COLUMN_LOGS_DESC + "," + COLUMN_LOGS_TYPE + " FROM " + USER_LOGS_TABLE + " ORDER BY " + COLUMN_LOGS_ID + " DESC LIMIT 1 ";
        } else {
            querySelect  = "SELECT " + COLUMN_LOGS_FULLADDRESS + "," + COLUMN_LOGS_DESC + "," + COLUMN_LOGS_TYPE + " FROM " + USER_LOGS_TABLE + " WHERE usercode = '" + userCode + "' ORDER BY " + COLUMN_LOGS_ID + " DESC LIMIT 1 ";
        }

        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String ulAddress = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_FULLADDRESS));
            String ulDesc = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_DESC));
            String ulType = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TYPE));
            if (ulType.equals("IN") || ulType.equals("OUT")) {
                userData = new String[]{ulAddress, ulDesc, "TIME " + ulType};
            } else {
                userData = new String[]{ulAddress, ulDesc, ulType};
            }
        } else {
            userData = new String[]{"N/A", "N/A", "NO ACTION"};
        }
        cursor.close();
        return  userData;
    }

    public String[] getUserLastLoc() {
        String [] userData = {};
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT " + COLUMN_LOGS_LONG + ", " + COLUMN_LOGS_LAT + " FROM " + USER_LOGS_TABLE + " WHERE " + COLUMN_LOGS_TYPE + " IN ('LOGIN','LOGOUT','IN','OUT') LIMIT 1";
        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String ulLong = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_LONG));
            String ulLat = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_LAT));
            userData = new String[]{ulLat,ulLong  };
        }
        cursor.close();
        return  userData;
    }

    //SYNC DATA
    public JSONObject SyncUserData(String userCode) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT * FROM " + USER_LOGS_TABLE + " WHERE " + COLUMN_LOGS_SYNC + " = 'UNSYNCED' AND " + COLUMN_LOGS_USER + " = '" + userCode  + "'";
        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.moveToFirst()) {

            do {

                JSONObject obj = new JSONObject();
                String ulID = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_ID));
                String ulCompany = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_COMPANY));
                String ulDevice = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_DEVICE));
                String ulUserCode = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_USER));
                String ulFullAddress = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_FULLADDRESS));
                String ulLogType = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TYPE));
                String ulLogDesc = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_DESC));
                String ulLongitude = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_LONG));
                String ulLatitude = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_LAT));
                String ulImage = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_IMAGE));
                String ulRegDate = cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_REGDATE));

                obj.put("id", ulID);
                obj.put("company", ulCompany);
                obj.put("device", ulDevice);
                obj.put("user", ulUserCode);
                obj.put("address", ulFullAddress);
                obj.put("type", ulLogType);
                obj.put("desc", ulLogDesc);
                obj.put("longitude", ulLongitude);
                obj.put("latitude", ulLatitude);
                obj.put("image", ulImage);
                obj.put("date", ulRegDate);

                array.put(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        jsonObject.put("MyUserData" , array);
        return jsonObject;
    }

    public JSONObject SyncUserDataFilling(String userCode) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT * FROM " + USER_FL_TABLE + " WHERE " + COLUMN_FL_SYNC + " = 'UNSYNCED' AND " + COLUMN_FL_USER + " = '" + userCode  + "'";

        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.moveToFirst()) {

            do {

                JSONObject obj = new JSONObject();
                String ufID = cursor.getString(cursor.getColumnIndex(COLUMN_FL_ID));
                String ufType = cursor.getString(cursor.getColumnIndex(COLUMN_FL_TYPE));
                String ufFromDate = cursor.getString(cursor.getColumnIndex(COLUMN_FL_DATE_FROM));
                String ufToDate = cursor.getString(cursor.getColumnIndex(COLUMN_FL_DATE_TO));
                String ufReason = cursor.getString(cursor.getColumnIndex(COLUMN_FL_REASON));
                String ufRemarks = cursor.getString(cursor.getColumnIndex(COLUMN_FL_REMARKS));
                String ufUser = cursor.getString(cursor.getColumnIndex(COLUMN_FL_USER));
                String ufStatus = cursor.getString(cursor.getColumnIndex(COLUMN_FL_STATUS));
                String ufRegDate = cursor.getString(cursor.getColumnIndex(COLUMN_FL_REGDATE));

                obj.put("flaID", ufID);
                obj.put("flaType", ufType);
                obj.put("flaFromDate", ufFromDate);
                obj.put("flaToDate", ufToDate);
                obj.put("flaReason", ufReason);
                obj.put("flaRemarks", ufRemarks);
                obj.put("flaUser", ufUser);
                obj.put("flaStatus", ufStatus);
                obj.put("flaRegDate", ufRegDate);

                array.put(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        jsonObject.put("MyUserData" , array);
        return jsonObject;
    }

    public void UpdateLogsLocal(String logsId) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LOGS_SYNC, "SYNCED");
        DB.update(USER_LOGS_TABLE, contentValues, "id=?" , new String[]{logsId});
        DB.close();
    }
    public void UpdateFillingLocal(String logsId) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FL_SYNC, "SYNCED");
        DB.update(USER_FL_TABLE, contentValues, "flid=?" , new String[]{logsId});
        DB.close();
    }
    //NAV LOGS

    public ArrayList<String> getLogType(String userCode, Boolean bool, String AP) {

        ArrayList<String> returnList = new ArrayList<>();
        String queryString;
        if (bool) {
            queryString = "SELECT " + COLUMN_LOGS_TYPE + " FROM " + USER_LOGS_TABLE + " WHERE " + COLUMN_LOGS_USER + " = '" + userCode + "' LIMIT 100 ";
        } else {
            if (AP.equals("Approved")) {
                queryString = "SELECT " + COLUMN_FLA_TYPE + "," + COLUMN_FLA_STATUS + " FROM " + USER_FLA_TABLE + " WHERE " + COLUMN_FLA_USER + " = '" + userCode + "' AND " + COLUMN_FLA_STATUS + " = 'A'  AND " + COLUMN_FLA_TYPE + " IN ('LEAVE','ABSENT') LIMIT 100 ";
            } else {
                queryString = "SELECT " + COLUMN_FL_TYPE + "," + COLUMN_FL_STATUS + " FROM " + USER_FL_TABLE + " WHERE " + COLUMN_FL_USER + " = '" + userCode + "' AND " + COLUMN_FL_STATUS + " = 'N/A' AND " + COLUMN_FL_TYPE + " IN ('LEAVE','ABSENT') LIMIT 100 ";

            }
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                String logType = cursor.getString(0);
                if (bool) {
                    returnList.add(logType);
                } else {
                    String logStats = cursor.getString(1);
                    String logDef;
                    if (logStats.equals("A")) {
                        logDef = "APPROVED";
                    } else {
                        logDef = "PENDING";
                    }
                    returnList.add(logType + "("+ logDef +")");
                }

            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return returnList;
    }
    public ArrayList<String> getDateTime(String userCode) {

        ArrayList<String> returnList = new ArrayList<>();
        String queryString = "SELECT " + COLUMN_FLA_REGDATE + " FROM " + USER_FLA_TABLE + " WHERE " + COLUMN_FLA_USER + " = '" + userCode + "' LIMIT 100";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                String logType = cursor.getString(0);
                returnList.add(logType);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return returnList;
    }
    public ArrayList<String> getAddress(String userCode) {

        ArrayList<String> returnList = new ArrayList<>();
        String queryString = "SELECT " + COLUMN_LOGS_FULLADDRESS + " FROM " + USER_LOGS_TABLE + " WHERE " + COLUMN_LOGS_USER + " = '" + userCode + "' LIMIT 100";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                String logType = cursor.getString(0);
                returnList.add(logType);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return returnList;
    }

    //SECURITY VERIFICATION

    public void updateVerification(String userCode) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LIST_VERIFICATION,"IS SET"); //These Fields should be your String values of actual column names
        DB.update(USER_LIST_TABLE, cv, "usercode = ?", new String[]{userCode});

    }

    public boolean secQuestionSetup(String userCode) {
        boolean statusIsSet = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT " + COLUMN_LIST_VERIFICATION + " FROM " + USER_LIST_TABLE + " WHERE usercode = '" + userCode + "' ";

        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String ulVerStats = cursor.getString(cursor.getColumnIndex(COLUMN_LIST_VERIFICATION));
            statusIsSet = !ulVerStats.equals("NOT SET");

        }
        cursor.close();

        return statusIsSet;
    }

    public void cancelVerificationSetup() {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SETTINGS_LOGGED, "N/A");
        contentValues.put(COLUMN_SETTINGS_USERCODE, "N/A");
        DB.update(USER_SETTINGS_TABLE, contentValues, "id = ?", new String[]{"1"});
    }

    public void changePassword(String userCode, String userPass) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LIST_PASS, userPass);
        DB.update(USER_LIST_TABLE, contentValues, "usercode = ?", new String[]{userCode});
    }

    //SETTINGS

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void DropTables() {
        SQLiteDatabase DB = this.getWritableDatabase();
        String fullDate = Functions.FullDateFormat();
        DB.delete(USER_LIST_TABLE, null,null);
        DB.delete(USER_LOGS_TABLE, null,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SETTINGS_LOGGED, "N/A");
        contentValues.put(COLUMN_SETTINGS_USERCODE, "N/A");
        contentValues.put(COLUMN_SETTINGS_LOGDATE, fullDate);
        DB.update(USER_SETTINGS_TABLE, contentValues, "id = ?", new String[]{"1"});
    }

    public void SaveNewPassword(String userCode, String userPass) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LIST_PASS, userPass);
        DB.update(USER_LIST_TABLE, contentValues, "usercode = ?", new String[]{userCode});
    }

    //Fetch Filling
    public void FetchData(FillingModal fillingModal, String regDate) {

        SQLiteDatabase db = this.getReadableDatabase();
        String querySelect = "SELECT " + COLUMN_FLA_ID + " FROM " + USER_FLA_TABLE + " WHERE " + COLUMN_FLA_REGDATE + " = '" + regDate + "'";
        Cursor cursor = db.rawQuery(querySelect, null);
        if (cursor.getCount() == 0) {

            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_FLA_TYPE, fillingModal.getFlType());
            contentValues.put(COLUMN_FLA_DATE_FROM, fillingModal.getFlDateFrom());
            contentValues.put(COLUMN_FLA_DATE_TO, fillingModal.getFlDateTo());
            contentValues.put(COLUMN_FLA_REASON, fillingModal.getFlReason());
            contentValues.put(COLUMN_FLA_REMARKS, fillingModal.getFlRemarks());
            contentValues.put(COLUMN_FLA_USER, fillingModal. getFlUserCode());
            contentValues.put(COLUMN_FLA_STATUS, fillingModal. getFlStatus());
            contentValues.put(COLUMN_FLA_REGDATE, regDate);
            DB.insert(USER_FLA_TABLE, null, contentValues);

        }

        querySelect = "SELECT " + COLUMN_FL_ID + " FROM " + USER_FL_TABLE + " WHERE " + COLUMN_FL_REGDATE + " = '" + regDate + "'";
        cursor = db.rawQuery(querySelect, null);

        if (cursor.getCount() == 1) {
            SQLiteDatabase DBa = this.getWritableDatabase();
            ContentValues contentValuesFL = new ContentValues();
            contentValuesFL.put(COLUMN_FL_STATUS, "A");
            DBa.update(USER_FL_TABLE, contentValuesFL, ""+ COLUMN_FL_REGDATE +"=?" , new String[]{regDate});

        }

        cursor.close();
    }
}
