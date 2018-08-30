package com.kaituocn.govstar.work.worklist;

import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class WorkAcceptActivity extends AppCompatActivity {

    TagFlowLayout tagFlowLayout1;
    TagFlowLayout tagFlowLayout2;
    TagFlowLayout tagFlowLayout3;
    TagFlowLayout tagFlowLayout4;

    CheckBox checkBox;
    List<String> process=new ArrayList<>();
    TagAdapter processAdapter;

    OptionsPickerView pvOptions;
    TextView quickView;
    EditText editText;

    boolean showAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_work_accept);

        showAction=getIntent().getBooleanExtra("showAction",false);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("任务验收");
        View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (showAction) {
            TextView actionView = findViewById(R.id.actionView);
            actionView.setVisibility(View.VISIBLE);
            actionView.setText("完成");
            findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.bottomLayout).setVisibility(View.GONE);
        }

        tagFlowLayout1=findViewById(R.id.frameLayout2);
        tagFlowLayout2=findViewById(R.id.frameLayout3);
        tagFlowLayout3=findViewById(R.id.frameLayout5);
        tagFlowLayout4=findViewById(R.id.frameLayout4);

        List<String> list=new ArrayList<>();
        list.add("蓟州区渔阳镇人民政府");
        list.add("蓟州区卫生和计划生育委员会");
        list.add("蓟州区卫生委员会");
        tagFlowLayout1.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag,parent, false);
                TextView textView=view.findViewById(R.id.textView);
                int color = 0;
                if (position==0) {
                    color=R.color.tag_green;
                }else if(position==1){
                    color=R.color.tag_red;
                }else if(position==2){
                    color=R.color.tag_gray;
                }
                Util.setBackgroundTint(textView,color);
                textView.setText(s);


                return view;
            }
        });
        tagFlowLayout2.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex,parent, false);
                TextView textView1=view.findViewById(R.id.textView1);
                TextView textView2=view.findViewById(R.id.textView2);
                textView1.setText("附件"+(position+1)+"：");
                textView2.setText("123456-"+(position+1)+".jpg");


                return view;
            }
        });

        tagFlowLayout3.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_annex,parent, false);
                TextView textView1=view.findViewById(R.id.textView1);
                TextView textView2=view.findViewById(R.id.textView2);
                textView1.setText("附件"+(position+1)+"：");
                textView2.setText("654321-"+(position+1)+".jpg");


                return view;
            }
        });

        for (int i = 0; i <3 ; i++) {
            process.add(" "+i);
        }
        processAdapter=new TagAdapter<String>(process) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_work_do_process,parent, false);
                Group group=view.findViewById(R.id.group);
                View dividerOver=view.findViewById(R.id.dividerOver);
                if (position==0) {
                    dividerOver.setVisibility(View.VISIBLE);
                }else{
                    dividerOver.setVisibility(View.GONE);

                }
                if (position%2==0) {
                    group.setVisibility(View.VISIBLE);
                }else{
                    group.setVisibility(View.GONE);
                }

                return view;
            }
        };
        tagFlowLayout4.setAdapter(processAdapter);

        checkBox=findViewById(R.id.checkBox3);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    process.clear();
                    for (int i = 0; i <10 ; i++) {
                        process.add(" "+i);
                    }
                    processAdapter.notifyDataChanged();
                }else{
                    process.clear();
                    for (int i = 0; i <3 ; i++) {
                        process.add(" "+i);
                    }
                    processAdapter.notifyDataChanged();
                }
            }
        });


        editText=findViewById(R.id.editText6);
        quickView=findViewById(R.id.quickView);
        quickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> optionsItems = new ArrayList<>();
                optionsItems.add("已阅");
                optionsItems.add("请王主任办理");
                optionsItems.add("请张主任办理");
                optionsItems.add("请李主任办理");
                optionsItems.add("请高主任办理");
                initOptionsPicker(optionsItems,editText);
            }
        });

    }

    private void initOptionsPicker(final List<String> optionsItems, final View view) {

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (view instanceof TextView) {
                    ((TextView) view).setText(optionsItems.get(options1));
                }
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
        pvOptions.show();
    }
}
