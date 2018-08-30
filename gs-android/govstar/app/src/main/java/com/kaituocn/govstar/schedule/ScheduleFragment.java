package com.kaituocn.govstar.schedule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.event.ChangeHeadEvent;
import com.kaituocn.govstar.main.OnFragmentInteractionListener;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface to handle interaction events.
 */
public class ScheduleFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    OnFragmentInteractionListener mListener;
    PopupWindow window;

    ScheduleTodoFragment todoFragment;
    ScheduleDateFragment dateFragment;

    RadioButton radioButton1;
    RadioButton radioButton2;
    ImageView headView;

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        headView= view.findViewById(R.id.headView);
        headView.setOnClickListener(this);
        Glide.with(this).load(Util.getFileBaseUrl(getContext()) +Constant.userEntity.getPersonInfo().getAvatarUrl())
               .error(R.drawable.t2).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE ).into(headView);

        radioButton1=view.findViewById(R.id.radioButton1);
        radioButton1.setOnCheckedChangeListener(this);
        radioButton2=view.findViewById(R.id.radioButton2);
        radioButton2.setOnCheckedChangeListener(this);

        TextView actionView = view.findViewById(R.id.actionView);
        actionView.setOnClickListener(this);
        actionView.setVisibility(View.VISIBLE);
        actionView.setText("排序");
        actionView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_sort, 0);

        if (todoFragment == null) {
            todoFragment = new ScheduleTodoFragment();
            getChildFragmentManager().beginTransaction().add(R.id.content_frame, todoFragment,"todo").commit();
        }
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        switch (buttonView.getId()) {
            case R.id.radioButton1:
                if (todoFragment == null) {
                    todoFragment = new ScheduleTodoFragment();
                }
                switchConent(dateFragment,todoFragment);
                break;
            case R.id.radioButton2:
                if (dateFragment == null) {
                    dateFragment = new ScheduleDateFragment();
                }
                switchConent(todoFragment,dateFragment);
                break;
        }
    }

    private void switchConent(Fragment from, Fragment to) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (from != null&&from.isAdded()) {
            transaction.hide(from);
        }
        if (!to.isAdded()) {
            String tag="";
            if (to instanceof ScheduleDateFragment) {
                tag="date";
            }else if(to instanceof ScheduleTodoFragment){
                tag="todo";
            }
            transaction.add(R.id.content_frame, to,tag).commit();
        } else {
            transaction.show(to).commit();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.headView:
                openDrawer();
                break;
            case R.id.actionView:
                showPopupWindow(v);
                break;
            case R.id.sortAView:
                if (window != null) {
                    window.dismiss();
                }
                if (radioButton1.isChecked()) {
                    Util.sort( todoFragment.adapter.getList(),2);
                    todoFragment.adapter.notifyDataSetChanged();
                }else if(radioButton2.isChecked()){
                    Util.sort( dateFragment.adapter.getList(),2);
                    dateFragment.adapter.notifyDataSetChanged();
                }
                break;
            case R.id.sortBView:
                if (window != null) {
                    window.dismiss();
                }
                if (radioButton1.isChecked()) {
                    Util.sort( todoFragment.adapter.getList(),1);
                    todoFragment.adapter.notifyDataSetChanged();
                }else if(radioButton2.isChecked()){
                    Util.sort( dateFragment.adapter.getList(),1);
                    dateFragment.adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void openDrawer() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    private void showPopupWindow(View anchor) {
        View contentView = LayoutInflater.from(anchor.getContext()).inflate(R.layout.popup_sort, null, false);
        window = new PopupWindow(initPopupView(contentView), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
//        window.showAsDropDown(anchor,  0,0);
        window.showAtLocation(anchor, Gravity.TOP | Gravity.RIGHT, Util.dp2px(getContext(), 6), Util.dp2px(getContext(), 64));

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.6f;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getActivity().getWindow().setAttributes(lp);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    private View initPopupView(View view) {
        view.findViewById(R.id.sortAView).setOnClickListener(this);
        view.findViewById(R.id.sortBView).setOnClickListener(this);
        view.findViewById(R.id.sortCView).setVisibility(View.GONE);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ChangeHeadEvent event) {
        System.out.println("======================ChangeHeadEvent");
        Glide.with(this).load(Util.getFileBaseUrl(getContext()) +Constant.userEntity.getPersonInfo().getAvatarUrl())
                .error(R.drawable.t2).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE ).into(headView);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        todoFragment=null;
        dateFragment=null;
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }



}
