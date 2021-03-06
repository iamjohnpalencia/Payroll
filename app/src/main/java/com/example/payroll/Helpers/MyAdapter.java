package com.example.payroll.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payroll.R;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<String> getLogType;
    ArrayList<String> getDateTime;
    ArrayList<String> getAddress;
    Context getContext;


    public MyAdapter(Context ct, ArrayList<String> lt, ArrayList<String> dt, ArrayList<String> ad) {

        getContext = ct;
        getLogType = lt;
        getDateTime = dt;
        getAddress = ad;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(getContext);
        View view = inflater.inflate(R.layout.logs_rows, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            holder.mytext1.setText(getLogType.get(position));
            holder.mytext2.setText(getDateTime.get(position));
            holder.mytext3.setText(getAddress.get(position));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return getLogType.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mytext1, mytext2, mytext3;

        public MyViewHolder(@NonNull  View itemView) {
            super(itemView);

            mytext1 = itemView.findViewById(R.id.tv_fillinglogs_type);
            mytext2 = itemView.findViewById(R.id.tv_fillinglogs_date);
            mytext3 = itemView.findViewById(R.id.tv_btmlogs_address);

        }
    }
}
