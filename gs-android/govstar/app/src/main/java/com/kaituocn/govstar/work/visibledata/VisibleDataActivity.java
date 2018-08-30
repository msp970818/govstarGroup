package com.kaituocn.govstar.work.visibledata;

import android.content.Intent;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;
import com.kaituocn.govstar.work.rank.RankingDeductionFragment;
import com.kaituocn.govstar.work.rank.RankingPersonFragment;
import com.kaituocn.govstar.work.rank.RankingUnitFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VisibleDataActivity extends AppCompatActivity {

    View btn1,btn2;
    WebView webView;

    ImageView actionView;
    String curYear;
    String state;

    TimePickerView pvTime;
    OptionsPickerView pvOptions;
    List<String> optionsItems = new ArrayList<>();


    HorizontalBarChart mHorizontalBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_visible_data);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("可视数据");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        curYear=Calendar.getInstance().get(Calendar.YEAR)+"";
        state="4";

        actionView = findViewById(R.id.action2View);
        actionView.setVisibility(View.VISIBLE);
        int padding = Util.dp2px(this, 14);
        actionView.setPadding(padding, padding, padding, padding);
        actionView.setImageResource(R.drawable.icon_work_rank_year);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });



        findViewById(R.id.actionGroup1).setVisibility(View.GONE);
        findViewById(R.id.actionGroup2).setVisibility(View.VISIBLE);
        btn1=findViewById(R.id.actionView2);
        btn2=findViewById(R.id.actionView3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),CountDataActivity.class);
                intent.putExtra("curYear",curYear);
                intent.putExtra("state",state);
                startActivity(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOptionsPicker();
            }
        });

        webView = findViewById(R.id.webView);
        WebSettings settings=webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.loadUrl("file:///android_asset/visualdata.html");
//        initView();

        getPersonLevel();
    }


    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {
                curYear= Util.getDateStr(date, "yyyy");
            }
        }).setType(new boolean[]{true, false, false, false, false, false})
                .setLabel("年", "月", "日", ":00", "分", "秒")
//                .setCancelColor(Color.GRAY)
//                .setSubmitColor(getResources().getColor(R.color.gs_red))
//                .setSubCalSize(15)
                .setLineSpacingMultiplier(2.5f)
                .setContentTextSize(15)
                .setDividerColor(0x00ffffff)
                .setLayoutRes(R.layout.layout_custom_pickerview_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                        TextView tvCancel = v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvTime.returnData();
                                pvTime.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvTime.dismiss();
                            }
                        });
                    }
                })
                .build();
        Calendar calendar=Calendar.getInstance();
        if (!TextUtils.isEmpty(curYear)) {
            calendar.set(Calendar.YEAR,Integer.parseInt(curYear));
        }
        pvTime.setDate(calendar);
        pvTime.show();
    }


    private void initOptionsPicker() {

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                state= getState(optionsItems.get(options1));
            }
        }).setLineSpacingMultiplier(3f)
                .setContentTextSize(15)
                .setDividerColor(0x00ffffff)
                .setLayoutRes(R.layout.layout_custom_pickerview_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                        TextView tvCancel = v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.returnData();
                                pvOptions.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.dismiss();
                            }
                        });
                    }
                })
                .build();
        pvOptions.setPicker(optionsItems);
        pvOptions.show();
    }

    private String getState(String name){
        switch (name) {
            case "显示本区数据":
                return "1";
            case "显示本单位数据":
                return "2";
            case "显示本部门数据":
                return "3";
            case "显示本人数据":
                return "4";
        }
        return "4";
    }


    private void getPersonLevel(){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/viewData/getPersonLevel";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("getPersonLevel==============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        int level=jsonObj.getInt("data");
                        optionsItems.clear();
                        optionsItems.add("显示本人数据");
                        switch (level) {
                            case 1:
                                optionsItems.add("显示本区数据");
                                break;
                            case 2:
                                optionsItems.add("显示本单位数据");
                                break;
                            case 3:
                                optionsItems.add("显示本部门数据");
                                break;
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



    private void initView(){
        mHorizontalBarChart = findViewById(R.id.mHorizontalBarChart);
        //设置相关属性
//        mHorizontalBarChart.setOnChartValueSelectedListener(this);
        mHorizontalBarChart.setDrawBarShadow(false);
        mHorizontalBarChart.setDrawValueAboveBar(true);
        mHorizontalBarChart.getDescription().setEnabled(false);
        mHorizontalBarChart.setMaxVisibleValueCount(60);
        mHorizontalBarChart.setPinchZoom(false);
        mHorizontalBarChart.setDrawGridBackground(false);

        //x轴
        XAxis xl = mHorizontalBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);

        //y轴
        YAxis yl = mHorizontalBarChart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f);

        //y轴
        YAxis yr = mHorizontalBarChart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        //设置数据
        setData(12, 50);
        mHorizontalBarChart.setFitBars(true);
        mHorizontalBarChart.animateY(2500);

        Legend l = mHorizontalBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
    }

    //来点随机数吧
    private void setData(int count, float range) {
        float barWidth = 9f;
        float spaceForBar = 10f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            yVals1.add(new BarEntry(i * spaceForBar, val));
        }
        BarDataSet set1;
        if (mHorizontalBarChart.getData() != null &&
                mHorizontalBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mHorizontalBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mHorizontalBarChart.getData().notifyDataChanged();
            mHorizontalBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "DataSet 1");

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(barWidth);
            mHorizontalBarChart.setData(data);
        }
    }
}
