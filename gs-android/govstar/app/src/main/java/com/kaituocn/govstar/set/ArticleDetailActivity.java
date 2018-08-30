package com.kaituocn.govstar.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ArticleDetailActivity extends AppCompatActivity {

    WebView webView;
    int id;
    String title;
    String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_article_detail);

        id=getIntent().getIntExtra("id",0);
        title=getIntent().getStringExtra("title");

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText(title);
        ImageView backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = findViewById(R.id.webView);

        WebSettings settings=webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(false);
        settings.setBuiltInZoomControls(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);

        getArticleDetail(id);
    }



    private void getArticleDetail(final int id){
        RequestUtil.request(new RequestListener() {
            @Override
            public String getUrl() {
                return "/article/article/detail";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map=new HashMap<>();
                map.put("id",id+"");
                return map;
            }

            @Override
            public JSONObject getJsonObj() {
                return null;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        content=jsonObj.getString("data");
                        webView.loadData(content, "text/html; charset=UTF-8", null);
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
