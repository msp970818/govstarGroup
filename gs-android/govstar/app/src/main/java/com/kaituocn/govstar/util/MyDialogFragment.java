package com.kaituocn.govstar.util;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.kaituocn.govstar.R;

public class MyDialogFragment extends DialogFragment {

    public static MyDialogFragment newInstance(String param) {
        MyDialogFragment fragment = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putString("info", param);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Dialog dialog= getDialog();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view=inflater.inflate(R.layout.layout_dialog_loading, container, false);
        Animation animation=new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1500);
        animation.setFillAfter(true);
        view.findViewById(R.id.loadingView).startAnimation(animation);
        ((TextView)view.findViewById(R.id.infoView)).setText(getArguments().getString("info")==null?"处理中，请稍后…":getArguments().getString("info"));
        return view;
    }


}
