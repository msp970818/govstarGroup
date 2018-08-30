package com.kaituocn.govstar.work;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.kaituocn.govstar.R;

import me.kareluo.intensify.image.IntensifyImageView;

public class PreviewActivity extends AppCompatActivity {

    IntensifyImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Util.setTransparentStatusBar(getWindow());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_preview);
        findViewById(R.id.backView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = findViewById(R.id.intensify_image);
        System.out.println("getStringExtra========"+getIntent().getStringExtra("path"));
        imageView.setImage(getIntent().getStringExtra("path"));

    }
}
