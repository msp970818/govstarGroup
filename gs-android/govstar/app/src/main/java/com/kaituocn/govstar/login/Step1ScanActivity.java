package com.kaituocn.govstar.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class Step1ScanActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    ImageView backView;
    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_step1_scan);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("扫描识别码");
        backView = findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);
        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);
        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);
        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();
        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

    }


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
//        Util.showToast(this, "result===========" + text);
        System.out.println("result==================="+text);

        Intent intent=new Intent();
        intent.putExtra("id",text);
        setResult(RESULT_OK,intent);
        finish();

    }


    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
