package com.kaituocn.govstar.set;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.RequestListener;
import com.kaituocn.govstar.util.RequestUtil;
import com.kaituocn.govstar.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateFeedbackActivity extends AppCompatActivity {

    OptionsPickerView pvOptions;
    int feedbackType;
    TextView textView;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_create_feedback);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("新建工单");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textView=findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideInput(v.getContext(),v);
                initOptionsPicker();
            }
        });

        editText=findViewById(R.id.editText);

        findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(feedbackType==0){
                    Util.showToast(v.getContext(),"请选择工单类型");
                }else if(TextUtils.isEmpty(editText.getText().toString().trim())){
                    Util.showToast(v.getContext(),"请输入反馈意见");
                }else{
                    submit();
                }
            }
        });
    }


    private void initOptionsPicker() {

        final List<String> optionsItems = new ArrayList<>();
        optionsItems.add("系统功能错误");
        optionsItems.add("提倡建议");
        optionsItems.add("服务帮助");
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                textView.setText(optionsItems.get(options1));
                feedbackType =options1+1;

            }
        }).setLineSpacingMultiplier(3f)
                .setContentTextSize(15)
                .setDividerColor(0x00ffffff)
                .setLayoutRes(R.layout.layout_custom_pickerview_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                        TextView tvCancel = v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.returnData();
                                pvOptions.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.dismiss();
                            }
                        });
                    }
                })
                .build();
        pvOptions.setPicker(optionsItems);
        if (feedbackType >0) {
            pvOptions.setSelectOptions(feedbackType -1);
        }
        pvOptions.show();
    }

    private void submit(){
        RequestUtil.jsonObjectRequest(new RequestListener() {
            @Override
            public String getUrl() {
                return "/UserFeedback/commit/first";
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }

            @Override
            public JSONObject getJsonObj() {
                JSONObject object=new JSONObject();
                try {
                    object.putOpt("question",editText.getText().toString().trim());
                    object.putOpt("type", feedbackType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("getJsonObj============"+object);
                return object;
            }

            @Override
            public void onResponse(JSONObject jsonObj) {
                System.out.println("submit==============="+jsonObj);
                try {
                    int code=jsonObj.getInt("code");
                    if (code==1) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
