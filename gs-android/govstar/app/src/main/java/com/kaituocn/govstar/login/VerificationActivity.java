package com.kaituocn.govstar.login;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;
import com.tuo.customview.VerificationCodeView;

public class VerificationActivity extends AppCompatActivity {

    VerificationCodeView codeView;

    Button verifyBtn,downBtn;
    TextView textView;
    String info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_verification);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("用户身份验证");

        findViewById(R.id.cancelView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        textView=findViewById(R.id.textView135);
        info=textView.getText().toString();
        SpannableString spannableString=new SpannableString(info);
//        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#C12735")), info.length()-5, spannableString.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        spannableString.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), info.length()-5, spannableString.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Util.showToast(getBaseContext(),"此功能未参与试用，请等待正式版。");
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#C12735"));//设置颜色
                ds.bgColor=Color.parseColor("#eeeeee");
            }
        },info.length()-5, spannableString.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        verifyBtn=findViewById(R.id.button3);
        verifyBtn.setEnabled(false);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("verifyCode",codeView.getInputContent());
               setResult(RESULT_OK,intent);
               finish();
            }
        });
        downBtn=findViewById(R.id.button4);


        codeView=findViewById(R.id.icv_1);
        codeView.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                if (codeView.getInputContent().length()==codeView.getEtNumber()) {
                    verifyBtn.setEnabled(true);
                }
            }

            @Override
            public void deleteContent() {
                verifyBtn.setEnabled(false);
            }
        });
    }
}
