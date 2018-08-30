package com.kaituocn.govstar.work.rank;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

import java.util.Calendar;
import java.util.Date;

public class RankingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    TimePickerView pvTime;

    RadioButton radioButton1, radioButton2, radioButton3;
    ImageView actionView;
    String curYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_ranking);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("");
        View backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton1.setOnCheckedChangeListener(this);
        radioButton2.setOnCheckedChangeListener(this);
        radioButton3.setOnCheckedChangeListener(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if (!radioButton1.isChecked()) {
                            radioButton1.setChecked(true);
                        }
                        break;
                    case 1:
                        if (!radioButton2.isChecked()) {
                            radioButton2.setChecked(true);
                        }
                        break;
                    case 2:
                        if (!radioButton3.isChecked()) {
                            radioButton3.setChecked(true);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        switch (buttonView.getId()) {
            case R.id.radioButton1:
                if (mViewPager.getCurrentItem() != 0) {
                    mViewPager.setCurrentItem(0, false);
                }
                break;
            case R.id.radioButton2:
                if (mViewPager.getCurrentItem() != 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                break;
            case R.id.radioButton3:
                if (mViewPager.getCurrentItem() != 2) {
                    mViewPager.setCurrentItem(2, false);
                }
                break;
        }
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.container + ":" + position;
    }

    public String getCurYear(){
        return curYear!=null?curYear: Util.stempToStr(System.currentTimeMillis(), "yyyy");
    }

    private void initTimePicker() {
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            public void onTimeSelect(Date date, View v) {

                curYear= Util.getDateStr(date, "yyyy");
                for (int i = 0; i < 3; i++) {
//                    Fragment fragment=getSupportFragmentManager().findFragmentByTag(getFragmentTag(mViewPager.getCurrentItem()));
                    Fragment fragment=getSupportFragmentManager().findFragmentByTag(getFragmentTag(i));
                    if (fragment == null) {
                        continue;
                    }
                    if (fragment instanceof RankingUnitFragment) {
                        ((RankingUnitFragment) fragment).loadData();
                    }else if(fragment instanceof RankingPersonFragment){
                        ((RankingPersonFragment) fragment).loadData();
                    }else if(fragment instanceof RankingDeductionFragment){
                        ((RankingDeductionFragment) fragment).loadData();
                    }
                }
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


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new RankingUnitFragment();
                    break;
                case 1:
                    fragment = new RankingPersonFragment();
                    break;
                case 2:
                    fragment = new RankingDeductionFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
