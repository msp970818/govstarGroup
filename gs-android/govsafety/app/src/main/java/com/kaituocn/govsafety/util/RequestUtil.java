package com.kaituocn.govsafety.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/24.
 */

public class RequestUtil {


    private static RequestQueue mQueue;
//    private static final String BASE_URL="http://192.168.3.26:8090";//学义
//    private static final String BASE_URL="http://192.168.3.115:8090";//郭士伟
//    private static final String BASE_URL="http://192.168.3.252:8090";
    private static final String BASE_URL="https://api.govstardc.cn";
    private static Context context;
    public static void initRequestQueue(Context con) {
        context = con;
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(con,new TTHurlStack());
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
            }
        }){


            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params= new HashMap<>();
                params.put("authKey",SharedPreferencesUtils.getData(context,SharedPreferencesUtils.KEY_AUTHKEY));
                System.out.println("getHeaders==========="+params.toString());
                return params;
            }
        };

        mQueue.add(jsonObjectRequest);
    }


}
