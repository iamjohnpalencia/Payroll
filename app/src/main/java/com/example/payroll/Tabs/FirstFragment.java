package com.example.payroll.Tabs;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payroll.Helpers.DatabaseHelper;
import com.example.payroll.Helpers.MyAdapter;
import com.example.payroll.R;

import java.util.ArrayList;



public class FirstFragment extends Fragment {

    RecyclerView recyclerView;
    String userCode;

    ArrayList<String> logtype = new ArrayList<>();
    ArrayList<String> dateTime = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();

    DatabaseHelper databaseHelper;
    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        userCode = requireActivity().getIntent().getExtras().getString("SESSION_CODE");
        recyclerView = v.findViewById(R.id.rv_tab_userlogs);

        databaseHelper = new DatabaseHelper(getContext());
        logtype = databaseHelper.getLogType(userCode, true, "");
        dateTime = databaseHelper.getDateTime(userCode);
        address = databaseHelper.getAddress(userCode);

        MyAdapter myAdapter = new MyAdapter(getContext(), logtype, dateTime, address);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return v;
    }


}