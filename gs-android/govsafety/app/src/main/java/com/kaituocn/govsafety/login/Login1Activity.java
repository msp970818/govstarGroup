package com.kaituocn.govsafety.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaituocn.govsafety.R;
import com.kaituocn.govsafety.util.CodeUtils;
import com.kaituocn.govsafety.util.Util;

public class Login1Activity extends AppCompatActivity {

    Button nextBtn;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("输入用户名与密码");
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=Util.showDialog(initDialogView(v.getContext()));
            }
        });

        nextBtn=findViewById(R.id.button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Login2Activity.class);
                startActivity(intent);
            }
        });



    }



    private View initDialogView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null, false);
        TextView titleView=view.findViewById(R.id.titleView);
        titleView.setText("首次使用请先绑定设备");
        titleView.setTextColor(getResources().getColor(R.color.dilog_blue));
        TextView infoView=view.findViewById(R.id.infoView);
        infoView.setText("第一次下载使用政务安全宝请先绑定您的系统账号！");
        TextView btn1View=view.findViewById(R.id.btn1View);
        btn1View.setText("立即绑定");
        btn1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.btn2View).setVisibility(View.GONE);


        return view;

    }


    private View initDialogView2(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_2, null, false);
        final EditText editText=view.findViewById(R.id.editText);
        final ImageView codeView=view.findViewById(R.id.imageView);
        Bitmap bitmap= CodeUtils.getInstance().createBitmap("AbcD",Util.dp2px(context,64),Util.dp2px(context,30));
        codeView.setImageBitmap(bitmap);

        ImageView refreshView=view.findViewById(R.id.refreshView);
        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap= CodeUtils.getInstance().createBitmap(CodeUtils.getInstance().createCode(),Util.dp2px(v.getContext(),64),Util.dp2px(v.getContext(),30));
                codeView.setImageBitmap(bitmap);
            }
        });

        view.findViewById(R.id.btn1View).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CodeUtils.getInstance().getCode().toLowerCase().equals(editText.getText().toString().trim().toLowerCase())) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }

                }else{
                    Toast.makeText(v.getContext(),"输入错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


}
