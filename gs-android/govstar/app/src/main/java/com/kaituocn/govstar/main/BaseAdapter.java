package com.kaituocn.govstar.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.ApprovalEntity;
import com.kaituocn.govstar.entity.DuBanEntity;
import com.kaituocn.govstar.entity.DuChaEntity;
import com.kaituocn.govstar.entity.FeedbackEntity;
import com.kaituocn.govstar.entity.FeedbackInfoEntity;
import com.kaituocn.govstar.entity.MeetingEntity;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.RankingDeductEntity;
import com.kaituocn.govstar.entity.RankingPersonEntity;
import com.kaituocn.govstar.entity.RankingUnitEntity;
import com.kaituocn.govstar.entity.ScheduleEntity;
import com.kaituocn.govstar.entity.SysNoticeEntity;
import com.kaituocn.govstar.entity.TodoEntity;
import com.kaituocn.govstar.entity.VisibleDataEntity;
import com.kaituocn.govstar.entity.WorkAccountEntity;
import com.kaituocn.govstar.set.FeedbackInfoActivity;
import com.kaituocn.govstar.util.BaseViewHolder;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.MySwipeMenuLayout;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.approval.ApprovalDoActivity;
import com.kaituocn.govstar.work.approval.ApprovalResult1Activity;
import com.kaituocn.govstar.work.approval.ApprovalResult2Activity;
import com.kaituocn.govstar.work.cloudmeeting.MeetingNoticeActivity;
import com.kaituocn.govstar.work.cloudmeeting.MeetingSignUpActivity;
import com.kaituocn.govstar.work.enterprise.SignServiceActivity;
import com.kaituocn.govstar.work.supervisionnotice.CreateInterviewNoticeActivity;
import com.kaituocn.govstar.work.supervisionnotice.CreateSupervisionNoticeActivity;
import com.kaituocn.govstar.work.supervisionnotice.InterviewNoticeStatusActivity;
import com.kaituocn.govstar.work.supervisionnotice.SignInterviewNoticeActivity;
import com.kaituocn.govstar.work.supervisionnotice.SignSupervisionNoticeActivity;
import com.kaituocn.govstar.work.supervisionnotice.SupervisionNoticeStatusActivity;
import com.kaituocn.govstar.work.workaccount.WorkAccountDetailActivity;
import com.kaituocn.govstar.work.worklist.AcceptResultActivity;
import com.kaituocn.govstar.work.worklist.ApplyDelayActivity;
import com.kaituocn.govstar.work.worklist.LookProcessActivity;
import com.kaituocn.govstar.work.worklist.WorkAcceptActivity;
import com.kaituocn.govstar.work.worklist.WorkCorrectedActivity;
import com.kaituocn.govstar.work.worklist.WorkDoActivity;
import com.kaituocn.govstar.work.worklist.WorkDoingActivity;
import com.kaituocn.govstar.work.worklist.WorkReturnActivity;
import com.kaituocn.govstar.yunav.TeamAVChatProfile;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public final static int TYPE_SCHEDULE_TODO = 1;
    public final static int TYPE_SCHEDULE_DATE = 2;
    public final static int TYPE_WROK_WORKLIST = 3;
    public final static int TYPE_WROK_SUPERSIVIONNOTICE = 4;
    public final static int TYPE_WROK_APPROVAL = 5;
    public final static int TYPE_WORK_HELP = 6;
    public final static int TYPE_WORK_ENTERPRISE = 8;
    public final static int TYPE_WORK_CLOUDMEETING = 9;
    public final static int TYPE_WORK_RANKING_UNIT = 10;
    public final static int TYPE_WORK_RANKING_PERSON = 11;
    public final static int TYPE_WORK_RANKING_REDUCTION = 12;
    public final static int TYPE_WORK_VISIBLEDATA_COUNTDATA = 13;
    public final static int TYPE_WROK_SUPERSIVION = 14;
    public final static int TYPE_WROK_ACCOUNT = 15;
    public final static int TYPE_SET_COLLECT = 20;
    public final static int TYPE_SET_SERVICE_NOTICE = 21;
    public final static int TYPE_SET_SERVICE_FEEDBACK = 22;
    public final static int TYPE_SET_SERVICE_FEEDBACK_INFO = 23;

    private RecyclerView recyclerView;
    private Context context;
    private int type;

    private List<Object> list = new ArrayList<>();



    private Map<String,String> scheduleTypeName;

    public BaseAdapter(Context context) {
        this.context = context;
    }

    public BaseAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }
    public Map<String, String> getScheduleTypeName() {
        return scheduleTypeName;
    }

    public void setScheduleTypeName(Map<String, String> scheduleTypeName) {
        this.scheduleTypeName = scheduleTypeName;
        notifyDataSetChanged();
    }

    public List<Object> getList() {
        return list;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setData(List<? extends Object> objects, boolean isAdd) {
        if (objects == null) {
            return;
        }
        if (!isAdd) {
            list.clear();
        }
        for (Object object : objects) {
            list.add(object);
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
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
        if (viewType == TYPE_SCHEDULE_TODO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule_todo, parent, false);
            return new ScheduleTodoViewHolder(view);
        }
        if (viewType == TYPE_SCHEDULE_DATE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule_date, parent, false);
            return new ScheduleDateViewHolder(view);
        }
        if (viewType == TYPE_WROK_WORKLIST) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist_2, parent, false);
            return new WorkWorkListViewHolder(view);
        }
        if (viewType == TYPE_WROK_SUPERSIVIONNOTICE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_supervision_notice, parent, false);
            return new WorkSupervisionNoticeViewHolder(view);
        }
        if (viewType == TYPE_WROK_APPROVAL) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist, parent, false);
            return new WorkApprovalViewHolder(view);
        }
        if (viewType == TYPE_WROK_SUPERSIVION) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist, parent, false);
            return new WorkSupervisonViewHolder(view);
        }

        if (viewType == TYPE_WORK_ENTERPRISE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist, parent, false);
            return new WorkEnterpriseViewHolder(view);
        }
        if (viewType == TYPE_WORK_RANKING_UNIT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_ranking_unit, parent, false);
            return new WorkRankingUnitViewHolder(view);
        }
        if (viewType == TYPE_WORK_RANKING_PERSON) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_ranking_person, parent, false);
            return new WorkRankingPersonViewHolder(view);
        }
        if (viewType == TYPE_WORK_RANKING_REDUCTION) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_ranking_reduction, parent, false);
            return new WorkRankingReductionViewHolder(view);
        }
        if (viewType == TYPE_WORK_VISIBLEDATA_COUNTDATA) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_visibledata_countdata, parent, false);
            return new WorkVisibleDataCountDataViewHolder(view);
        }
        if (viewType == TYPE_WORK_HELP) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_help, parent, false);
            return new WorkHelpViewHolder(view);
        }
        if (viewType == TYPE_WORK_CLOUDMEETING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_supervision_notice, parent, false);
            return new WorkCloudMeetingViewHolder(view);
        }
        if (viewType == TYPE_WROK_ACCOUNT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist, parent, false);
            return new WorkAccountViewHolder(view);
        }
        if (viewType == TYPE_SET_COLLECT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist_2, parent, false);
            return new SetCollectViewHolder(view);
        }
        if (viewType == TYPE_SET_SERVICE_NOTICE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist, parent, false);
            return new SetServiceNoticeViewHolder(view);
        }
        if (viewType == TYPE_SET_SERVICE_FEEDBACK) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_worklist, parent, false);
            return new SetServiceFeedbackViewHolder(view);
        }
        if (viewType == TYPE_SET_SERVICE_FEEDBACK_INFO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_set_feedback, parent, false);
            return new SetFeedbackInfoHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.updateUI(list.get(position));
    }



    private class ScheduleTodoViewHolder extends BaseViewHolder {
        View mView;
        ImageView imageView;
        ImageView flagView;
        ImageView levelView;
        ClipDrawable clipDrawable;
        TextView titleView;
        TextView createDepView;
        TextView departmentView;
        TextView leaderView;
        TextView mainPersonView;
        TextView endTimeView;
        TextView startTimeView;
        TextView typeView;
        TextView remainView;

        TodoEntity entity;


        public ScheduleTodoViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            startTimeView = view.findViewById(R.id.timeView);
            titleView = view.findViewById(R.id.titleView);
            createDepView = view.findViewById(R.id.regionView);
            departmentView = view.findViewById(R.id.textView2);
            leaderView = view.findViewById(R.id.textView3);
            mainPersonView = view.findViewById(R.id.textView4);
            endTimeView = view.findViewById(R.id.textView5);
            typeView = view.findViewById(R.id.textView6);

            remainView = view.findViewById(R.id.textView8);

            imageView = view.findViewById(R.id.imageView);
            flagView = view.findViewById(R.id.flagView);
            levelView = view.findViewById(R.id.levelView);
            clipDrawable = (ClipDrawable) levelView.getDrawable();


        }

        @Override
        public void updateUI(Object object) {
            entity = (TodoEntity) object;
            System.out.println("==========="+entity.getTaskState());
            startTimeView.setText(entity.getCreateTime());
            titleView.setText(entity.getSupervisionTitle());
            createDepView.setText(entity.getCreateComp()+" - "+entity.getCreateDepa());
            departmentView.setText(entity.getDepartment());
            leaderView.setText("牵头领导："+entity.getLeader());
            mainPersonView.setText("主责人："+entity.getFirstLeader());

            flagView.setVisibility(View.GONE);

            String endtime=entity.getEndTime();
            if (endtime.length()>16) {
                endtime=endtime.substring(0,16);
            }
            endTimeView.setText("结束日期："+endtime);
            typeView.setText("类型："+entity.getSupervisionTypeName());

            if (!TextUtils.isEmpty(entity.getProgress())) {
                clipDrawable.setLevel(Integer.parseInt(entity.getProgress()) * 100);
            }
            if (!TextUtils.isEmpty(entity.getTimeout())) {
                remainView.setText("剩余:" + entity.getTimeout() + "天");
                int day=Integer.parseInt(entity.getTimeout());
                if (day>10) {
                    imageView.setImageResource(R.drawable.icon_schedule_supervision_1);
                }else if(day>5){
                    imageView.setImageResource(R.drawable.icon_schedule_supervision_2);
                }else if(day>2){
                    imageView.setImageResource(R.drawable.icon_schedule_supervision_3);
                }else if(day>0){
                    imageView.setImageResource(R.drawable.icon_schedule_supervision_4);
                }else{
//                    imageView.setImageResource(R.drawable.icon_schedule_supervision_5);
//                    remainView.setText("剩余 : 已超时");
                }
            }
            if("2".equals(entity.getTaskState())){
                imageView.setImageResource(R.drawable.icon_schedule_supervision_5);
                remainView.setText("剩余 : 已超时");
                flagView.setVisibility(View.VISIBLE);
            }

//            switch (entity.getId() % 19) {
//                case 0:
//                    imageView.setImageResource(R.drawable.icon_schedule_supervision_1);
//                    break;
//                case 1:
//                    imageView.setImageResource(R.drawable.icon_schedule_supervision_2);
//                    break;
//                case 2:
//                    imageView.setImageResource(R.drawable.icon_schedule_supervision_3);
//                    break;
//                case 3:
//                    imageView.setImageResource(R.drawable.icon_schedule_supervision_4);
//                    break;
//                case 4:
//                    imageView.setImageResource(R.drawable.icon_schedule_supervision_5);
//                    break;
//                case 5:
//                    imageView.setImageResource(R.drawable.icon_schedule_enterprise_1);
//                    break;
//                case 6:
//                    imageView.setImageResource(R.drawable.icon_schedule_enterprise_2);
//                    break;
//                case 7:
//                    imageView.setImageResource(R.drawable.icon_schedule_enterprise_3);
//                    break;
//                case 8:
//                    imageView.setImageResource(R.drawable.icon_schedule_enterprise_4);
//                    break;
//                case 9:
//                    imageView.setImageResource(R.drawable.icon_schedule_enterprise_5);
//                    break;
//                case 10:
//                    imageView.setImageResource(R.drawable.icon_schedule_delay_1);
//                    break;
//                case 11:
//                    imageView.setImageResource(R.drawable.icon_schedule_delay_2);
//                    break;
//                case 12:
//                    imageView.setImageResource(R.drawable.icon_schedule_delay_3);
//                    break;
//                case 13:
//                    imageView.setImageResource(R.drawable.icon_schedule_meeting_1);
//                    break;
//                case 14:
//                    imageView.setImageResource(R.drawable.icon_schedule_meeting_2);
//                    break;
//                case 15:
//                    imageView.setImageResource(R.drawable.icon_schedule_meeting_3);
//                    break;
//                case 16:
//                    imageView.setImageResource(R.drawable.icon_schedule_alert_1);
//                    break;
//                case 17:
//                    imageView.setImageResource(R.drawable.icon_schedule_alert_2);
//                    break;
//                case 18:
//                    imageView.setImageResource(R.drawable.icon_schedule_alert_3);
//                    break;
//            }

        }

        @Override
        public void onClick(View v) {
            if (v==mView) {
                if (entity.getTaskState().equals("2")) {
                    Intent intent=new Intent(context,WorkCorrectedActivity.class);
                    intent.putExtra("taskId",entity.getTaskId());
                    intent.putExtra("id",entity.getId());
                    intent.putExtra("showAction",true);
                    context.startActivity(intent);
                }else if(entity.getTaskState().equals("0")||entity.getTaskState().equals("4")) {
                    Intent intent=new Intent(context,WorkDoActivity.class);
                    intent.putExtra("id",entity.getId());
                    intent.putExtra("taskId",entity.getTaskId());
                    context.startActivity(intent);
                }
            }
        }
    }

    private class ScheduleDateViewHolder extends BaseViewHolder {
        View mView;
        ImageView imageView;
        ImageView levelView;
        ClipDrawable clipDrawable;
        TextView timeView;
        TextView endTimeView;
        TextView titleView;
        TextView startTimeView;
        TextView infoView;
        TextView stateView;
        ScheduleEntity entity;

        public ScheduleDateViewHolder(View view) {
            super(view);
            mView = view;
            timeView = view.findViewById(R.id.timeView);
            titleView = view.findViewById(R.id.titleView);
            startTimeView = view.findViewById(R.id.regionView);
            endTimeView = view.findViewById(R.id.textView7);
            infoView = view.findViewById(R.id.textView2);
            stateView = view.findViewById(R.id.textView8);
            stateView.setOnClickListener(this);
            imageView = view.findViewById(R.id.imageView);
            levelView = view.findViewById(R.id.levelView);
            clipDrawable = (ClipDrawable) levelView.getDrawable();
        }

        @Override
        public void updateUI(Object object) {
            entity = (ScheduleEntity) object;
            if (getAdapterPosition()==0) {
                timeView.setVisibility(View.VISIBLE);
            }else{
                timeView.setVisibility(View.GONE);
            }
            titleView.setText(entity.getScheduleTitle());
            startTimeView.setText("开始：" + entity.getStartTime());
            endTimeView.setText("结束：" + entity.getEndTime());
            infoView.setText(entity.getScheduleDescribe());
            if (entity.getCompleteType() == 1) {
                stateView.setText("已完成");
                stateView.setTextColor(context.getResources().getColor(R.color.item_gray_dark));
                stateView.setEnabled(false);
                infoView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            } else {
                stateView.setText("标记为已完成");
                stateView.setTextColor(context.getResources().getColor(R.color.item_green));
                stateView.setEnabled(true);
                infoView.getPaint().setFlags(0);
            }
            switch (entity.getScheduleType()) {
                case 1:
                    imageView.setImageResource(R.drawable.icon_schedule_date_ys);

                    break;
                case 2:
                    imageView.setImageResource(R.drawable.icon_schedule_date_jd);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.icon_schedule_date_hy);

                    break;
                case 4:
                    imageView.setImageResource(R.drawable.icon_schedule_date_dy);

                    break;
                case 5:
                    imageView.setImageResource(R.drawable.icon_schedule_date_zb);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.icon_schedule_date_px);

                    break;
                case 7:
                    imageView.setImageResource(R.drawable.icon_schedule_date_qt);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textView8:
                    completeSchedule();
                    break;
            }
        }

        private void completeSchedule() {
            RequestUtil.request(new RequestListener() {
                @Override
                public String getUrl() {
                    return "/schedule/complete";
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", entity.getId() + "");
                    return map;
                }

                @Override
                public JSONObject getJsonObj() {
                    return null;
                }

                @Override
                public void onResponse(JSONObject jsonObj) {
                    System.out.println("completeSchedule===========" + jsonObj);
                    try {
                        int code = jsonObj.getInt("code");
                        if (code == 1) {
                            entity.setCompleteType(1);
                            notifyItemChanged(getAdapterPosition());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }
    }

    private class WorkSupervisonViewHolder extends BaseViewHolder {
        View mView;
        View colorView;
        TextView titleView;
        TextView unitView;
        TextView applyTimeView;
        TextView endTimeView;
        TextView statusView;
        ImageView flagView;

        DuChaEntity entity;
        public WorkSupervisonViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            flagView = view.findViewById(R.id.flagView);
            colorView = view.findViewById(R.id.colorView);
            titleView = view.findViewById(R.id.textView34);
            unitView = view.findViewById(R.id.textView35);
            endTimeView = view.findViewById(R.id.textView36);
            applyTimeView = view.findViewById(R.id.textView37);
            statusView = view.findViewById(R.id.textView39);
            ((TextView) view.findViewById(R.id.textView38)).setText("剩余：");

        }

        @Override
        public void updateUI(Object object) {
            entity = (DuChaEntity) object;
            titleView.setText(entity.getSupervisionTitle());
            unitView.setText(entity.getCreateComp());
            String endTime=entity.getEndTime();
            if (endTime.length()>10) {
                endTime=endTime.substring(0,10);
            }
            endTimeView.setText("任务截止日期:"+endTime);
            String receiveTime=entity.getCreateTime();
            if (receiveTime.length()>16) {
                receiveTime=receiveTime.substring(0,16);
            }
            applyTimeView.setText("接收日期:"+receiveTime);

            statusView.setText(entity.getTimeout()+"天");
            flagView.setVisibility(View.GONE);
            int taskState=entity.getTaskState();
            System.out.println("taskState============"+taskState);
            if(taskState<=4){
                int timeOut=Integer.parseInt(entity.getTimeout());
                if (timeOut>10) {
                    Util.setBackgroundTint(colorView,R.color.item_green);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_green));
                }else if(timeOut>5){
                    Util.setBackgroundTint(colorView,R.color.item_yellow);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_yellow));
                }else if(timeOut>2){
                    Util.setBackgroundTint(colorView,R.color.item_orange);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_orange));
                }else if(timeOut>=0){
                    Util.setBackgroundTint(colorView,R.color.item_red);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_red));
                }else {
                    Util.setBackgroundTint(colorView,R.color.item_red);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_red));
                }
            }else if(taskState<=8){
                Util.setBackgroundTint(colorView,R.color.item_blue);
                statusView.setTextColor(context.getResources().getColor(R.color.item_blue));
                if (entity.getIsOverTime()==1){
                    flagView.setVisibility(View.VISIBLE);
                }
                statusView.setText("已完成");

            }else if(taskState==9){
                Util.setBackgroundTint(colorView,R.color.item_gray_light);
                statusView.setTextColor(context.getResources().getColor(R.color.item_gray_light));
                statusView.setText("已超时");
                flagView.setVisibility(View.VISIBLE);
                flagView.setImageResource(R.drawable.icon_supervision_flag_schedule);
            }


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout:
                    int taskState=entity.getTaskState();
                    switch (taskState) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 9:
                            Intent intent=new Intent(context,WorkDoingActivity.class);
                            intent.putExtra("id",entity.getId());
                            intent.putExtra("taskId",entity.getTaskId());
                            intent.putExtra("isOver",taskState==9);
                            context.startActivity(intent);
                            break;
                        case 5:
                            intent=new Intent(context,WorkAcceptActivity.class);
                            intent.putExtra("showAction",true);
                            context.startActivity(intent);
                            break;
                        case 6:
                            intent=new Intent(context,WorkAcceptActivity.class);
                            intent.putExtra("showAction",false);
                            context.startActivity(intent);
                            break;
                        case 7:
                            intent=new Intent(context,AcceptResultActivity.class);
                            intent.putExtra("showAction",true);
                            context.startActivity(intent);
                            break;
                        case 8:
                            intent=new Intent(context,AcceptResultActivity.class);
                            intent.putExtra("showAction",false);
                            context.startActivity(intent);
                            break;

                    }
                    break;
            }
        }
    }

    private class WorkWorkListViewHolder extends BaseViewHolder {
        View mView;
        View layout;
        View colorView;
        View collectView;

        TextView titleView;
        TextView unitView;
        TextView applyTimeView;
        TextView endTimeView;
        TextView statusView;
        ImageView flagView;

        DuChaEntity entity;
        public WorkWorkListViewHolder(View view) {
            super(view);
            mView = view;

            layout = view.findViewById(R.id.layout);
            layout.setOnClickListener(this);

            colorView = view.findViewById(R.id.colorView);

            collectView = view.findViewById(R.id.collectView);
            collectView.setVisibility(View.VISIBLE);
            collectView.setOnClickListener(this);

            flagView = view.findViewById(R.id.flagView);
            titleView = view.findViewById(R.id.textView34);
            unitView = view.findViewById(R.id.textView35);
            endTimeView = view.findViewById(R.id.textView36);
            applyTimeView = view.findViewById(R.id.textView37);
            statusView = view.findViewById(R.id.textView39);
        }

        @Override
        public void updateUI(Object object) {
            entity = (DuChaEntity) object;
            titleView.setText(entity.getSupervisionTitle());
            unitView.setText(entity.getCreateComp());
            String endTime=entity.getEndTime();
            if (endTime.length()>10) {
                endTime=endTime.substring(0,10);
            }
            endTimeView.setText("任务截止日期:"+endTime);
            String receiveTime=entity.getCreateTime();
            if (receiveTime.length()>16) {
                receiveTime=receiveTime.substring(0,16);
            }
            applyTimeView.setText("接收日期:"+receiveTime);
            statusView.setText(entity.getTimeout()+"天");
            flagView.setVisibility(View.GONE);

            int taskState=entity.getTaskState();
            System.out.println("taskState============"+taskState);
            if(taskState==0||taskState==4){
                int timeOut=Integer.parseInt(entity.getTimeout());
                if (timeOut>10) {
                    Util.setBackgroundTint(colorView,R.color.item_green);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_green));
                }else if(timeOut>5){
                    Util.setBackgroundTint(colorView,R.color.item_yellow);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_yellow));
                }else if(timeOut>2){
                    Util.setBackgroundTint(colorView,R.color.item_orange);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_orange));
                }else if(timeOut>=0){
                    Util.setBackgroundTint(colorView,R.color.item_red);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_red));
                }
            }else if(taskState==2){
                Util.setBackgroundTint(colorView,R.color.item_red);
                statusView.setTextColor(context.getResources().getColor(R.color.item_red));
                statusView.setText("需提交整改说明");
                flagView.setVisibility(View.VISIBLE);
                flagView.setImageResource(R.drawable.icon_supervision_flag_schedule);
            }else if(taskState==31){
                Util.setBackgroundTint(colorView,R.color.item_gray_light);
                statusView.setTextColor(context.getResources().getColor(R.color.item_gray_light));
                statusView.setText("整改说明审核中");
                flagView.setVisibility(View.VISIBLE);
                flagView.setImageResource(R.drawable.icon_supervision_flag_schedule);
            }else if(taskState==5){
                statusView.setText("完成");
                if (entity.getIsCheck()==0) {
                    Util.setBackgroundTint(colorView,R.color.item_blue);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_blue));
                }else if(entity.getIsCheck()==1){
                    Util.setBackgroundTint(colorView,R.color.item_orange);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_orange));
                }
                if (entity.getIsOverTime()==1) {
                    flagView.setVisibility(View.VISIBLE);
                }else{
                    flagView.setVisibility(View.GONE);
                }
            }else if(taskState==6){
                Util.setBackgroundTint(colorView,R.color.item_blue_light);
                statusView.setTextColor(context.getResources().getColor(R.color.item_blue_light));
                statusView.setText("已处理");
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.collectView:
//                    Util.showToast(v.getContext(), "收藏");
                    collect();
                    break;
                case R.id.layout:
                    int taskState=entity.getTaskState();
                    switch (taskState) {
                        case 0:
                        case 4://工作办理
                            Intent intent = new Intent(v.getContext(), WorkDoActivity.class);
                            intent.putExtra("id",entity.getId());
                            intent.putExtra("taskId",entity.getTaskId());
                            context.startActivity(intent);
                            break;
                        case 2://整改说明
                            intent = new Intent(v.getContext(), WorkCorrectedActivity.class);
                            intent.putExtra("taskId",entity.getTaskId());
                            intent.putExtra("id",entity.getId());
                            intent.putExtra("showAction",true);
                            context.startActivity(intent);
                            break;

                        case 5:
                        case 6:
                            intent = new Intent(v.getContext(), WorkDoingActivity.class);
                            intent.putExtra("id",entity.getId());
                            intent.putExtra("taskId",entity.getTaskId());
                            intent.putExtra("title",taskState==5?"已完成":"已处理");
                            context.startActivity(intent);
                            break;

                    }

                    break;

            }
        }


        private void collect(){
            RequestUtil.jsonObjectRequest(new RequestListener() {
                @Override
                public String getUrl() {
                    return "/userCollection/addUserCollection";
                }

                @Override
                public Map<String, String> getParams() {
                    return null;
                }

                @Override
                public JSONObject getJsonObj() {
                    JSONObject object=new JSONObject();
                    try {
                        object.putOpt("id",entity.getId());
                        object.putOpt("taskId",entity.getTaskId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object;
                }

                @Override
                public void onResponse(JSONObject jsonObj) {
                    System.out.println("collect============="+jsonObj);
                    try {
                        int code=jsonObj.getInt("code");
                        if (code==1) {
                            ((SwipeMenuLayout) mView).quickClose();
                            Util.showToast(context, "收藏成功");
                        }else{
                            Util.showToast(context, "收藏失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }
    }


    private class WorkSupervisionNoticeViewHolder extends BaseViewHolder {
        View mView;
        View colorView;
        TextView titleView;
        TextView timeView;
        TextView companyView;
        TextView mainPersonView;
        TextView transactorView;
        TextView stateView;
        DuBanEntity entity;

        public WorkSupervisionNoticeViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            colorView = view.findViewById(R.id.colorView);
            titleView = view.findViewById(R.id.textView34);
            companyView = view.findViewById(R.id.textView35);
            mainPersonView = view.findViewById(R.id.textView36);
            transactorView = view.findViewById(R.id.textView42);
            timeView = view.findViewById(R.id.textView37);
            stateView = view.findViewById(R.id.textView39);

        }

        @Override
        public void updateUI(Object object) {
            entity = (DuBanEntity) object;
            titleView.setText(entity.getSupervisionTitle());
            companyView.setText("主责单位：" + entity.getMainCompany());
            mainPersonView.setText("主责人：" + entity.getMainName());
            transactorView.setText("办理人：" + entity.getTransactor());
            timeView.setText("截止日期：" + entity.getEndTime());
            stateView.setText(getStateName(entity.getTaskState()));
            switch (entity.getTaskState()) {
                case "4":
                case "5":
                case "11":
                    Util.setBackgroundTint(colorView, R.color.text_black);
                    break;
                case "7":
                case "8":
                case "12":
                    Util.setBackgroundTint(colorView, R.color.tag_red);
                    break;
                default:
                    Util.setBackgroundTint(colorView, R.color.item_blue);
                    break;

            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.layout) {
                Intent intent = null;
                switch (entity.getTaskState()) {
                    case "4":
                        intent = new Intent(context, CreateSupervisionNoticeActivity.class);
                        break;
                    case "5":
                    case "6":
                        intent = new Intent(context, SupervisionNoticeStatusActivity.class);
                        break;
                    case "7":
                        intent = new Intent(context, CreateInterviewNoticeActivity.class);
                        break;
                    case "8":
                    case "9":
                        intent = new Intent(context, InterviewNoticeStatusActivity.class);
                        break;
                    case "11":
                        intent = new Intent(context, SignSupervisionNoticeActivity.class);
                        break;
                    case "12":
                        intent = new Intent(context, SignInterviewNoticeActivity.class);
                        break;
                    case "13":
                        intent = new Intent(context, SignSupervisionNoticeActivity.class);
                        intent.putExtra("type", 1);
                        break;
                    case "14":
                        intent = new Intent(context, SignInterviewNoticeActivity.class);
                        intent.putExtra("type", 1);
                        break;
                }
                if (intent != null) {
                    intent.putExtra("supervisionId", entity.getSupervisionId());
                    ((Activity) context).startActivityForResult(intent, 1);
                }

            }
        }

        private String getStateName(String state) {
            if (state == null) {
                return "";
            }
            String name = null;
            switch (state) {
                case "4":
                    name = "已逾期";
                    break;
                case "5":
                    name = "督办中";
                    break;
                case "6":
                    name = "已签收";
                    break;
                case "7":
                    name = "可约谈";
                    break;
                case "8":
                    name = "约谈中";
                    break;
                case "9":
                    name = "已约谈";
                    break;
                case "10":
                    name = "已完成";
                    break;
                case "11":
                    name = "督办单";
                    break;
                case "12":
                    name = "被约谈";
                    break;
                case "13":
                    name = "已签收";
                    break;
                case "14":
                    name = "已签收";
                    break;
            }
            return name;
        }
    }


    private class WorkApprovalViewHolder extends BaseViewHolder {
        View mView;
        View colorView;
        TextView titleView;
        TextView unitView;
        TextView applyTimeView;
        TextView endTimeView;
        TextView statusView;
        ApprovalEntity entity;

        public WorkApprovalViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            colorView = view.findViewById(R.id.colorView);
            titleView = view.findViewById(R.id.textView34);
            unitView = view.findViewById(R.id.textView35);
            endTimeView = view.findViewById(R.id.textView36);
            applyTimeView = view.findViewById(R.id.textView37);
            statusView = view.findViewById(R.id.textView39);


            ((TextView) view.findViewById(R.id.textView38)).setText("状态：");
        }

        @Override
        public void updateUI(Object object) {
            entity = (ApprovalEntity) object;
            titleView.setText(entity.getDelayTitle());
            unitView.setText("发起单位："+entity.getFoundersCompanyName());
            applyTimeView.setText("接收日期:"+Util.stempToStr(entity.getApplyTime(),null));
            if (entity.getResultState()==4) {
                endTimeView.setText("任务截止日期:"+Util.stempToStr(entity.getDelayTime(),"yyyy-MM-dd"));
            }else{
                if (!TextUtils.isEmpty(entity.getAskedCompleteTime())) {
                    endTimeView.setText("任务截止日期:"+Util.stempToStr(Long.parseLong(entity.getAskedCompleteTime()),"yyyy-MM-dd"));
                }else{
                    endTimeView.setText("");
                }
            }
            if (entity.getPendingState()==2|| entity.getPendingState()==3) {
                statusView.setText("等待审批");
                statusView.setTextColor(context.getResources().getColor(R.color.text_black));
                Util.setBackgroundTint(colorView,R.color.item_green);
            }else if (entity.getResultState()==4) {
                statusView.setText("同意");
                statusView.setTextColor(context.getResources().getColor(R.color.item_green));
                Util.setBackgroundTint(colorView,R.color.item_blue);
            }else if(entity.getResultState()==5){
                statusView.setText("不同意");
                statusView.setTextColor(context.getResources().getColor(R.color.item_red));
                Util.setBackgroundTint(colorView,R.color.item_blue);
            }


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout:

                    if (entity.getPendingState()==2) {
                        Intent intent = new Intent(context, ApprovalDoActivity.class);
                        intent.putExtra("entity",entity);
                        ((Activity)context).startActivityForResult(intent,1);
                    }else  if (entity.getPendingState()==3) {
//                        Intent intent = new Intent(context, ApprovalDoActivity.class);
//                        intent.putExtra("entity",entity);
//                        ((Activity)context).startActivityForResult(intent,1);
                    }else if(entity.getResultState()==4){
                        if (entity.getApproverId()== Integer.parseInt(TeamAVChatProfile.getUserId())) {
                            Intent intent = new Intent(context, ApprovalResult1Activity.class);
                            intent.putExtra("type",1);//同意
                            intent.putExtra("entity",entity);
                            context.startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, ApprovalResult2Activity.class);
                            intent.putExtra("type",1);//同意
                            intent.putExtra("entity",entity);
                            context.startActivity(intent);
                        }
                    }else if (entity.getResultState()==5){
                        if (entity.getApproverId()== Integer.parseInt(TeamAVChatProfile.getUserId())) {
                            Intent intent = new Intent(context, ApprovalResult1Activity.class);
                            intent.putExtra("entity",entity);
                            context.startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, ApprovalResult2Activity.class);
                            intent.putExtra("entity",entity);
                            context.startActivity(intent);
                        }
                    }

                    break;

            }
        }
    }


    private class WorkEnterpriseViewHolder extends BaseViewHolder {
        private final View mView;
        private final TextView textView;

        public WorkEnterpriseViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            textView = view.findViewById(R.id.textView73);

        }

        @Override
        public void updateUI(Object object) {
            MyEntity entity = (MyEntity) object;

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout:
//                    int index = getAdapterPosition();
//                    if (index == 0) {
//                        Intent intent = new Intent(v.getContext(), WorkReturnActivity.class);
//                        context.startActivity(intent);
//                    }
//                    if (index == 1) {
//                        Intent intent = new Intent(v.getContext(), ApplyDelayActivity.class);
//                        context.startActivity(intent);
//                    }
//                    if (index == 2) {
//                        Intent intent = new Intent(v.getContext(), LookProcessActivity.class);
//                        context.startActivity(intent);
//                    }
//                    if (index == 3) {
//                        Intent intent = new Intent(v.getContext(), SignServiceActivity.class);
//                        context.startActivity(intent);
//                    }
                    break;
            }
        }
    }

    private class WorkCloudMeetingViewHolder extends BaseViewHolder {
        View mView;
        View colorView;
        TextView titleView;
        TextView initiatorView;
        TextView meetingTypeView;
        TextView dateView;
        TextView stateView;

        MeetingEntity entity;

        public WorkCloudMeetingViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            colorView = view.findViewById(R.id.colorView);
            view.findViewById(R.id.textView42).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.textView38)).setText("状态：");
            titleView = view.findViewById(R.id.textView34);
            initiatorView = view.findViewById(R.id.textView35);
            meetingTypeView = view.findViewById(R.id.textView36);
            dateView = view.findViewById(R.id.textView37);
            stateView = view.findViewById(R.id.textView39);

        }

        @Override
        public void updateUI(Object object) {
            entity = (MeetingEntity) object;
            titleView.setText(entity.getMeetingTitile());
            initiatorView.setText("发起人：" + entity.getCreateName());

            System.out.println("=============" + entity.getMeetingTitile() + "    " + entity.getShowState());

            switch (entity.getMettingType()) {
                case 1:
                    meetingTypeView.setText("会议类型：现场会议");
                    break;
                case 2:
                    meetingTypeView.setText("会议类型：音视频通话");
                    break;
                case 3:
                    meetingTypeView.setText("会议类型：直播会议");
                    break;
            }


            if (entity.getMettingState() == 1) {
                if (entity.getShowState() == 1) {
                    stateView.setText("等待报名");
                    Util.setBackgroundTint(colorView, R.color.item_green);
                } else {
                    if (entity.getShowState() == 7) {
                        stateView.setText("请假");
                    } else {
                        stateView.setText("进行中");
                    }
                    Util.setBackgroundTint(colorView, R.color.item_blue_light);
                }

                dateView.setText("开始日期：" + Util.stempToStr(entity.getMettringStartTime(), null));
            } else {

                stateView.setText("已结束");
                Util.setBackgroundTint(colorView, R.color.item_blue);

                dateView.setText("结束日期：" + Util.stempToStr(entity.getEndTime(), null));

            }


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout:
                    if (entity.getMettingState() == 1) {
                        switch (entity.getShowState()) {
                            case 1://会议报名
                                Intent intent = new Intent(context, MeetingSignUpActivity.class);
                                intent.putExtra("titleName", "会议报名");
                                intent.putExtra("type", 1);
                                intent.putExtra("entity", entity);
                                ((Activity) context).startActivityForResult(intent, 1);
                                break;
                            case 2:
                            case 5:
                            case 33:
                                intent = new Intent(context, MeetingSignUpActivity.class);
                                intent.putExtra("titleName", "进入网络会议室");
                                intent.putExtra("type", 2);
                                intent.putExtra("entity", entity);
                                intent.putExtra("click", entity.getShowState() == 2);
                                ((Activity) context).startActivityForResult(intent, 1);
                                break;
                            case 3:
                                intent = new Intent(context, MeetingSignUpActivity.class);
                                intent.putExtra("titleName", "会议签到");
                                intent.putExtra("type", 3);
                                intent.putExtra("entity", entity);
                                intent.putExtra("click", entity.getSingState() == 2);
                                ((Activity) context).startActivityForResult(intent, 1);
                                break;
                            case 4:
                                intent = new Intent(context, MeetingNoticeActivity.class);
                                intent.putExtra("type", 1);
                                intent.putExtra("titleName", "会议通知中");
                                intent.putExtra("meetingId", entity.getId());
                                intent.putExtra("meetingType", entity.getMettingType());
                                ((Activity) context).startActivityForResult(intent, 1);
                                break;

                        }

                    }

                    //现场会议详情页
                    if (entity.getMettingType() == 1 && entity.getShowState() == 44) {
                        Intent intent = new Intent(context, MeetingSignUpActivity.class);
                        intent.putExtra("titleName", "会议详情");
                        intent.putExtra("type", 6);
                        context.startActivity(intent);
                        break;
                    }

                    //直播结束
                    if (entity.getMettingState() == 2) {
                        Intent intent = new Intent(context, MeetingSignUpActivity.class);
                        intent.putExtra("titleName", "会议详情");
                        intent.putExtra("type", 4);
                        intent.putExtra("entity", entity);
                        context.startActivity(intent);
                        break;
                    }

                    break;

            }
        }
    }

    private class WorkAccountViewHolder extends BaseViewHolder {
        View mView;
        View colorView;
        TextView titleView;
        TextView unitView;
        TextView startTimeView;
        TextView endTimeView;

        WorkAccountEntity entity;
        public WorkAccountViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            colorView = view.findViewById(R.id.colorView);
            titleView = view.findViewById(R.id.textView34);
            unitView = view.findViewById(R.id.textView35);
            startTimeView = view.findViewById(R.id.textView36);
            endTimeView = view.findViewById(R.id.textView37);

            view.findViewById(R.id.textView39).setVisibility(View.GONE);
            view.findViewById(R.id.textView38).setVisibility(View.GONE);

        }

        @Override
        public void updateUI(Object object) {
            entity = (WorkAccountEntity) object;
            titleView.setText(entity.getSupervisionTitle());
            unitView.setText("所属单位："+entity.getCompName());
            startTimeView.setText("发起日期:"+entity.getCreateTime());

            if (TextUtils.isEmpty(entity.getEndActualTime())) {
                endTimeView.setVisibility(View.GONE);
                Util.setBackgroundTint(colorView,R.color.item_blue_light);
            }else{
                endTimeView.setVisibility(View.VISIBLE);
                endTimeView.setText("完成日期:"+entity.getEndActualTime());
                Util.setBackgroundTint(colorView,R.color.item_blue);
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout:
                    Intent intent=new Intent(context, WorkAccountDetailActivity.class);
                    intent.putExtra("id",entity.getSupervisionId());
                    intent.putExtra("title",entity.getSupervisionTitle());
                    context.startActivity(intent);
                    break;
            }
        }
    }

    private class WorkRankingUnitViewHolder extends BaseViewHolder {
        View mView;
        TextView rankingView;
        TextView titleView;
        ImageView imageView;
        RankingUnitEntity entity;
        public WorkRankingUnitViewHolder(View view) {
            super(view);
            mView = view;
            rankingView = view.findViewById(R.id.rankingView);
            titleView = view.findViewById(R.id.textView118);
            imageView = view.findViewById(R.id.imageView);

        }

        @Override
        public void updateUI(Object object) {
            entity = (RankingUnitEntity) object;
            rankingView.setText(entity.getInitScore()+".");
            titleView.setText(entity.getCompanyName());
            ((TextView)mView.findViewById(R.id.textView122)).setText("工作量："+entity.getWorkNum());
            ((TextView)mView.findViewById(R.id.textView123)).setText("效率："+entity.getEfficiency()+"天/任务");
            ((TextView)mView.findViewById(R.id.textView124)).setText("综合："+entity.getScore());
            imageView.setVisibility(View.VISIBLE);
            switch (entity.getInitScore()) {
                case 1:
                    imageView.setImageResource(R.drawable.icon_work_ranking_1);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.icon_work_ranking_2);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.icon_work_ranking_3);
                    break;
                default:
                    imageView.setVisibility(View.GONE);
                    break;
            }

        }
    }

    private class WorkRankingPersonViewHolder extends BaseViewHolder {
        View mView;
        TextView textView;
        TextView zanNumView;
        ImageView imageView;
        ImageView zanView;
        ImageView headView;
        RankingPersonEntity entity;
        public WorkRankingPersonViewHolder(View view) {
            super(view);
            mView = view;
            textView = view.findViewById(R.id.textView73);
            zanNumView = view.findViewById(R.id.zanNumView);
            imageView = view.findViewById(R.id.imageView);
            zanView = view.findViewById(R.id.zanView);
            zanView.setOnClickListener(this);
            headView = view.findViewById(R.id.headView);

        }

        @Override
        public void updateUI(Object object) {
            entity = (RankingPersonEntity) object;
            Glide.with(context).load(Util.getFileBaseUrl(context) +entity.getAvatarUrl())
                    .error(R.drawable.t2).skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE ).into(headView);
            ((TextView)mView.findViewById(R.id.textView118)).setText(entity.getInitScore()+"."+entity.getUserName()+" - ");
            ((TextView)mView.findViewById(R.id.textView125)).setText(entity.getCompanyName());
            ((TextView)mView.findViewById(R.id.textView122)).setText("任务量："+entity.getWorkNum());
            ((TextView)mView.findViewById(R.id.textView123)).setText("评分："+entity.getScore());
            ((TextView)mView.findViewById(R.id.textView123)).setText("时效："+entity.getEfficiency());
            zanNumView.setText(entity.getLikeNum()+"");
            if("1".equals(entity.getLike())){
                zanNumView.setVisibility(View.VISIBLE);
                zanView.setVisibility(View.GONE);

            }else{
                zanNumView.setVisibility(View.GONE);
                zanView.setVisibility(View.VISIBLE);
            }
            imageView.setVisibility(View.VISIBLE);
            switch (entity.getInitScore()) {
                case 1:
                    imageView.setImageResource(R.drawable.icon_work_ranking_1);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.icon_work_ranking_2);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.icon_work_ranking_3);
                    break;
                default:
                    imageView.setVisibility(View.GONE);
                    break;
            }

        }

        @Override
        public void onClick(View v) {
           if(v==zanView){
               sendZan();
           }
        }

        private void sendZan(){
            RequestUtil.request(new RequestListener() {
                @Override
                public String getUrl() {
                    return "/Score/easyLike";
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String,String> map= new HashMap<>();
                    map.put("id",entity.getId()+"");
                    return map;
                }

                @Override
                public JSONObject getJsonObj() {
                    return null;
                }

                @Override
                public void onResponse(JSONObject jsonObj) {
                    System.out.println("sendZan=========="+jsonObj);
                    try {
                        int code=jsonObj.getInt("code");
                        if (code==1) {
                            entity.setLike("1");
                            entity.setLikeNum(entity.getLikeNum()+1);
                            notifyItemChanged(getAdapterPosition());
                        }else {
                            Util.showToast(context,jsonObj.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Util.showToast(context,context.getString(R.string.error_server));
                }
            });
        }
    }

    private class WorkRankingReductionViewHolder extends BaseViewHolder {
        View mView;
        TextView timeView;
        RankingDeductEntity entity;

        public WorkRankingReductionViewHolder(View view) {
            super(view);
            mView = view;
            timeView = view.findViewById(R.id.textView127);

        }

        @Override
        public void updateUI(Object object) {
            entity = (RankingDeductEntity) object;
            timeView.setText(Util.stempToStr(entity.getCreateTime(),null));
            ((TextView) mView.findViewById(R.id.textView128)).setText("被处罚："+entity.getUserName());
            ((TextView) mView.findViewById(R.id.textView129)).setText("扣分："+entity.getDeductTheScore());
            ((TextView) mView.findViewById(R.id.textView130)).setText("标题："+entity.getTaskTitle());
            ((TextView) mView.findViewById(R.id.textView131)).setText(entity.getReason());
            ((TextView) mView.findViewById(R.id.textView132)).setText("编号："+entity.getTaskNum());
            ((TextView) mView.findViewById(R.id.textView132)).setText("来自："+entity.getCompanyName());
        }
    }

    private class WorkVisibleDataCountDataViewHolder extends BaseViewHolder {
        View mView;
        TextView textView1,textView2,textView3;
        VisibleDataEntity entity;
        public WorkVisibleDataCountDataViewHolder(View view) {
            super(view);
            mView = view;
            textView1 = view.findViewById(R.id.textView138);
            textView2 = view.findViewById(R.id.textView139);
            textView3 = view.findViewById(R.id.textView140);

        }

        @Override
        public void updateUI(Object object) {
            entity = (VisibleDataEntity) object;
            textView1.setText(entity.getAllTaskNum());
            textView2.setText(entity.getCompleteTaskNum());
            textView3.setText(entity.getDelayTaskNum());
        }
    }

    private class WorkHelpViewHolder extends BaseViewHolder {
        private final View mView;
        private final TextView textView;

        public WorkHelpViewHolder(View view) {
            super(view);
            mView = view;
            textView = view.findViewById(R.id.textView73);

        }

        @Override
        public void updateUI(Object object) {
            MyEntity entity = (MyEntity) object;

        }
    }


    private class SetServiceNoticeViewHolder extends BaseViewHolder {
        View mView;
        TextView titieView,infoView,personView,timeView;
        SysNoticeEntity entity;
        public SetServiceNoticeViewHolder(View view) {
            super(view);
            mView = view;
            view.findViewById(R.id.textView38).setVisibility(View.GONE);
            view.findViewById(R.id.textView39).setVisibility(View.GONE);
            titieView=view.findViewById(R.id.textView34);
            infoView=view.findViewById(R.id.textView35);
            personView=view.findViewById(R.id.textView36);
            timeView=view.findViewById(R.id.textView37);
        }

        @Override
        public void updateUI(Object object) {
            entity = (SysNoticeEntity) object;
            titieView.setText(entity.getPushTitle());
            infoView.setText(entity.getPushContent());
            personView.setText("推送人："+entity.getCreateUserName());
            timeView.setText("提交日期："+Util.stempToStr(entity.getCreateTime(),null));
        }
    }

    private class SetServiceFeedbackViewHolder extends BaseViewHolder {
        View mView;
        TextView titleView;
        TextView personView;
        TextView timeView;
        FeedbackEntity entity;
        public SetServiceFeedbackViewHolder(View view) {
            super(view);
            mView = view;
            mView.setOnClickListener(this);
            titleView = view.findViewById(R.id.textView34);
            personView = view.findViewById(R.id.textView36);
            timeView = view.findViewById(R.id.textView37);
            view.findViewById(R.id.textView35).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.textView38).setVisibility(View.GONE);
            view.findViewById(R.id.textView39).setVisibility(View.GONE);

        }

        @Override
        public void updateUI(Object object) {
            entity = (FeedbackEntity) object;
            titleView.setText(entity.getQuestion());
            personView.setText("回复人："+entity.getAnswerUser());
            timeView.setText("提交日期："+entity.getCommitTime());

        }

        @Override
        public void onClick(View v) {
            if (v==mView) {
                Intent intent=new Intent(v.getContext(), FeedbackInfoActivity.class);
                intent.putExtra("id",entity.getId());
                context.startActivity(intent);
            }
        }
    }

    private class SetFeedbackInfoHolder extends BaseViewHolder {
        View mView;
        TextView questionView;
        TextView questionUser;
        TextView questionTime;
        TextView answerView;
        TextView answerUser;
        TextView answerTime;

        View answerLayout;

        FeedbackInfoEntity entity;

        public SetFeedbackInfoHolder(View view) {
            super(view);
            mView = view;
            questionView = view.findViewById(R.id.questionView);
            questionUser = view.findViewById(R.id.questionUser);
            questionTime = view.findViewById(R.id.questionTime);
            answerView = view.findViewById(R.id.answerView);
            answerUser = view.findViewById(R.id.answerUser);
            answerTime = view.findViewById(R.id.answerTime);
            answerLayout = view.findViewById(R.id.answerLayout);

        }

        @Override
        public void updateUI(Object object) {
            entity = (FeedbackInfoEntity) object;

            questionView.setText(entity.getQuestion());
            questionUser.setText("提问："+entity.getQuestionUser());
            questionTime.setText(entity.getQuestionTime());

            answerView.setText(entity.getAnswer());
            answerUser.setText("回复："+entity.getAnswerUser());
            answerTime.setText(entity.getAnswerTime());

            if (TextUtils.isEmpty(entity.getAnswerUser())) {
                answerLayout.setVisibility(View.GONE);
            }else{
                answerLayout.setVisibility(View.GONE);
            }

        }
    }

    private class SetCollectViewHolder extends BaseViewHolder {

        View iv_top;
        View iv_del;
        View deleteView;

        View mView;
        View layout;
        View colorView;

        TextView titleView;
        TextView unitView;
        TextView applyTimeView;
        TextView endTimeView;
        TextView statusView;
        ImageView flagView;

        DuChaEntity entity;


        public SetCollectViewHolder(View view) {
            super(view);
            mView = view;
            ((MySwipeMenuLayout) mView).setListener(new MySwipeMenuLayout.OnSwipeMenuListener() {
                @Override
                public void onClose() {
                    iv_top.setVisibility(View.VISIBLE);
                    iv_del.setVisibility(View.VISIBLE);
                    deleteView.setVisibility(View.GONE);
                }
            });

            iv_top = view.findViewById(R.id.iv_top);
            iv_del = view.findViewById(R.id.iv_del);
            deleteView = view.findViewById(R.id.deleteView);

            iv_top.setVisibility(View.VISIBLE);
            iv_del.setVisibility(View.VISIBLE);

            iv_top.setOnClickListener(this);
            iv_del.setOnClickListener(this);
            deleteView.setOnClickListener(this);

            layout = view.findViewById(R.id.layout);
            layout.setOnClickListener(this);
            colorView = view.findViewById(R.id.colorView);

            flagView = view.findViewById(R.id.flagView);
            titleView = view.findViewById(R.id.textView34);
            unitView = view.findViewById(R.id.textView35);
            endTimeView = view.findViewById(R.id.textView36);
            applyTimeView = view.findViewById(R.id.textView37);
            statusView = view.findViewById(R.id.textView39);

        }

        @Override
        public void updateUI(Object object) {
            entity = (DuChaEntity) object;
            titleView.setText(entity.getSupervisionTitle());
            unitView.setText(entity.getCreateComp());
            String endTime=entity.getEndTime();
            if (endTime.length()>10) {
                endTime=endTime.substring(0,10);
            }
            endTimeView.setText("任务截止日期:"+endTime);
            String receiveTime=entity.getCreateTime();
            if (receiveTime.length()>16) {
                receiveTime=receiveTime.substring(0,16);
            }
            applyTimeView.setText("接收日期:"+receiveTime);
            statusView.setText(entity.getTimeout()+"天");
            flagView.setVisibility(View.GONE);

            int taskState=entity.getTaskState();
            System.out.println("taskState============"+taskState);
            if(taskState==0||taskState==4){
                int timeOut=Integer.parseInt(entity.getTimeout());
                if (timeOut>10) {
                    Util.setBackgroundTint(colorView,R.color.item_green);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_green));
                }else if(timeOut>5){
                    Util.setBackgroundTint(colorView,R.color.item_yellow);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_yellow));
                }else if(timeOut>2){
                    Util.setBackgroundTint(colorView,R.color.item_orange);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_orange));
                }else if(timeOut>=0){
                    Util.setBackgroundTint(colorView,R.color.item_red);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_red));
                }
            }else if(taskState==2){
                Util.setBackgroundTint(colorView,R.color.item_red);
                statusView.setTextColor(context.getResources().getColor(R.color.item_red));
                statusView.setText("需提交整改说明");
                flagView.setVisibility(View.VISIBLE);
                flagView.setImageResource(R.drawable.icon_supervision_flag_schedule);
            }else if(taskState==31){
                Util.setBackgroundTint(colorView,R.color.item_gray_light);
                statusView.setTextColor(context.getResources().getColor(R.color.item_gray_light));
                statusView.setText("整改说明审核中");
                flagView.setVisibility(View.VISIBLE);
                flagView.setImageResource(R.drawable.icon_supervision_flag_schedule);
            }else if(taskState==5){
                statusView.setText("完成");
                if (entity.getIsCheck()==0) {
                    Util.setBackgroundTint(colorView,R.color.item_blue);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_blue));
                }else if(entity.getIsCheck()==1){
                    Util.setBackgroundTint(colorView,R.color.item_orange);
                    statusView.setTextColor(context.getResources().getColor(R.color.item_orange));
                }
                if (entity.getIsOverTime()==1) {
                    flagView.setVisibility(View.VISIBLE);
                }else{
                    flagView.setVisibility(View.GONE);
                }
            }else if(taskState==6){
                Util.setBackgroundTint(colorView,R.color.item_blue_light);
                statusView.setTextColor(context.getResources().getColor(R.color.item_blue_light));
                statusView.setText("已处理");
            }
        }

        @Override
        public void onClick(View v) {
            int index;
            switch (v.getId()) {
                case R.id.iv_top:
                    top();
                    break;
                case R.id.iv_del:
                    iv_top.setVisibility(View.GONE);
                    iv_del.setVisibility(View.GONE);
                    deleteView.setVisibility(View.VISIBLE);
                    break;
                case R.id.deleteView:
                    delete();
                    break;

                case R.id.layout:
                    int taskState=entity.getTaskState();
                    switch (taskState) {
                        case 0:
                        case 4://工作办理
                            Intent intent = new Intent(v.getContext(), WorkDoActivity.class);
                            intent.putExtra("id",entity.getId());
                            intent.putExtra("taskId",entity.getTaskId());
                            context.startActivity(intent);
                            break;
                        case 2://整改说明
                            intent = new Intent(v.getContext(), WorkCorrectedActivity.class);
                            intent.putExtra("taskId",entity.getTaskId());
                            intent.putExtra("id",entity.getId());
                            intent.putExtra("showAction",true);
                            context.startActivity(intent);
                            break;

                        case 5:
                        case 6:
                            intent = new Intent(v.getContext(), WorkDoingActivity.class);
                            intent.putExtra("id",entity.getId());
                            intent.putExtra("taskId",entity.getTaskId());
                            intent.putExtra("title",taskState==5?"已完成":"已处理");
                            context.startActivity(intent);
                            break;

                    }
                    break;

            }
        }

        private void top(){
            RequestUtil.jsonObjectRequest(new RequestListener() {
                @Override
                public String getUrl() {
                    return "/userCollection/stick";
                }

                @Override
                public Map<String, String> getParams() {
                    return null;
                }

                @Override
                public JSONObject getJsonObj() {
                    JSONObject object=new JSONObject();
                    try {
                        object.putOpt("userCollectionId",entity.getUserCollectionId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object;
                }

                @Override
                public void onResponse(JSONObject jsonObj) {
                    System.out.println("top============="+jsonObj);
                    try {
                        int code=jsonObj.getInt("code");
                        if (code==1) {
                            int index = getAdapterPosition();
                            Object object = list.remove(index);
                            list.add(0, object);
                            notifyItemMoved(index, 0);
                            ((SwipeMenuLayout) mView).quickClose();
                        }else{
                            Util.showToast(context, "置顶失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }

        private void delete(){
            RequestUtil.jsonObjectRequest(new RequestListener() {
                @Override
                public String getUrl() {
                    return "/userCollection/delete";
                }

                @Override
                public Map<String, String> getParams() {
                    return null;
                }

                @Override
                public JSONObject getJsonObj() {
                    JSONObject object=new JSONObject();
                    try {
                        object.putOpt("userCollectionId",entity.getUserCollectionId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object;
                }

                @Override
                public void onResponse(JSONObject jsonObj) {
                    System.out.println("delete============="+jsonObj);
                    try {
                        int code=jsonObj.getInt("code");
                        if (code==1) {
                            int index = getAdapterPosition();
                            list.remove(index);
                            notifyItemRemoved(index);
                        }else{
                            Util.showToast(context, "删除失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }
    }


}
