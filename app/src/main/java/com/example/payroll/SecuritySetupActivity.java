package com.example.payroll;

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


public class SecuritySetupActivity extends AppCompatActivity {


    TextView tvForUser;
    String userCode;
    String userPass;
    int questionCode;
    boolean setupSuccess = false;

    Spinner spinner;
    EditText editText;
    ProgressDialog dialog;

    Button btnSave, btnCancel;

    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_verification);

        userCode = getIntent().getStringExtra("SESSION_CODE");
        userPass = getIntent().getStringExtra("SESSION_PASS");

        tvForUser = findViewById(R.id.tv_security_foruser);
        tvForUser.append(userCode);

        spinner = findViewById(R.id.sp_security_question);
        editText = findViewById(R.id.et_security_answer);

        btnSave = findViewById(R.id.btn_fpass_save);
        btnCancel = findViewById(R.id.btn_fpass_cancel);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionCode = spinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(v -> {
            if (isEmpty(editText.getText().toString())) {
                Toast.makeText(SecuritySetupActivity.this, "Fill up all the fields", Toast.LENGTH_SHORT).show();
            } else {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Processing.");
                dialog.show();

                SaveVerification saveVerification = new SaveVerification(String.valueOf(questionCode), editText.getText().toString(), userCode, userPass);
                Thread myThread = new Thread(saveVerification);
                myThread.start();

            }
        });

        btnCancel.setOnClickListener(v -> {
            CancelSetup cancelSetup = new CancelSetup();
            Thread myThread = new Thread(cancelSetup);
            myThread.start();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading questions. Please wait.");
        dialog.show();

        getVerificationData runnable = new getVerificationData();
        Thread myThread = new Thread(runnable);
        myThread.start();

    }

     class getQuestions implements Runnable {

        boolean HasInternet = false;
        @Override
        public void run() {
            String url = "http://jvadsv.aiolosinnovativesolutions.com/generatequestion.php";
            HasInternet = Functions.CheckForInternetConnection(SecuritySetupActivity.this);
            if (HasInternet) {
                StringRequest request = new StringRequest(url, this::parseJsonData, error -> Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show());
                request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue rQueue = Volley.newRequestQueue(SecuritySetupActivity.this);
                rQueue.add(request);
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

                Functions.spinnerGlobalArray = new ArrayAdapter<String>(SecuritySetupActivity.this,R.layout.questions_row,android.R.id.text1,al)
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
                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class getVerificationData implements Runnable {

        boolean HasInternet = false;
        @Override
        public void run() {
            String url = "http://jvadsv.aiolosinnovativesolutions.com/fetchdata.php?usercode=" + userCode + "&tofetch=questions";
            HasInternet = Functions.CheckForInternetConnection(SecuritySetupActivity.this);
            if (HasInternet) {
                StringRequest request = new StringRequest(url, this::parseJsonData, error -> Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show());
                request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue rQueue = Volley.newRequestQueue(SecuritySetupActivity.this);
                rQueue.add(request);
            }
        }

        private void parseJsonData(String s) {
            try {

                String exist = null;
                JSONObject object = new JSONObject(s);
                JSONArray fruitsArray = object.getJSONArray("svexist");

                for(int i = 0; i < fruitsArray.length(); ++i) {
                    JSONObject obj = fruitsArray.getJSONObject(i);
                    exist = obj.getString("EXIST");

                }
                assert exist != null;
                if (exist.equals("1")) {
                    dialog.dismiss();

                    databaseHelper = new DatabaseHelper(SecuritySetupActivity.this);
                    databaseHelper.updateVerification(userCode);
                    Intent intent = new Intent(SecuritySetupActivity.this, UserLogsActivity.class);
                    intent.putExtra("SESSION_CODE", userCode);
                    intent.putExtra("SESSION_PASS", userPass);
                    startActivity(intent);
                    Toast.makeText(SecuritySetupActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    getQuestions runnable = new getQuestions();
                    Thread myThread = new Thread(runnable);
                    myThread.start();
                }

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

        public SaveVerification(String question, String answer, String userCode, String userPass) {
            this.getQuestionID = question;
            this.getAnswer = answer;
            this.getUserCode = userCode;
            this.getUserPass = userPass;
        }

        @Override
        public void run() {

            try {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                dialog.dismiss();
                if (setupSuccess) {
                    databaseHelper = new DatabaseHelper(SecuritySetupActivity.this);
                    databaseHelper.updateVerification(userCode);
                    Intent intent = new Intent(SecuritySetupActivity.this, UserLogsActivity.class);
                    intent.putExtra("SESSION_CODE", getUserCode);
                    intent.putExtra("SESSION_PASS", getUserPass);
                    startActivity(intent);
                    Toast.makeText(SecuritySetupActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SecuritySetupActivity.this, "An error occured.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class CancelSetup implements Runnable {
        @Override
        public void run() {
            databaseHelper = new DatabaseHelper(SecuritySetupActivity.this);
            databaseHelper.cancelVerificationSetup();

            runOnUiThread(() -> {
                dialog.dismiss();
                Intent intent = new Intent(SecuritySetupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }


}
