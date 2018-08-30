package com.kaituocn.govstar.set;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class ServiceActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_service);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("服务中心");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.textView7).setOnClickListener(this);
        findViewById(R.id.textView8).setOnClickListener(this);
        findViewById(R.id.textView9).setOnClickListener(this);
        findViewById(R.id.textView10).setOnClickListener(this);
        findViewById(R.id.textView11).setOnClickListener(this);
        findViewById(R.id.textView11).setOnClickListener(this);
        findViewById(R.id.textView114).setOnClickListener(this);
        findViewById(R.id.textView116).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.textView7:
                intent=new Intent(getApplication(),ServiceVersionActivity.class);
                break;
            case R.id.textView8:
                intent=new Intent(v.getContext(),OperationInfoActivity.class);
                break;
            case R.id.textView9:
                intent=new Intent(v.getContext(),FeedbackListActivity.class);
                break;
            case R.id.textView10:
                intent=new Intent(v.getContext(),ServiceUpgradeActivity.class);
                break;
            case R.id.textView11:
                intent=new Intent(v.getContext(),ServiceNoticeActivity.class);
                break;
            case R.id.textView114:
                intent=new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:4006970960"));
                break;
            case R.id.textView116:
                intent=new Intent(v.getContext(),ServiceClauseActivity.class);
                break;
        }
        startActivity(intent);
    }
}
