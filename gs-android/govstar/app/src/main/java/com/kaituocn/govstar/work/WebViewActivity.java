package com.kaituocn.govstar.work;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kaituocn.govstar.R;

public class WebViewActivity extends AppCompatActivity {

    String path;
    WebView webView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_web_view);

        path = getIntent().getStringExtra("path");

        findViewById(R.id.backView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = findViewById(R.id.imageView);

        int res=getIntent().getIntExtra("res",0);
        if (res!=0) {
            Glide.with(this).load(res).into(imageView);
        }else if (isImage(path)) {
            Glide.with(this).load(path).into(imageView);
        } else {
            webView = findViewById(R.id.webView);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.loadUrl(path);
        }

    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

    private boolean isImage(String path) {
        String suffix = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        switch (suffix) {
            case "jpg":
            case "jpeg":
            case "png":
                return true;
        }
        return false;
    }
}
