package com.kaituocn.govstar.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/24.
 */

public class RequestUtil {


    private static RequestQueue mQueue;
//    private static final String BASE_URL="http://192.168.3.26:8090";//学艺
//    private static final String BASE_URL="http://192.168.3.22:8090";//富强
//    private static final String BASE_URL="http://192.168.3.25:8090";//任帅
//    private static final String BASE_URL="http://192.168.3.115:8090";//郭士伟
//    private static final String BASE_URL="https://192.168.3.25:8443";//任帅
//    private static final String BASE_URL="http://192.168.3.252:8090";
    private static final String BASE_URL="https://api.govstardc.cn";
    private static Context context;
    public static void initRequestQueue(Context con) {
        context = con;
        if (mQueue == null) {
//            mQueue = Volley.newRequestQueue(con);
            mQueue = Volley.newRequestQueue(con, new TTHurlStack());
        }

    }


    public static void request(final RequestListener listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + listener.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    System.out.println("json format error ====== " + response);
                    jsonObj = new JSONObject();
                    try {
                        jsonObj.putOpt("code", 500);
                        jsonObj.putOpt("error", "json format error !");
                    } catch (JSONException e1) {
//                    e1.printStackTrace();
                    }
                }
                listener.onResponse(jsonObj);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("request VolleyError================"+error.toString());
                listener.onError(error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = listener.getParams();
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params= new HashMap<>();
                params.put("authKey",SharedPreferencesUtils.getData(context,SharedPreferencesUtils.KEY_AUTHKEY));
                return params;
            }

        };


        mQueue.add(stringRequest);
    }

    public static void jsonObjectRequest(final RequestListener listener){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, BASE_URL + listener.getUrl(), listener.getJsonObj(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("jsonObjectRequest VolleyError================"+error.toString());
                listener.onError(error.toString());
            }
        }){


            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params= new HashMap<>();
                params.put("authKey",SharedPreferencesUtils.getData(context,SharedPreferencesUtils.KEY_AUTHKEY));
//                System.out.println("getHeaders==========="+params.toString());
                return params;
            }
        };

        mQueue.add(jsonObjectRequest);
    }




    public static void uploadFile(final File file, final RequestListener listener) {
        new Thread(){
            @Override
            public void run() {
                HttpClient httpclient;
                try {
                    if (!BASE_URL.contains("https://")) {
                        httpclient = new DefaultHttpClient();
                    }else{
                        httpclient = HttpClientHelper.getHttpClient();
                    }
                    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6 * 1000);
                    httpclient.getParams().setParameter("utf8", "utf-8");

                    HttpPost httpPost = new HttpPost(BASE_URL + listener.getUrl());

                    MultipartEntity mpEntity = new MultipartEntity();

                    Map<String, String> params=listener.getParams();
                    if (params != null) {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            mpEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                        }
                    }

                    mpEntity.addPart("fileName", new FileBody(file));

                    // 设置请求参数
                    httpPost.setEntity(mpEntity);
                    HttpResponse response = httpclient.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        JSONObject jsonObject=new JSONObject(result);
                        listener.onResponse(jsonObject);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public static void uploadFiles(final List<File> files, final RequestListener listener) {
        new Thread(){
            @Override
            public void run() {
                HttpClient httpclient;
                try {
                    if (!BASE_URL.contains("https://")) {
                        httpclient = new DefaultHttpClient();
                    }else{
                        httpclient = HttpClientHelper.getHttpClient();
                    }
                    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6 * 1000);
                    httpclient.getParams().setParameter("utf8", "utf-8");

                    HttpPost httpPost = new HttpPost(BASE_URL + listener.getUrl());

                    MultipartEntity mpEntity = new MultipartEntity();

                    Map<String, String> params=listener.getParams();
                    if (params != null) {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            mpEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                        }
                    }

                    if (files != null) {
                        for (File file : files) {
                            mpEntity.addPart("fileName", new FileBody(file));
                        }
                    }
                    // 设置请求参数
                    httpPost.setEntity(mpEntity);
                    HttpResponse response = httpclient.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        JSONObject jsonObject=new JSONObject(result);
                        listener.onResponse(jsonObject);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }



}
