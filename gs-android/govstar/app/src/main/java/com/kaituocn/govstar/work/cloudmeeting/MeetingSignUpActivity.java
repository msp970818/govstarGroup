package com.kaituocn.govstar.work.cloudmeeting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.FileEntity;
import com.kaituocn.govstar.entity.MeetingEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.util.Constant;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.WebViewActivity;
import com.kaituocn.govstar.work.supervision.PersonChoseActivity;
import com.kaituocn.govstar.yunav.Control;
import com.kaituocn.govstar.yunav.TeamAVChatProfile;
import com.kaituocn.govstar.yunav.player.LiveActivity;
import com.kaituocn.govstar.yunav.player.VodActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TagFlowLayout tagFlowLayout;
    TagAdapter tagAdapter;
    ImageView imageView;

    TextView agentView;
    public View actionView1;


    String replacePerson;

    int type;
    Group groupAgent, groupAnnex, groupNoSignUp;
    View bottomLayout;
    TextView bottomView2;

    MeetingEntity entity;

    Dialog dialog;

    List<FileEntity> fileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_meeting_sign_up);

        entity = (MeetingEntity) getIntent().getSerializableExtra("entity");
        type = getIntent().getIntExtra("type", 0);

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(getIntent().getStringExtra("titleName"));

        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        groupAgent = findViewById(R.id.groupAgent);
        groupAnnex = findViewById(R.id.groupAnnex);
        groupNoSignUp = findViewById(R.id.groupNoSignUp);
        groupAnnex.setVisibility(View.GONE);
        groupNoSignUp.setVisibility(View.GONE);

        bottomLayout = findViewById(R.id.bottomLayout);
        bottomView2 = findViewById(R.id.bottomView2);

        tagFlowLayout = findViewById(R.id.frameLayout);
        tagFlowLayout.setAdapter(tagAdapter = new TagAdapter<FileEntity>(fileList) {
            @Override
            public View getView(FlowLayout parent, int position, FileEntity entity) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex, parent, false);
                TextView textView1 = view.findViewById(R.id.textView1);
                TextView textView2 = view.findViewById(R.id.textView2);
                textView1.setText("附件" + (position + 1) + "：");
                textView2.setText(entity.getFilesTitle());


                return view;
            }
        });
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
//                Intent intent=new Intent(view.getContext(), PreviewActivity.class);
                Intent intent = new Intent(view.getContext(), WebViewActivity.class);
                intent.putExtra("path", Util.getFileBaseUrl(getApplication()) + fileList.get(position).getFilesUrl());
                startActivity(intent);
                return true;
            }
        });

        imageView = findViewById(R.id.imageView16);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.INVISIBLE);
            }
        });

        if (entity == null) {
            getDetailInfo(getIntent().getStringExtra("id"),2);
        } else {
            init();
        }


    }


    private void init() {
        if (type == 1) {
            TextView actionView = findViewById(R.id.actionView);
            actionView.setVisibility(View.VISIBLE);
            actionView.setText("完成");
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    meetingBaoming();
                }
            });

            agentView = findViewById(R.id.textView52);
            agentView.setOnClickListener(this);

            groupAgent.setVisibility(View.VISIBLE);
