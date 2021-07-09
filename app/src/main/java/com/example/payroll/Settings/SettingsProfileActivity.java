package com.example.payroll.Settings;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Functions;
import com.example.payroll.R;
import com.example.payroll.UserLogsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import static android.text.TextUtils.isEmpty;

public class SettingsProfileActivity extends AppCompatActivity {

    Spinner spinner;
    ProgressDialog dialog;

    Button btnSaveVerification, btnSavePass;

    EditText etAnswer, etCurrentPass, etNewPass, etConNewPass;
    String userCode, userPass;
    int questionCode = 0;

    boolean setupSuccess = false;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);
        spinner = findViewById(R.id.sp_settings_profile_question);
        btnSaveVerification = findViewById(R.id.btn_settings_profile_save);

        etAnswer = findViewById(R.id.et_settings_profile_answer);

        etCurrentPass = findViewById(R.id.et_settings_profile_currentpass);
        etNewPass = findViewById(R.id.et_settings_profile_newpass);
        etConNewPass = findViewById(R.id.et_settings_profile_conpass);

        btnSavePass = findViewById(R.id.btn_settings_profile_savepass);
        userCode = getIntent().getStringExtra("SESSION_CODE");
        userPass = getIntent().getStringExtra("SESSION_PASS");

        btnSaveVerification.setOnClickListener(v -> {
            if (isEmpty(etAnswer.getText().toString())) {
                Toast.makeText(SettingsProfileActivity.this, "Fill up all the fields", Toast.LENGTH_SHORT).show();
            } else {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Processing.");
                dialog.show();

                SaveVerification saveVerification = new SaveVerification(String.valueOf(questionCode), etAnswer.getText().toString(), userCode, userPass);
                Thread myThread = new Thread(saveVerification);
                myThread.start();
            }
        });

        btnSavePass.setOnClickListener(v -> {
            String curPass = etCurrentPass.getText().toString();
            String newPass = etNewPass.getText().toString();
            String conPass = etConNewPass.getText().toString();
            if (isEmpty(curPass) || isEmpty(newPass) || isEmpty(conPass)) {
                Toast.makeText(SettingsProfileActivity.this, "Fill up all the fields", Toast.LENGTH_SHORT).show();
            } else {

                if(curPass.equals(userPass)) {
                    if (newPass.equals(conPass)) {
                        dialog = new ProgressDialog(this);
                        dialog.setMessage("Processing.");
                        dialog.show();

                        SaveNewPass runnable = new SaveNewPass(newPass, userCode);
                        Thread myThread = new Thread(runnable);
                        myThread.start();

                    } else {
                        Toast.makeText(SettingsProfileActivity.this, "New password did not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingsProfileActivity.this, "Current password did not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionCode = spinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading questions. Please wait.");
        dialog.show();

        getQuestions runnable = new getQuestions();
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    class getQuestions implements Runnable {

        boolean HasInternet = false;
        @Override
        public void run() {
            String url = "http://jvadsv.aiolosinnovativesolutions.com/generatequestion.php";

            if (Functions.CheckForInternetConnection(SettingsProfileActivity.this)) {
                HasInternet = true;
                StringRequest request = new StringRequest(url, this::parseJsonData, error -> Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show());
                request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue rQueue = Volley.newRequestQueue(SettingsProfileActivity.this);
                rQueue.add(request);
            } else {
                HasInternet = false;
            }
        }

        private void parseJsonData(String s) {
            try {

                JSONObject object = new JSONObject(s);
                JSONArray fruitsArray = object.getJSONArray("questions");
                ArrayList al = new ArrayList();

                for(int i = 0; i < fruitsArray.length(); ++i) {
                    al.add(fruitsArray.getString(i));
                }

                Functions.spinnerGlobalArray = new ArrayAdapter<String>(SettingsProfileActivity.this,R.layout.questions_row,android.R.id.text1,al)
                {

                    @Override
                    public View getDropDownView(final int position, final View convertView, @NonNull final ViewGroup parent)
                    {
                        final View v=super.getDropDownView(position,convertView,parent);

                        v.post(() -> ((TextView)v.findViewById(android.R.id.text1)).setSingleLine(false));
                        return v;
                    }
                };

                Functions.spinnerGlobalArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(Functions.spinnerGlobalArray);
                if (!HasInternet) {
                    Toast.makeText(SettingsProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class SaveVerification implements Runnable {
        StringBuilder content;
        String getQuestionID;
        String getAnswer;
        String getUserCode;
        String getUserPass;
        boolean hasInternet = false;

        public SaveVerification(String question, String answer, String userCode, String userPass) {
            this.getQuestionID = question;
            this.getAnswer = answer;
            this.getUserCode = userCode;
            this.getUserPass = userPass;
        }

        @Override
        public void run() {

            try {

                if (Functions.CheckForInternetConnection(SettingsProfileActivity.this)) {
                    hasInternet = true;
                    URL url = new URL("http://jvadsv.aiolosinnovativesolutions.com/generatequestion.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    Map<String, String> params = new HashMap<>();


                    params.put("forsecurity", "YES");
                    params.put("questionid", getQuestionID);
                    params.put("answer", getAnswer);
                    params.put("usercode", getUserCode);
                    params.put("userpass", getUserPass);

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        if (postData.length() != 0) {
                            postData.append('&');
                        }
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }

                    byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
                    connection.setDoOutput(true);
                    try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
                        writer.write(postDataBytes);
                        writer.flush();
                        writer.close();

                        try (BufferedReader in = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()))) {
                            String line;
                            content = new StringBuilder();
                            while ((line = in.readLine()) != null) {
                                content.append(line);
                                content.append(System.lineSeparator());
                            }
                        }
                        setupSuccess = content.length() == 8;
                        System.out.println(content.length());
                    } finally {
                        connection.disconnect();
                    }
                } else {
                    hasInternet = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                dialog.dismiss();
                if (hasInternet) {
                    if (setupSuccess) {
                        databaseHelper = new DatabaseHelper(SettingsProfileActivity.this);
                        databaseHelper.updateVerification(userCode);
                        Toast.makeText(SettingsProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        etAnswer.setText("");
                    } else {
                        Toast.makeText(SettingsProfileActivity.this, "An error occured.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingsProfileActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

     class SaveNewPass implements Runnable {
         String getNewPassword;
         String getUserCode;
         public SaveNewPass (String newPassword, String userCode) {
             this.getNewPassword = newPassword;
             this.getUserCode = userCode;
         }
         @Override
         public void run() {
             DatabaseHelper databaseHelper;
             databaseHelper = new DatabaseHelper(SettingsProfileActivity.this);
             databaseHelper.SaveNewPassword(getUserCode, getNewPassword);

             runOnUiThread(() -> {
                 dialog.dismiss();
                 Intent intent = new Intent(SettingsProfileActivity.this, UserLogsActivity.class);
                 intent.putExtra("SESSION_CODE", getUserCode);
                 intent.putExtra("SESSION_PASS", getNewPassword);
                 startActivity(intent);
             });
         }
     }
}
