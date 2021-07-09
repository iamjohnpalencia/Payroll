package com.example.payroll.Settings;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Functions;
import com.example.payroll.LoginActivity;
import com.example.payroll.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class ForgotPasswordActivity extends AppCompatActivity {
    Spinner spinner;
    EditText etUserCode, editTextAnswer, etNewPass, etConPass;
    ProgressDialog dialog;
    Button btnSave, btnCancel;
    int questionCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        spinner = findViewById(R.id.sp_fpass_question);
        editTextAnswer = findViewById(R.id.et_fpass_answer);

        etUserCode = findViewById(R.id.et_fpass_usercode);
        etNewPass = findViewById(R.id.et_fpass_newpass);
        etConPass = findViewById(R.id.et_fpass_confirmpass);

        btnSave = findViewById(R.id.btn_fpass_save);
        btnCancel = findViewById(R.id.btn_fpass_cancel);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading questions. Please wait.");
        dialog.show();


        btnSave.setOnClickListener(v -> {
            if (isEmpty(etUserCode.getText().toString()) || isEmpty(editTextAnswer.getText().toString()) || isEmpty(etNewPass.getText().toString()) || isEmpty(etConPass.getText().toString())) {

                Toast.makeText(ForgotPasswordActivity.this, "Fill up all the fields", Toast.LENGTH_SHORT).show();

            } else {

                String newPassword = etNewPass.getText().toString();
                String conPass = etConPass.getText().toString();

                if (newPassword.equals(conPass)) {
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Processing.");
                    dialog.show();

                    CheckUsersAccount saveVerification = new CheckUsersAccount(String.valueOf(questionCode), etUserCode.getText().toString(), editTextAnswer.getText().toString(), newPassword);
                    Thread myThread = new Thread(saveVerification);
                    myThread.start();

                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Password not match.", Toast.LENGTH_SHORT).show();
                }




            }
        });

        btnCancel.setOnClickListener(v -> {

            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        });
        spinner.setAdapter(Functions.spinnerGlobalArray);
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


     class CheckUsersAccount implements Runnable {
        StringBuilder content;
        String getQuestion;
        String getUserCode;
        String getAnswer;
        String getUserPass;

        public CheckUsersAccount(String question, String userCode, String answer, String password) {
            this.getQuestion = question;
            this.getUserCode = userCode;
            this.getAnswer = answer;
            this.getUserPass = password;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("http://jvadsv.aiolosinnovativesolutions.com/generatequestion.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                Map<String, String> params = new HashMap<>();

                params.put("forsecurity", "NO");
                params.put("questionid", getQuestion);
                params.put("answer", getAnswer);
                params.put("usercode", getUserCode);

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

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        content = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                        }
                    }

                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                dialog.dismiss();
                System.out.println(content);
                if (content.length() == 6) {
                    Toast.makeText(ForgotPasswordActivity.this, "Credentials not match.", Toast.LENGTH_SHORT).show();
                } else if (content.length() == 8) {
                    DatabaseHelper databaseHelper;
                    databaseHelper = new DatabaseHelper(ForgotPasswordActivity.this);
                    databaseHelper.changePassword(getUserCode, getUserPass);
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Users security verification not set. Please contact administrator.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
