package com.kaituocn.govstar.login;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.SharedPreferencesUtils;
import com.kaituocn.govstar.util.Util;

import me.relex.circleindicator.CircleIndicator;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    CircleIndicator indicator;
    TextView skipView,enterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Util.setTransparentStatusBar(getWindow());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_guide);

        skipView=findViewById(R.id.skipView);
        enterView=findViewById(R.id.enterView);
        enterView.setVisibility(View.GONE);

        skipView.setOnClickListener(this);
        enterView.setOnClickListener(this);

        viewPager=  findViewById(R.id.viewPager);
        GuidePagerAdapter adapter=new GuidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==2) {
                    enterView.setVisibility(View.VISIBLE);
                    skipView.setVisibility(View.GONE);
                }else{
                    enterView.setVisibility(View.GONE);
                    skipView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skipView:
            case R.id.enterView:
                SharedPreferencesUtils.setParam(v.getContext(),SharedPreferencesUtils.KEY_GUIDE,true);
                Intent intent=new Intent(v.getContext(),Step1Activity.class);
                startActivity(intent);
                finish();
                break;

        }
    }
}
