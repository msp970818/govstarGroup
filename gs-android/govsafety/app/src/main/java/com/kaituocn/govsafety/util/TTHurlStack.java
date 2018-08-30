package com.kaituocn.govsafety.util;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class TTHurlStack extends HurlStack {

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        if (url.toString().trim().toLowerCase().startsWith("https://")) {
            HTTPSTrustManager.allowAllSSL();
        }
        return super.createConnection(url);
    }
}
