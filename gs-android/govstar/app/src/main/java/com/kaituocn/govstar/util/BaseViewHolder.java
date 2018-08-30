package com.kaituocn.govstar.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/3/31.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void updateUI(Object object, int flag) {
        updateUI(object);
    }

    public void updateUI(Object object) {

    }

    @Override
    public void onClick(View v) {

    }
}
