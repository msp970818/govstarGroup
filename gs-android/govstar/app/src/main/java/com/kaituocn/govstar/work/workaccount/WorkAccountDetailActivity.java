package com.kaituocn.govstar.work.workaccount;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class WorkAccountDetailActivity extends AppCompatActivity {


    WebView webView;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_work_account_detail);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText(getIntent().getStringExtra("title"));
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        id=getIntent().getIntExtra("id",0);

        webView = findViewById(R.id.webView);
        WebSettings settings=webView.getSettings();
        //支持App内部javascript交互
        settings.setJavaScriptEnabled(true);
        //自适应屏幕
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        settings.setLoadWithOverviewMode(true);
        //设置可以支持缩放
//        settings.setSupportZoom(true);
        //扩大比例的缩放
//        settings.setUseWideViewPort(true);
        //设置是否出现缩放工具
//        settings.setBuiltInZoomControls(true);

        webView.loadUrl("file:///android_asset/ledger.html");

    }
}