//            groupNoSignUp.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            actionView1 = findViewById(R.id.actionView1);
            ((TextView) findViewById(R.id.actionTextView1)).setText("请假");
            ((TextView) findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_jujue, 0, 0, 0);
            actionView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(v.getContext(),LeaveActivity.class);
                    intent.putExtra("entity",entity);
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        if (type == 2) {
            bottomLayout.setVisibility(View.VISIBLE);
            actionView1 = findViewById(R.id.actionView1);

            actionView1.setBackgroundColor(getResources().getColor(R.color.gs_red));
            actionView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entity.getMettingType() == 2) {//音视频通话
                        getDetailInfo(entity.getId(),3);

                    } else if (entity.getMettingType() == 3) {//直播
                        Intent intent = new Intent(v.getContext(), LiveActivity.class);
                        intent.putExtra("videoPath", entity.getRoom().getRtmpPullUrl());
                        intent.putExtra("meetingId", entity.getId());
                        startActivity(intent);
                    }
                }
            });
            ((TextView) findViewById(R.id.actionTextView1)).setText("进入网络会议室");
            ((TextView) findViewById(R.id.actionTextView1)).setTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_enter, 0, 0, 0);
            boolean click = getIntent().getBooleanExtra("click", true);
            if (!click) {
                actionView1.setEnabled(false);
                actionView1.setBackgroundColor(getResources().getColor(R.color.item_gray_light));
            }
        }
        if (type == 3) {
            bottomLayout.setVisibility(View.VISIBLE);
            if (getIntent().getBooleanExtra("click", true)) {
                findViewById(R.id.actionView1).setBackgroundColor(getResources().getColor(R.color.gs_red));
                ((TextView) findViewById(R.id.actionTextView1)).setText("立即签到");
                findViewById(R.id.actionTextView1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        meetingQiandao();
                    }
                });
            } else {
                findViewById(R.id.actionView1).setBackgroundColor(getResources().getColor(R.color.item_gray_light));
                ((TextView) findViewById(R.id.actionTextView1)).setText("已签到");
            }
            ((TextView) findViewById(R.id.actionTextView1)).setTextColor(Color.WHITE);
            ((TextView) findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_signup, 0, 0, 0);

        }

        if (type == 4) {
            if (entity.getMettingType() == 3) {
                bottomView2.setVisibility(View.VISIBLE);
                bottomView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), VodActivity.class);
                        intent.putExtra("videoPath", entity.getRoom().getOrigUrl());
                        startActivity(intent);
                    }
                });
                if("1".equals(entity.getIsOverdue())){
                    bottomView2.setEnabled(true);
                    Util.setBackgroundTint(bottomView2,R.color.item_gray_light);
                    bottomView2.setText("回放已过期");
                }
            }
        }
        if (type == 5) {
            bottomView2.setVisibility(View.VISIBLE);
            Util.setBackgroundTint(bottomView2, R.color.item_gray_light);
        }
        if (type == 6) {
            bottomLayout.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.actionTextView1)).setText("电子签到单");
            ((TextView) findViewById(R.id.actionTextView1)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_btn_dzqdd, 0, 0, 0);
            findViewById(R.id.actionView1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MeetingNoticeActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("titleName", "电子签到单");
                    intent.putExtra("meetingId", entity.getId());
                    intent.putExtra("meetingType", entity.getMettingType());
                    startActivity(intent);
                }
            });
        }

        initView(entity);

        getAnnexList();
    }


    private void initView(MeetingEntity entity) {
        if (entity == null) {
            return;
        }
        ((TextView) findViewById(R.id.textView45)).setText(entity.getMeetingTitile());
        ((TextView) findViewById(R.id.textView46)).setText(entity.getMettingNum());
        ((TextView) findViewById(R.id.textView54)).setText(Constant.getMeetingName(entity.getMettingType()));
        ((TextView) findViewById(R.id.textView69)).setText(entity.getCreateName());
        ((TextView) findViewById(R.id.textView51)).setText(getMeetingState(entity));
        ((TextView) findViewById(R.id.textView113)).setText(Util.stempToStr(entity.getMettringStartTime(), null));
        ((TextView) findViewById(R.id.editText2)).setText(Html.fromHtml(entity.getMettingDescribe()));
        showAttendee(entity.getAttendName(),entity.getAttendavatarUrl());

    }

    private void showAttendee(String joinNameStr,String avatarStr) {
        if (TextUtils.isEmpty(joinNameStr)) {
            return;
        }
        String[] joinNames = joinNameStr.split(",");
        String[] avatarUrls=avatarStr.split(",");
        switch (joinNames.length > 6 ? 6 : joinNames.length) {
            case 6:
                findViewById(R.id.imageView6).setVisibility(View.VISIBLE);
                findViewById(R.id.nameView6).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.nameView6)).setText(joinNames[5]);
                Util.setAvatar(this,avatarUrls[5],(ImageView) findViewById(R.id.imageView6));
            case 5:
                findViewById(R.id.imageView5).setVisibility(View.VISIBLE);
                findViewById(R.id.nameView5).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.nameView5)).setText(joinNames[4]);
                Util.setAvatar(this,avatarUrls[4],(ImageView) findViewById(R.id.imageView5));
            case 4:
                findViewById(R.id.imageView4).setVisibility(View.VISIBLE);
                findViewById(R.id.nameView4).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.nameView4)).setText(joinNames[3]);
                Util.setAvatar(this,avatarUrls[3],(ImageView) findViewById(R.id.imageView4));
            case 3:
                findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
                findViewById(R.id.nameView3).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.nameView3)).setText(joinNames[2]);
                Util.setAvatar(this,avatarUrls[2],(ImageView) findViewById(R.id.imageView3));
            case 2:
                findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
                findViewById(R.id.nameView2).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.nameView2)).setText(joinNames[1]);
                Util.setAvatar(this,avatarUrls[1],(ImageView) findViewById(R.id.imageView2));
            case 1:
                findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
                findViewById(R.id.nameView1).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.nameView1)).setText(joinNames[0]);
                Util.setAvatar(this,avatarUrls[0],(ImageView) findViewById(R.id.imageView1));
                break;
            case 0:
                findViewById(R.id.groupJoins).setVisibility(View.GONE);
                break;

        }
    }


    private String getMeetingState(MeetingEntity entity) {
        if (entity.getMettingState() == 1) {
            if (entity.getShowState() == 1) {
                return "等待报名";
            } else {
                return "进行中";
            }

        } else {
            return "已结束";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView52:
                Intent intent = new Intent(v.getContext(), PersonChoseActivity.class);
                intent.putExtra("type", 23);
                intent.putExtra("meetingId", entity.getId());
                startActivityForResult(intent, 1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    List<PersonEntity> list = data.getParcelableArrayListExtra("entities");
                    agentView.setText("已选择 " + list.size() + " 位代理人");
                    replacePerson = Util.getPersonIds(list);
                    System.out.println("replacePerson==============" + replacePerson);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        setResult(RESULT_OK);
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    groupAnnex.setVisibility(View.VISIBLE);
                    tagAdapter.notifyDataChanged();
                    ((TextView) findViewById(R.id.textView120)).setText("已上传 " + fileList.size() + " 个附件");
                    break;
                case 2:
                    init();
                    break;
                case 3:
                    String[] ids = entity.getAttendPersonIds().split(",");
                    ArrayList list = new ArrayList(Arrays.asList(ids));
                    list.add(0, TeamAVChatProfile.getUserId());
                    Control.createChatRoom(getApplication(), list, entity.getMeetingTitile(), entity.getId(), "iconUrl");
                    break;
            }
        }
    };

    private int getSelectedNum(boolean[] selecteds) {
        int num = 0;
        for (boolean selected : selecteds) {
            if (selected) {
                num++;
            }
        }
        return num;
    }

    private String getSelectedIds(String[] ids) {
        StringBuffer sb = new StringBuffer();
        for (String id : ids) {
            if (!TextUtils.isEmpty(id)) {
                sb.append(id);
                sb.append(",");
            }
        }
        String string = sb.toString();
        return string.substring(0, string.length() - 1);
    }


    //会议报名
    private void meetingBaoming() {
        dialog = Util.showLoadingDialog(this, "提交中…");

        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/confirmAttent";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", entity.getId());
                map.put("remarksInfo", "");
                map.put("replacePerson", "");

                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object = new JSONObject();
                try {
                    object.putOpt("mettingId", entity.getId());
                    object.putOpt("remarksInfo", "");
                    object.putOpt("replacePerson", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("object===========" + object);
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("meetingBaoming=========" + jsonObj.toString());
                dialog.dismiss();
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Util.showToast(getApplication(), jsonObj.getString("message"));
                    }
                } catch (JSONException e) {

                    Util.showToast(getApplication(), e.toString());
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
            }
        });
    }

    //会议签到
    private void meetingQiandao() {
        dialog = Util.showLoadingDialog(this, "提交中…");

        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/mettingSign";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", entity.getId());
                return map;
            }

            @Override
            public JSONObject getJsonObj() {

                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("meetingQiandao=========" + jsonObj.toString());
                dialog.dismiss();
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        finish();
                    } else {
                        Util.showToast(getApplication(), jsonObj.getString("message"));
                    }
                } catch (JSONException e) {

                    Util.showToast(getApplication(), e.toString());
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
            }
        });
    }

    private void getAnnexList() {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/getAppFileList";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", entity.getId());
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getAnnexList================" + jsonObj);
                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        List<FileEntity> entities = new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<FileEntity>>() {
                        }.getType());
                        if (entities.size() > 0) {
                            fileList.addAll(entities);
                            handler.sendEmptyMessage(1);
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


    private void getDetailInfo(final String mettingId, final int what) {
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/CloudMetting/getMettingInfo";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mettingId", mettingId);
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getMettingInfo=================" + jsonObj);

                try {
                    int code = jsonObj.getInt("code");
                    if (code == 1) {
                        entity = new Gson().fromJson(jsonObj.getString("data"), MeetingEntity.class);
                        if (entity.getMettingRoomId() != null) {
                            MeetingEntity.Room room = new Gson().fromJson(entity.getMettingRoomId(), MeetingEntity.Room.class);
                            entity.setRoom(room);
                        }
                        handler.sendEmptyMessage(what);
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
