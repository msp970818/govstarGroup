package com.kaituocn.govstar.work.worklist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class AcceptResultActivity extends AppCompatActivity {

    boolean showAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_accept_result);

        showAction=getIntent().getBooleanExtra("showAction",false);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("验收结果");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (showAction){
            ((TextView)findViewById(R.id.actionTextView1)).setText("知道了");
        }else {
            findViewById(R.id.base_action_layout).setVisibility(View.GONE);
        }
    }
}
