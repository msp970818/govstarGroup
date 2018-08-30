package com.kaituocn.govstar.work.schedulemanage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.MyEntity;
import com.kaituocn.govstar.entity.PersonScheduleEntity;
import com.kaituocn.govstar.entity.ScheduleEntity;
import com.kaituocn.govstar.util.BaseViewHolder;
import com.kaituocn.govstar.util.ScheduleGridView;
import com.kaituocn.govstar.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.HOUR_OF_DAY;

public class ScheduleAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public final static int TYPE_A = 1;

    private RecyclerView recyclerView;
    private Context context;
    private int type;

    private List<Object> list = new ArrayList<>();

    private String curDate;

    public ScheduleAdapter(Context context) {
        this.context = context;
    }

    public ScheduleAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public List<Object> getList() {
        return list;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
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

        if (viewType == TYPE_A) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule_content, parent, false);
            return new AViewHolder(view);
        }


        return null;
    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.updateUI(list.get(position));
    }


    private class AViewHolder extends BaseViewHolder {
        LinearLayout contentLayout;
        ScheduleGridView gridView;
        PersonScheduleEntity entity;
        TextView nameView;
        public AViewHolder(View view) {
            super(view);
            contentLayout = view.findViewById(R.id.contentLayout);
            gridView=view.findViewById(R.id.gridView);
            nameView=view.findViewById(R.id.nameView);
        }

        @Override
        public void updateUI(Object object) {
            entity= (PersonScheduleEntity) object;
            nameView.setText(entity.getName());
            contentLayout.removeAllViews();
            for (ScheduleEntity scheduleEntity : entity.getBody()) {

                ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.item_panel_schedule, null);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.height=Util.dp2px(context,25);
                int left=getLeftMargin(scheduleEntity.getStartTime());
                params.leftMargin=Util.dp2px(context,left);
//                params.width= Util.dp2px(context,180);
                params.width= Util.dp2px(context,getLength(scheduleEntity.getStartTime(),scheduleEntity.getEndTime(),left));

                int padding=Util.dp2px(context,10);
                imageView.setPadding(0,padding,0,padding);
                imageView.setLayoutParams(params);

                imageView.setTag(scheduleEntity.getId());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Util.showToast(v.getContext(),v.getTag().toString());
                        Intent intent=new Intent(v.getContext(),ScheduleInfoActivity.class);
                        intent.putExtra("id",(int)v.getTag());
                        context.startActivity(intent);
                    }
                });
                switch (scheduleEntity.getScheduleImportance()) {
                    case 1:
                        Util.setImageTint(imageView,R.color.gs_red);
                        break;
                    case 2:
                        Util.setImageTint(imageView,R.color.gs_blue);
                        break;
                    case 3:
                        Util.setImageTint(imageView,R.color.gs_green);
                        break;
                }
                contentLayout.addView(imageView);
            }

        }

    }

    private int getLength(String startTime,String endTime,int left){
        String nextDay=getNextDay();
        int step=60;
        if (endTime.compareTo(nextDay)>=0) {
            return 24*60-left;
        }else{
            if (startTime.compareTo(curDate)<0) {
                Point pointEnd=getHoursMinutes(endTime);
                return pointEnd.x*step+pointEnd.y*step/60;
            }else{
                Point pointStart=getHoursMinutes(startTime);
                Point pointEnd=getHoursMinutes(endTime);
                return (pointEnd.x-pointStart.x)*step+(pointEnd.y-pointStart.y)*step/60;
            }
        }
    }

    private int getLeftMargin(String dateStr){
        if (dateStr.compareTo(curDate)<=0) {
            return 0;
        }
        int step=60;
        Point point=getHoursMinutes(dateStr);
        int length=point.x*step+point.y*step/60;

        return length;
    }

    private Point getHoursMinutes(String dateStr) {
        Point point=new Point();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = sdf.parse(dateStr);
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(d);
            point.x=calendar.get(Calendar.HOUR_OF_DAY);
            point.y=calendar.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return point;
    }

    private String getNextDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = sdf.parse(curDate);
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            Date date= calendar.getTime();
            return Util.getDateStr(date,null);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

}
