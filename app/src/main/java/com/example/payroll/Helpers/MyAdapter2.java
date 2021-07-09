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

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    ArrayList<String> getLogType;
    ArrayList<String> getDateTime;
    Context getContext;

    public MyAdapter2(Context ct, ArrayList<String> lt, ArrayList<String> dt) {
        getContext = ct;
        getLogType = lt;
        getDateTime = dt;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(getContext);
        View view = inflater.inflate(R.layout.logs_filling, parent, false);
        return new MyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        try {
            holder.mytext1.setText(getLogType.get(position));
            holder.mytext2.setText(getDateTime.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return getLogType.size();
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder{

        TextView mytext1, mytext2;

        public MyViewHolder2(@NonNull  View itemView) {
            super(itemView);

            mytext1 = itemView.findViewById(R.id.tv_fillinglogs_type);
            mytext2 = itemView.findViewById(R.id.tv_fillinglogs_date);

        }
    }
}
