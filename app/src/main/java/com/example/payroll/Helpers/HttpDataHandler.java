package com.example.payroll.Helpers;

import android.content.Context;

import com.example.payroll.Functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by reale on 11/30/2016.
 */

public class HttpDataHandler {

    public HttpDataHandler() {
    }

    public String GetHTTPData(String requestUrl, Context context)
    {
        URL url;
        StringBuilder response = new StringBuilder();
        try{

            if (Functions.CheckForInternetConnection(context)) {
                url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while((line = br.readLine()) != null)
                        response.append(line);
                } else {
                    response = new StringBuilder();
                }

            } else {
                response = new StringBuilder();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
