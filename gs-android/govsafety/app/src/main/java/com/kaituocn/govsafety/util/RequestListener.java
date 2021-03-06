package com.kaituocn.govsafety.util;

import org.json.JSONObject;

import java.util.Map;

public interface RequestListener {

    String getUrl();
    Map<String, String> getParams();
    JSONObject getJsonObj();
    void onResponse(JSONObject jsonObj);
}
