package com.example.payroll;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Helpers.HttpDataHandler;
import com.example.payroll.Modals.FillingModal;
import com.example.payroll.Modals.UserListModal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FillingsActivity extends AppCompatActivity {
    boolean fromDate = true;
    Calendar myCalendarTo = Calendar.getInstance();

    TextView tvFromDate, tvDateFromVal, tvToDate, tvDateTo;
    Spinner spinnerCat, spinnerReason;
    EditText etRemarks;

    Button flSave;
    String userCode;

    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filiings);

        flSave = findViewById(R.id.btn_filling_save);

        tvFromDate = findViewById(R.id.tv_filling_datefrom);
        tvDateFromVal = findViewById(R.id.tv_filling_datefromval);

        tvToDate = findViewById(R.id.tv_filling_dateto);
        tvDateTo = findViewById(R.id.tv_filling_datetoval);

        spinnerCat = findViewById(R.id.sp_filling_category);
        spinnerReason  = findViewById(R.id.sp_filling_reason);

        etRemarks = findViewById(R.id.et_filling_remarks);

        userCode = getIntent().getStringExtra("SESSION_CODE");

        String[] arraySpinner = new String[] {
                "Leave", "Absent"
        };

        int mSelectedIndex = 0;
        ArrayAdapter adapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,arraySpinner) {
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapter);

        String[] arrayReason = new String[] {
                "Maternity", "Sick", "Vacation", "Other"
        };

        ArrayAdapter reasonAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,arrayReason) {
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
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(reasonAdapter);


        final Calendar myCalendar = Calendar.getInstance();
        myCalendar.add(Calendar.DATE, 0);

        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel(myCalendar);
        };

        tvFromDate.setOnClickListener(v -> {
            fromDate = true;
            DatePickerDialog datePickerDialog = new DatePickerDialog(FillingsActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
            datePickerDialog.show();
        });

        tvToDate.setOnClickListener(v -> {
            fromDate = false;
            if (!tvDateFromVal.getText().equals("")) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(FillingsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(myCalendarTo.getTimeInMillis());
                datePickerDialog.show();
            } else {
                Toast.makeText(this, "Select from date first", Toast.LENGTH_SHORT).show();
            }
        });

        flSave.setOnClickListener(v ->{

            String getCat = spinnerCat.getSelectedItem().toString();
            String getDateFrom = tvDateFromVal.getText().toString();
            String getDateTo = tvDateTo.getText().toString();
            String getRemarks = etRemarks.getText().toString();
            String getReason = spinnerReason.getSelectedItem().toString();

            if (!tvDateFromVal.getText().equals("") || !tvDateTo.getText().equals("") || !etRemarks.getText().toString().equals("")) {
                dialog = new ProgressDialog(FillingsActivity.this);
                dialog.setMessage("Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                if (!FillingsActivity.this.isFinishing()){
                    dialog.show();
                }
                InsertFL runnable = new InsertFL(getCat,getDateFrom,getDateTo,getRemarks,getReason,userCode);
                Thread myThread = new Thread(runnable);
                myThread.start();
            } else {
                Toast.makeText(this, "Fill up all the fields", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void updateLabel(Calendar myCalendar) {
        String myFormat = "yy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        if (fromDate) {
            tvDateFromVal.setText(sdf.format(myCalendar.getTime()));
            myCalendarTo = myCalendar;
        } else {
            tvDateTo.setText(sdf.format(myCalendar.getTime()));
        }

    }

    class InsertFL implements Runnable {

        DatabaseHelper databaseHelper;
        FillingModal fillingModal;
        UserListModal userListModal;
        String[] userData;

        String getCategory;
        String getDateFrom;
        String getDateTo;
        String getRemarks;
        String getReason;
        String getUserCode;
        String strTime;
        String getTimeDate;
        Calendar calendar = Calendar.getInstance();

        public InsertFL(String getCategory, String getDateFrom, String getDateTo, String getRemarks, String getReason, String getUserCode) {
            this.getCategory = getCategory;
            this.getDateFrom = getDateFrom;
            this.getDateTo = getDateTo;
            this.getRemarks = getRemarks;
            this.getReason = getReason;
            this.getUserCode = getUserCode;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {

            if (Functions.CheckForInternetConnection(FillingsActivity.this)) {
                HttpDataHandler http = new HttpDataHandler();
                getTimeDate = "http://jvadsv.aiolosinnovativesolutions.com/getdatetime.php";
                strTime = "LIVE: " + http.GetHTTPData(getTimeDate, FillingsActivity.this);
            } else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
                strTime = "OFF: " +  timeFormat.format(calendar.getTime());
            }

            fillingModal = new FillingModal(getUserCode, getCategory.toUpperCase(), getDateFrom, getDateTo, getReason, getRemarks, "UNSYNCED", "N/A");
            databaseHelper = new DatabaseHelper(FillingsActivity.this);
            databaseHelper.insertFL(fillingModal);

            databaseHelper = new DatabaseHelper(FillingsActivity.this);
            userData = databaseHelper.getUserInfo(getUserCode, getCategory.toUpperCase(),strTime);

            userListModal = new UserListModal( -1, userData[0],userData[1],userData[2],userData[3],userData[4],userData[5],userData[6],userData[7],userData[8]);
            databaseHelper = new DatabaseHelper(FillingsActivity.this);
            databaseHelper.insertUserLogs(userListModal);

            runOnUiThread(() -> {
                dialog.dismiss();
                tvDateFromVal.setText("");
                tvDateTo.setText("");
                etRemarks.setText("");
                Toast.makeText(FillingsActivity.this, "Complete!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
