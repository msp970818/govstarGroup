package com.kaituocn.govstar.set;

import android.app.Dialog;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.RankingDeductEntity;
import com.kaituocn.govstar.entity.UserEntity;
import com.kaituocn.govstar.event.ChangeHeadEvent;
import com.kaituocn.govstar.main.MainActivity;
import com.kaituocn.govstar.main.OnFragmentInteractionListener;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.rank.RankingActivity;
import com.kaituocn.govstar.yunav.TeamAVChatProfile;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement theinterface
 * to handle interaction events.
 */
public class SetFragment extends Fragment implements View.OnClickListener{

    OnFragmentInteractionListener mListener;
    ImageView headView;
    TextView scoreView;
    TagFlowLayout tagFlowLayout;
    TagAdapter tagAdapter;
    List<UserEntity.AccountInfo> tagList=new ArrayList<>();

    public SetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_set, container, false);
        scoreView=view.findViewById(R.id.textView4);
        ((TextView)view.findViewById(R.id.textView3)).setText(String.format("分(%d)",Calendar.getInstance().get(Calendar.YEAR)));
        headView= view.findViewById(R.id.headView);
        headView.setOnClickListener(this);
        Glide.with(this).load(Util.getFileBaseUrl(getContext()) +Constant.userEntity.getPersonInfo().getAvatarUrl())
                .error(R.drawable.t2).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE ).into(headView);
        view.findViewById(R.id.collectView).setOnClickListener(this);
        view.findViewById(R.id.serviceView).setOnClickListener(this);
        view.findViewById(R.id.setView).setOnClickListener(this);
        tagFlowLayout= view.findViewById(R.id.tagFlowLayout);

        ((TextView)view.findViewById(R.id.nameView)).setText(Constant.userEntity.getPersonInfo().getName());
        ((TextView)view.findViewById(R.id.infoView)).setText(Constant.userEntity.getPersonInfo().getComName()+" - "+Constant.userEntity.getPersonInfo().getDepName());

        List<UserEntity.AccountInfo> list=Constant.userEntity.getMultiAccountList();
        tagList.clear();
        for (UserEntity.AccountInfo accountInfo : list) {
            if (accountInfo.getId()!=Integer.parseInt( TeamAVChatProfile.getUserId())) {
                tagList.add(accountInfo);
            }
        }
        if (tagList.size()<1) {
            tagFlowLayout.setVisibility(View.GONE);
        }
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                changeAccount(tagList.get(position).getId());
                return true;
            }
        });
        tagFlowLayout.setAdapter(tagAdapter=new TagAdapter<UserEntity.AccountInfo>(tagList) {
            @Override
            public View getView(FlowLayout parent, int position, UserEntity.AccountInfo accountInfo) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_account,parent, false);
                TextView textView=view.findViewById(R.id.textView1);
                textView.setText("账号切换 - "+accountInfo.getComName()+" - "+accountInfo.getDepName());
                return view;
            }
        });

        getScore();
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.headView:
//                openDrawer();
                break;
            case R.id.collectView:
                intent=new Intent(v.getContext(),CollectActivity.class);
                startActivity(intent);
                break;
            case R.id.serviceView:
                intent=new Intent(v.getContext(),ServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.setView:
                intent=new Intent(v.getContext(),SettingActivity.class);
                startActivity(intent);
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

    Dialog dialog;
    private void changeAccount(final int userId){
        dialog=Util.showLoadingDialog(getActivity(),"账户切换中…");
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/GsMobileUser/changeLoginUser";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.putOpt("userId",userId)
                            .putOpt("platform","Android")
                            .putOpt("appType","GS")
                            .putOpt("uuid", Util.getSerialNumber(getContext()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                dialog.dismiss();
                System.out.println("changeAccount========="+jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code==1) {
                        JSONObject data = jsonObj.getJSONObject("data");
                        SharedPreferencesUtils.setData(getContext(),SharedPreferencesUtils.KEY_AUTHKEY,data.getJSONObject("personInfo").getString("authKey"));
                        SharedPreferencesUtils.setData(getContext(),SharedPreferencesUtils.KEY_UID,data.getJSONObject("personInfo").getString("id"));
                        SharedPreferencesUtils.setData(getContext(),SharedPreferencesUtils.KEY_USER,data.toString());
                        Intent intent=new Intent(getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Util.showToast(getContext(),jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
            }
        });
    }


    private void getScore(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/Score/ownDeduct";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String,String> map=new HashMap<>();
                map.put("annual", Calendar.getInstance().get(Calendar.YEAR)+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("ownDeduct============"+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        JSONArray array= jsonObj.getJSONArray("data");
                        if (!array.isNull(0)) {
                            String score=array.getJSONObject(0).getString("totalScore");
                            scoreView.setText(score);
                        }else {
                            scoreView.setText("100");
                        }
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
