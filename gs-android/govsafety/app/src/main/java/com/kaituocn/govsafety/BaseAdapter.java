package com.kaituocn.govsafety;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public final static int TYPE_CLIENT=1;

    private RecyclerView recyclerView;
    private Context context;
    private int type;

    private List<Object> list = new ArrayList<>();

    public BaseAdapter(Context context) {
        this.context = context;
    }

    public BaseAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public List<Object> getList() {
        return list;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setData(List<? extends Object> objects, boolean isAdd) {
        if (!isAdd) {
            list.clear();
        }
        for (Object object : objects) {
            list.add(object);
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView=recyclerView;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==TYPE_CLIENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_client, parent, false);
            return new ClientViewHolder(view);
        }



        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            holder.updateUI(list.get(position));
    }



    private class ClientViewHolder extends BaseViewHolder {
        private View mView;
        private TextView titleView;

        public ClientViewHolder(View view) {
            super(view);
            mView = view;


        }

        @Override
        public void updateUI(Object object) {



        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }





}
