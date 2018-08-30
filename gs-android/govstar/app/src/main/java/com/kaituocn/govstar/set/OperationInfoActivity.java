package com.kaituocn.govstar.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationInfoActivity extends AppCompatActivity {

    ImageView backView;
    TagFlowLayout tagFlowLayout1,tagFlowLayout2,tagFlowLayout3;
    TagAdapter tagAdapter1,tagAdapter2,tagAdapter3;

    List<String> list=new ArrayList<>();
    List<FirstCatalog> firstCatalogs=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_operation_info);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("操作说明");
        backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tagFlowLayout1=findViewById(R.id.tagFlowLayout1);
        tagFlowLayout2=findViewById(R.id.tagFlowLayout2);
        tagFlowLayout3=findViewById(R.id.tagFlowLayout3);


//        list.add("1、如何获取用户账号？");
//        list.add("2、为什么我不能发布领导指示？");

        tagFlowLayout1.setAdapter(tagAdapter1=new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String str) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operation_info_1,parent, false);
                TextView textView=view.findViewById(R.id.textView1);
                textView.setText(str);
                return view;
            }
        });
        tagFlowLayout2.setAdapter(tagAdapter2=new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String str) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operation_info_1,parent, false);
                TextView textView=view.findViewById(R.id.textView1);
                textView.setText(str);
                return view;
            }
        });
        tagFlowLayout3.setAdapter(tagAdapter3=new TagAdapter<FirstCatalog>(firstCatalogs) {

            @Override
            public View getView(FlowLayout parent, int position, final FirstCatalog firstCatalog) {
                final List<SecondCatalog> secondCatalogs=new ArrayList<>();
                final TagAdapter adapter=new TagAdapter<SecondCatalog>(secondCatalogs) {
                    @Override
                    public View getView(FlowLayout parent, int position, final SecondCatalog secondCatalog) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operation_info_3,parent, false);
                        TextView textView=view.findViewById(R.id.textView);
                        textView.setText(secondCatalog.getTitle());
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(v.getContext(),ArticleDetailActivity.class);
                                intent.putExtra("id",secondCatalog.getId());
                                intent.putExtra("title",secondCatalog.getTitle());
                                startActivity(intent);

                            }
                        });
                        return view;
                    }
                };

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operation_info_2,parent, false);
                CheckBox checkBox=view.findViewById(R.id.checkBox);
                checkBox.setText(firstCatalog.getCateName());

                final TagFlowLayout layout=view.findViewById(R.id.tagFlowLayout);
                final Group group=view.findViewById(R.id.group);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            group.setVisibility(View.VISIBLE);
                            if (secondCatalogs.isEmpty()) {
                                getCateId(firstCatalog.getId(),secondCatalogs, adapter);
                            }
                        }else{
                            group.setVisibility(View.GONE);
                        }
                    }
                });


                layout.setAdapter(adapter);
                return view;
            }

            private void getCateId(final int cateId, final List<SecondCatalog> secondCatalogs, final TagAdapter adapter){
                RequestUtil.request(new RequestListener() {
                    @Override
                    public String getUrl() {
                        return "/article/article/cateId";
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> map=new HashMap<>();
                        map.put("cateId",cateId+"");
                        return map;
                    }

                    @Override
                    public JSONObject getJsonObj() {
                        return null;
                    }

                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        System.out.println("cateId==================="+jsonObj);
                        try {
                            int code=jsonObj.getInt("code");
                            if (code==1) {
                                List<SecondCatalog> list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<SecondCatalog>>(){}.getType());
                                secondCatalogs.clear();
                                secondCatalogs.addAll(list);
                                adapter.notifyDataChanged();
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
        });

        getCates();
    }



    private void getCates(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/article/all/cates";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("type","2");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("cates==================="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        List<FirstCatalog> list=new Gson().fromJson(jsonObj.getString("data"), new TypeToken<List<FirstCatalog>>(){}.getType());
                        firstCatalogs.clear();
                        firstCatalogs.addAll(list);
                        tagAdapter3.notifyDataChanged();
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


    private static class FirstCatalog{
        private int id;
        private String cateName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCateName() {
            return cateName;
        }

        public void setCateName(String cateName) {
            this.cateName = cateName;
        }
    }

    private static class SecondCatalog{
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
