package com.kaituocn.govsafety.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/24.
 */

public class RequestUtil {


    private static RequestQueue mQueue;
    private static final String BASE_URL="http://192.168.3.111:8080";

    public static void initRequestQueue(Context con) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(con);
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

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = listener.getParams();
                return params;
            }
        };


        mQueue.add(stringRequest);
    }


}
