package com.kaituocn.govsafety.util;

import org.json.JSONObject;

import java.util.Map;

public interface RequestListener {

    String getUrl();
    Map<String, String> getParams();
    void onResponse(JSONObject jsonObj);
}
