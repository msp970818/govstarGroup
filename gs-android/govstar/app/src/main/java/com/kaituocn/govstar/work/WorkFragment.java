package com.kaituocn.govstar.work;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.event.ChangeHeadEvent;
import com.kaituocn.govstar.main.OnFragmentInteractionListener;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.approval.ApprovalActivity;
import com.kaituocn.govstar.work.cloudmeeting.CloudMeetingActivity;
import com.kaituocn.govstar.work.enterprise.EnterpriseServiceActivity;
import com.kaituocn.govstar.work.rank.RankingActivity;
import com.kaituocn.govstar.work.schedulemanage.ScheduleManageActivity;
import com.kaituocn.govstar.work.supervision.SupervisionActivity;
import com.kaituocn.govstar.work.supervisionnotice.SupervisionNoticeActivity;
import com.kaituocn.govstar.work.visibledata.VisibleDataActivity;
import com.kaituocn.govstar.work.workaccount.WorkAccountActivity;
import com.kaituocn.govstar.work.worklist.WorkListActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement theinterface
 * to handle interaction events.
 */
public class WorkFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    ImageView headView;
    public WorkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_work, container, false);
        TextView titleView=view.findViewById(R.id.titleView);
        titleView.setText("工作中心");
        headView= view.findViewById(R.id.headView);
        headView.setOnClickListener(this);
        Glide.with(this).load(Util.getFileBaseUrl(getContext()) +Constant.userEntity.getPersonInfo().getAvatarUrl())
               .error(R.drawable.t2).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE ).into(headView);
        view.findViewById(R.id.textView19).setOnClickListener(this);
        view.findViewById(R.id.textView20).setOnClickListener(this);
        view.findViewById(R.id.textView21).setOnClickListener(this);
        view.findViewById(R.id.textView22).setOnClickListener(this);
        view.findViewById(R.id.textView23).setOnClickListener(this);
        view.findViewById(R.id.textView24).setOnClickListener(this);
        view.findViewById(R.id.textView25).setOnClickListener(this);
        view.findViewById(R.id.textView26).setOnClickListener(this);
        view.findViewById(R.id.textView27).setOnClickListener(this);
        view.findViewById(R.id.textView28).setOnClickListener(this);
        view.findViewById(R.id.textView29).setOnClickListener(this);

        //62发起全部单位的任务 6发起本单位督查任务 63查看督察任务菜单
        if (Constant.userEntity.getPowerMap().get("62")||Constant.userEntity.getPowerMap().get("6")||(Constant.userEntity.getPowerMap().containsKey("63")&&Constant.userEntity.getPowerMap().get("63"))) {
            view.findViewById(R.id.textView19).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.textView19).setVisibility(View.GONE);
        }
        //19督办通知权限
        if (Constant.userEntity.getPowerMap().get("62")||Constant.userEntity.getPowerMap().get("6")||Constant.userEntity.getPowerMap().get("19")) {
            view.findViewById(R.id.textView20).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.textView20).setVisibility(View.GONE);
        }
        //36工作台账
        if (Constant.userEntity.getPowerMap().containsKey("36")&&Constant.userEntity.getPowerMap().get("36")) {
            view.findViewById(R.id.textView23).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.textView23).setVisibility(View.GONE);
        }
        //59帮扶留言
        if (Constant.userEntity.getPowerMap().containsKey("59")&&Constant.userEntity.getPowerMap().get("59")) {
            view.findViewById(R.id.textView29).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.textView29).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.headView:
                openDrawer();
                break;
            case R.id.textView19:
                intent=new Intent(v.getContext(),SupervisionActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView20:
                intent=new Intent(v.getContext(),SupervisionNoticeActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView21:
                intent=new Intent(v.getContext(),ApprovalActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView22:
                intent=new Intent(v.getContext(),WorkListActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView23:
                intent=new Intent(v.getContext(),WorkAccountActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView24:
                intent=new Intent(v.getContext(),ScheduleManageActivity.class);
                startActivity(intent);
                break;
            case R.id.textView25:
                intent=new Intent(v.getContext(),RankingActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView26:
                intent=new Intent(v.getContext(),VisibleDataActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView27:
                intent=new Intent(v.getContext(),CloudMeetingActivity.class);
                startActivity(intent);
                break;
            case R.id.textView28:
                intent=new Intent(v.getContext(),EnterpriseServiceActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;
            case R.id.textView29:
                intent=new Intent(v.getContext(),HelpActivity.class);
                startActivity(intent);
//                Util.showToast(v.getContext(),"该功能将在近期上线！");
                break;

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void openDrawer() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
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
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


}
