package com.kaituocn.govstar.work.worklist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.NoticePersonEntity;
import com.kaituocn.govstar.entity.WorkFlowEntity;
import com.kaituocn.govstar.util.BaseViewHolder;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public final static int TYPE_WORK_PROCESS=1;
    public final static int TYPE_CLOUD_MEETING_NOTICE=2;
    public final static int TYPE_CLOUD_MEETING_SIGN=3;

    private RecyclerView recyclerView;
    private Context context;
    private int type;

    private int meetingType;
    private String meetingId;

    private List<Object> list = new ArrayList<>();

    public ProcessAdapter(Context context) {
        this.context = context;
    }

    public ProcessAdapter(Context context, int type) {
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


    public void setMeetingType(int meetingType) {
        this.meetingType = meetingType;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
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

        if (viewType==TYPE_WORK_PROCESS) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_process, parent, false);
            return new WorkProcessViewHolder(view);
        }else if (viewType==TYPE_CLOUD_MEETING_NOTICE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_meeting_process, parent, false);
            return new CloudMeetingNoticeHolder(view);
        }else if (viewType==TYPE_CLOUD_MEETING_SIGN) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_meeting_process, parent, false);
            return new CloudMeetingSignHolder(view);
        }


        return null;
    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        if (holder instanceof WorkProcessViewHolder) {
            ((WorkProcessViewHolder) holder).group.setVisibility(View.VISIBLE);
            ((WorkProcessViewHolder) holder).group2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            holder.updateUI(list.get(position));
    }





    private class WorkProcessViewHolder extends BaseViewHolder {
        View mView;
        TextView titleView;
        TextView beizhuView;
        View pointView;
        TextView topTitleView;
        TextView timeView;
        ImageView headView;
        Group group;
        Group group2;
        View dividerOver;
        WorkFlowEntity entity;
        public WorkProcessViewHolder(View view) {
            super(view);
            mView = view;
            group=view.findViewById(R.id.group);
            group2=view.findViewById(R.id.group2);
            dividerOver=view.findViewById(R.id.dividerOver);
            titleView=view.findViewById(R.id.textView79);
            beizhuView=view.findViewById(R.id.textView81);
            pointView=view.findViewById(R.id.flagView);
            topTitleView=view.findViewById(R.id.textView78);
            timeView=view.findViewById(R.id.textView83);
            headView=view.findViewById(R.id.headView);

            view.findViewById(R.id.textView80).setVisibility(View.GONE);
//            titleView.setOnClickListener(this);
        }

        @Override
        public void updateUI(Object object) {
            entity= (WorkFlowEntity) object;

            group2.setVisibility(View.GONE);
            dividerOver.setVisibility(View.GONE);
            if (getAdapterPosition()==0) {
                dividerOver.setVisibility(View.VISIBLE);
            }else{
                dividerOver.setVisibility(View.GONE);
            }
            if (showTitle(getAdapterPosition())) {
                group.setVisibility(View.VISIBLE);

                topTitleView.setText(entity.getTitle());
                if (entity.getMyType()==1) {
                    pointView.setBackgroundResource(R.drawable.icon_point_blue);
                    topTitleView.setTextColor(context.getResources().getColor(R.color.item_blue));
                }else  if (entity.getMyType()==2){
                    pointView.setBackgroundResource(R.drawable.icon_point_red);
                    topTitleView.setTextColor(context.getResources().getColor(R.color.tag_red));
                }else {
                    pointView.setBackgroundResource(R.drawable.icon_point_green);
                    topTitleView.setTextColor(context.getResources().getColor(R.color.tag_green));
                }
            }else{
                group.setVisibility(View.GONE);
            }

            titleView.setText(entity.getUser());
            beizhuView.setText(entity.getInfo());
            if (entity.getDate().length()>16) {
                timeView.setText(entity.getDate().substring(0,16));
            }else{
                timeView.setText(entity.getDate());
            }
            Util.setAvatar(context,entity.getAvatarUrl(),headView);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textView79:
                    if (group2.getVisibility()== View.GONE) {
                        beizhuView.setBackgroundColor(Color.WHITE);
                        group2.setVisibility(View.VISIBLE);
                    }else{
                        beizhuView.setBackgroundResource(R.drawable.bg_corners_34);
                        group2.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        private boolean showTitle(int position){
            int preType=position==0?-1:((WorkFlowEntity)list.get(position-1)).getMyType();
            int curType=((WorkFlowEntity)list.get(position)).getMyType();
            if (preType==curType) {
                return false;
            }
            return true;
        }
    }

    private class CloudMeetingNoticeHolder extends BaseViewHolder {
        View mView;
        TextView topTitleView;
        TextView titleView;
        TextView deleteView;
        TextView timeView;
        TextView companyView;
        Group group;
        View dividerOver;
        View pointView;

        NoticePersonEntity entity;

        public CloudMeetingNoticeHolder(View view) {
            super(view);
            mView = view;
            group=view.findViewById(R.id.group);
            dividerOver=view.findViewById(R.id.dividerOver);
            titleView=view.findViewById(R.id.textView79);
            topTitleView=view.findViewById(R.id.textView78);
            companyView=view.findViewById(R.id.textView82);
            timeView=view.findViewById(R.id.textView83);
            deleteView=view.findViewById(R.id.textView80);
            deleteView.setOnClickListener(this);
            pointView=view.findViewById(R.id.flagView);
        }

        @Override
        public void updateUI(Object object) {
            entity= (NoticePersonEntity) object;
            if (getAdapterPosition()==0) {
                dividerOver.setVisibility(View.VISIBLE);
            }else{
                dividerOver.setVisibility(View.GONE);
            }

            if (showTitle(getAdapterPosition())) {
                group.setVisibility(View.VISIBLE);
                if (entity.getMyType()==1) {
                    pointView.setBackgroundResource(R.drawable.icon_point_blue);
                    topTitleView.setText("【"+entity.getPersonName()+"】创建"+ Constant.getMeetingName(meetingType));
                    topTitleView.setTextColor(context.getResources().getColor(R.color.item_blue));
                }else{
                    pointView.setBackgroundResource(R.drawable.icon_point_green);
                    topTitleView.setText("邀请列表");
                    topTitleView.setTextColor(context.getResources().getColor(R.color.tag_green));
                }
            }else{
                group.setVisibility(View.GONE);
            }

            if (entity.getMyType()==1) {
                deleteView.setVisibility(View.GONE);
                titleView.setText(entity.getPersonName()+"已创建"+Constant.getMeetingName(meetingType)+"。");
                titleView.setTextColor(context.getResources().getColor(R.color.text_black));
                companyView.setVisibility(View.INVISIBLE);
            }else{
                deleteView.setVisibility(View.VISIBLE);
                companyView.setVisibility(View.VISIBLE);
                companyView.setText(entity.getPersonComName());
                switch (entity.getReceiptState()) {
                    case 1:
                        titleView.setText(entity.getPersonName()+"准时参加会议。");
                        titleView.setTextColor(context.getResources().getColor(R.color.tag_green));
                        break;
                    case 2:
                        titleView.setText(entity.getPersonName()+"未参加会议。");
                        titleView.setTextColor(context.getResources().getColor(R.color.tag_red));
                        break;
                    case 3:
                        titleView.setText(entity.getPersonName()+"已请假。");
                        titleView.setTextColor(context.getResources().getColor(R.color.item_orange));
                        break;
                }
            }

            if (!TextUtils.isEmpty(entity.getReceiptTime())) {
                long time=Long.parseLong(entity.getReceiptTime());
                timeView.setText(Util.stempToStr(time,null));
            }else{
                timeView.setText("");
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textView80:
                    cancelInvitation(entity,getAdapterPosition());
                    break;
            }
        }

        private boolean showTitle(int position){
            int preType=position==0?-1:((NoticePersonEntity)list.get(position-1)).getMyType();
            int curType=((NoticePersonEntity)list.get(position)).getMyType();
            if (preType==curType) {
                return false;
            }
            return true;
        }
    }

    private class CloudMeetingSignHolder extends BaseViewHolder {
        View mView;
        TextView topTitleView;
        TextView titleView;
        TextView companyView;
        TextView timeView;
        Group group;
        View dividerOver;
        ImageView stateView;
        View pointView;

        NoticePersonEntity entity;

        public CloudMeetingSignHolder(View view) {
            super(view);
            mView = view;
            view.findViewById(R.id.textView80).setVisibility(View.GONE);
            group=view.findViewById(R.id.group);
            dividerOver=view.findViewById(R.id.dividerOver);
            titleView=view.findViewById(R.id.textView79);
            stateView=view.findViewById(R.id.stateView);
            stateView.setVisibility(View.VISIBLE);
            pointView=view.findViewById(R.id.flagView);
            topTitleView=view.findViewById(R.id.textView78);
            companyView=view.findViewById(R.id.textView82);
            timeView=view.findViewById(R.id.textView83);
        }

        @Override
        public void updateUI(Object object) {
            entity= (NoticePersonEntity) object;
            if (getAdapterPosition()==0) {
                dividerOver.setVisibility(View.VISIBLE);
            }else{
                dividerOver.setVisibility(View.GONE);
            }
            if (showTitle(getAdapterPosition())) {
                group.setVisibility(View.VISIBLE);
                if (entity.getMyType()==1) {
                    pointView.setBackgroundResource(R.drawable.icon_point_blue);
                    topTitleView.setText("【"+entity.getPersonName()+"】创建"+ Constant.getMeetingName(meetingType));
                    topTitleView.setTextColor(context.getResources().getColor(R.color.item_blue));
                }else{
                    pointView.setBackgroundResource(R.drawable.icon_point_green);
                    topTitleView.setText("邀请列表");
                    topTitleView.setTextColor(context.getResources().getColor(R.color.tag_green));
                }
            }else{
                group.setVisibility(View.GONE);
            }


            if (entity.getMyType()==1) {
                titleView.setText(entity.getPersonName()+"已创建"+Constant.getMeetingName(meetingType)+"。");
                titleView.setTextColor(context.getResources().getColor(R.color.text_black));
                companyView.setVisibility(View.INVISIBLE);
                stateView.setImageResource(R.drawable.icon_meeting_flag_ok);
            }else{
                companyView.setVisibility(View.VISIBLE);
                companyView.setText(entity.getPersonComName());
                if ("1".equals(entity.getIsLeave())) {
                    titleView.setText(entity.getPersonName()+"已请假。");
                    titleView.setTextColor(context.getResources().getColor(R.color.item_orange));
                    stateView.setImageResource(R.drawable.icon_meeting_flag_error);
                }else{
                    switch (entity.getSingState()){
                        case 1://已参会
                            titleView.setText(entity.getPersonName()+"准时参加会议。");
                            titleView.setTextColor(context.getResources().getColor(R.color.tag_green));
                            stateView.setImageResource(R.drawable.icon_meeting_flag_clock);

                            break;
                        case 2://未参会
                            titleView.setText(entity.getPersonName()+"未参加会议。");
                            titleView.setTextColor(context.getResources().getColor(R.color.tag_red));
                            stateView.setImageResource(R.drawable.icon_meeting_flag_error);
                            break;
                        case 3://迟到
                            titleView.setText(entity.getPersonName()+"已报名参加。");
                            titleView.setTextColor(context.getResources().getColor(R.color.text_black));
                            stateView.setImageResource(R.drawable.icon_meeting_flag_local);
                            break;
                    }

                }

            }

            if (!TextUtils.isEmpty(entity.getReceiptTime())) {
                long time=Long.parseLong(entity.getReceiptTime());
                timeView.setText(Util.stempToStr(time,null));
            }else{
                timeView.setText("");
            }

        }


        private boolean showTitle(int position){
            int preType=position==0?-1:((NoticePersonEntity)list.get(position-1)).getMyType();
            int curType=((NoticePersonEntity)list.get(position)).getMyType();
            if (preType==curType) {
                return false;
            }
            return true;
        }

    }





    private void cancelInvitation(final NoticePersonEntity entity, final int position){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/recallInvitation";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("mettingId",meetingId);
                map.put("tabId",entity.getTabId()+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("cancelInvitation==========="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemChanged(position);
//                        notifyDataSetChanged();
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
