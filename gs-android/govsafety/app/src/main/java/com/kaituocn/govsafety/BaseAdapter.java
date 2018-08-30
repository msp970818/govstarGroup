package com.kaituocn.govsafety;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaituocn.govsafety.entity.DeviceEntity;
import com.kaituocn.govsafety.util.RequestListener;
import com.kaituocn.govsafety.util.RequestUtil;
import com.kaituocn.govsafety.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        View mView;
        TextView titleView;
        TextView infoView1,infoView2,infoView3;
        ImageView iconView,closeView;
        DeviceEntity entity;
        public ClientViewHolder(View view) {
            super(view);
            mView = view;
            iconView=view.findViewById(R.id.imageView1);
            closeView=view.findViewById(R.id.imageView2);
            closeView.setOnClickListener(this);
            titleView=view.findViewById(R.id.textView);
            infoView1=view.findViewById(R.id.textView6);
            infoView2=view.findViewById(R.id.textView8);
            infoView3=view.findViewById(R.id.textView13);

        }

        @Override
        public void updateUI(Object object) {
            entity= (DeviceEntity) object;

            if (entity.getMyType()==1) {//手机
                iconView.setImageResource(R.drawable.icon_client_phone);
                titleView.setText(entity.getTitle());
                infoView1.setText("登录时间："+entity.getLoginDate());
                infoView2.setText("登录设备："+entity.getDeviceName());
                infoView3.setText("IMEI："+entity.getImei());
            }else{//电脑
                iconView.setImageResource(R.drawable.icon_client_computer);
                titleView.setText("智慧督查电脑端在线…");
                infoView1.setText("登录时间："+entity.getLoginDate());
                infoView2.setText("IP归属："+entity.getIp());
                infoView3.setText("设备名称："+"PC");
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView2:
                    closeDevice();
                    break;
            }
        }

        private void closeDevice(){
            RequestUtil.jsonObjectRequest(new RequestListener() {
                @Override
                public String getUrl() {
                    return "/GsSafe/otherLogOut";
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
                        object.putOpt("type",entity.getMyType()==1?"mobile":"PC");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object;
                }

                @Override
                public void onResponse(JSONObject jsonObj) {
                    System.out.println("closeDevice=================="+jsonObj);
                    try {
                        int code = jsonObj.getInt("code");
                        if (code==1) {
                            list.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                        }else {
                            Util.showToast(context,jsonObj.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }





}
