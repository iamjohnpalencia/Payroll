package com.example.payroll.Tabs;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Helpers.MyAdapter2;
import com.example.payroll.R;

import java.util.ArrayList;


public class SecondFragment extends Fragment {

    RecyclerView recyclerView;
    Spinner spinnerCat;

    String userCode;

    ArrayList<String> logtype = new ArrayList<>();
    ArrayList<String> dateTime = new ArrayList<>();

    DatabaseHelper databaseHelper;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second, container, false);
        userCode = requireActivity().getIntent().getExtras().getString("SESSION_CODE");
        recyclerView = v.findViewById(R.id.rv_tab_filling);

        spinnerCat = v.findViewById(R.id.sp_tab_filling_cat);
        String[] arraySpinner = new String[] {
                "Approved", "Filled"
        };

        int mSelectedIndex = 0;
        ArrayAdapter adapter= new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,arraySpinner) {
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

        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String AP = spinnerCat.getSelectedItem().toString();
                databaseHelper = new DatabaseHelper(getContext());
                logtype = databaseHelper.getLogType(userCode, false, AP);
                dateTime = databaseHelper.getDateTime(userCode);

                MyAdapter2 myAdapter = new MyAdapter2(getContext(), logtype, dateTime);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

            }

        });

        databaseHelper = new DatabaseHelper(getContext());
        logtype = databaseHelper.getLogType(userCode, false, "Approved");
        dateTime = databaseHelper.getDateTime(userCode);

        MyAdapter2 myAdapter = new MyAdapter2(getContext(), logtype, dateTime);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }
}