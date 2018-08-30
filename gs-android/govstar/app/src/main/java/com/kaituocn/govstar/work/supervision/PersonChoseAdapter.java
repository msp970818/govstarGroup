package com.kaituocn.govstar.work.supervision;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaituocn.govstar.R;
import com.kaituocn.govstar.entity.LevelUnitEntity;
import com.kaituocn.govstar.entity.LevelUnitTagEntity;
import com.kaituocn.govstar.entity.PersonEntity;
import com.kaituocn.govstar.util.BaseViewHolder;
import com.kaituocn.govstar.util.Util;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class PersonChoseAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    public final static int TYPE_SCHEDULE_PERSON=1;
    public final static int TYPE_WORK_CREATESUPERVISION_UNIT=2;
    public final static int TYPE_LEVEL_UNIT=3;
    public final static int TYPE_LEVEL_UNIT_TAG=4;

    private RecyclerView recyclerView;
    private Context context;
    private int type;

    private List<Object> list = new ArrayList<>();
    private List<Object> copyList = new ArrayList<>();
    private List<Object> tempList = new ArrayList<>();

    private List<Integer> resultIds;

    private boolean isPerson;


    private ArrayList<Parcelable> entityList=new ArrayList<>();

    public PersonChoseAdapter(Context context) {
        this.context = context;
    }

    public PersonChoseAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public List<Object> getList() {
        return list;
    }

    public void searchData(String string){
        if (copyList.isEmpty()) {
            copyList.addAll(list);
        }
        list.clear();
        if (TextUtils.isEmpty(string)) {
            list.addAll(copyList);
        }else {
            for (Object object : copyList) {
                if (object instanceof PersonEntity) {
                    if (((PersonEntity) object).getName().contains(string)) {
                        list.add(object);
                    }
                }else if(object instanceof LevelUnitTagEntity){
                    if (((LevelUnitTagEntity) object).getName().contains(string)){
                        list.add(object);
                    }else {
                        List<LevelUnitTagEntity.ChildItem> childItems= ((LevelUnitTagEntity) object).getChildren();
                        List<LevelUnitTagEntity.ChildItem> temp=new ArrayList<>();
                        for (LevelUnitTagEntity.ChildItem childItem : childItems) {
                            if (childItem.getName().contains(string)) {
                                temp.add(childItem);
                            }
                        }
                        if (!temp.isEmpty()) {
                            LevelUnitTagEntity entity=new LevelUnitTagEntity();
                            entity.setId(((LevelUnitTagEntity) object).getId());
                            entity.setName(((LevelUnitTagEntity) object).getName());
                            entity.setChildren(temp);
                            list.add(entity);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setResultIds(List<Integer> resultIds) {
        this.resultIds = resultIds;
    }

    public boolean isPerson() {
        return isPerson;
    }

    public void setPerson(boolean person) {
        isPerson = person;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setData(List<? extends Object> objects, boolean isAdd) {
        if (objects==null) {
            return;
        }
        if (!isAdd) {
            list.clear();
        }
        for (Object object : objects) {
            list.add(object);
        }
        entityList.clear();
        copyList.clear();

        if(resultIds!=null&&!resultIds.isEmpty()){
            for (Object o : list) {
                if (o instanceof PersonEntity) {
                    if (resultIds.contains(((PersonEntity) o).getId())) {
                        ((PersonEntity) o).setCheck(true);
                    }
                }
            }
        }
    }



    public ArrayList<Parcelable> getEntityList() {
        entityList.clear();
        for (Object o : list) {
            if (o instanceof PersonEntity) {
                if (((PersonEntity) o).isCheck()) {
                    entityList.add((Parcelable) o);
                }
            }else if(o instanceof LevelUnitEntity){
                if (((LevelUnitEntity) o).isCheck()) {
                    entityList.add((Parcelable) o);
                }
            }else if(o instanceof LevelUnitTagEntity){
                List<LevelUnitTagEntity.ChildItem> childItems=((LevelUnitTagEntity) o).getChildren();
                for (LevelUnitTagEntity.ChildItem childItem : childItems) {
                    if (childItem.isCheck()) {
                        PersonEntity personEntity=new PersonEntity();
                        personEntity.setId(childItem.getId());
                        personEntity.setName(childItem.getName());
                        entityList.add(personEntity);
                    }
                }
            }

        }
        return entityList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView=recyclerView;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType==TYPE_SCHEDULE_PERSON) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_createvision_leader, parent, false);
            return new PersonViewHolder(view);
        }

        if (viewType==TYPE_WORK_CREATESUPERVISION_UNIT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_createvision_unit, parent, false);
            return new UnitViewHolder(view);
        }
        if (viewType==TYPE_LEVEL_UNIT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_createvision_level, parent, false);
            return new LevelUnitHolder(view);
        }
        if (viewType==TYPE_LEVEL_UNIT_TAG) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_work_createvision_level2, parent, false);
            return new LevelUnitTagHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            holder.updateUI(list.get(position));
    }



    private class PersonViewHolder extends BaseViewHolder {
        View mView;
        ImageView headView;
        TextView nameView;
        CheckBox checkBox;
        PersonEntity entity;
        public PersonViewHolder(View view) {
            super(view);
            mView = view;
            checkBox=view.findViewById(R.id.checkBox);
            nameView=view.findViewById(R.id.nameView);
            headView=view.findViewById(R.id.headView);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    entity.setCheck(isChecked);
                    System.out.println("isChecked==========="+isChecked);
                }
            });
        }

        @Override
        public void updateUI(Object object) {
            entity= (PersonEntity) object;
            nameView.setText(entity.getName());
            Util.setAvatar(context,entity.getAvatarUrl(),headView);
            checkBox.setChecked(entity.isCheck());
        }
    }


    private class UnitViewHolder extends BaseViewHolder {
        View mView;
        TextView nameView;
        CheckBox checkBox;
        PersonEntity entity;
        public UnitViewHolder(View view) {
            super(view);
            mView = view;
            checkBox=view.findViewById(R.id.checkBox);
            nameView=view.findViewById(R.id.nameView);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    entity.setCheck(isChecked);

                }
            });
        }

        @Override
        public void updateUI(Object object) {
            entity= (PersonEntity) object;
            nameView.setText(entity.getName());
            checkBox.setChecked(entity.isCheck());

        }
    }

    SparseBooleanArray topArray=new SparseBooleanArray();
    SparseBooleanArray topCheckArray=new SparseBooleanArray();
    SparseBooleanArray itemArray=new SparseBooleanArray();


    private class LevelUnitHolder extends BaseViewHolder implements CompoundButton.OnCheckedChangeListener{
        View mView;
        TextView titleNameView,titleCheckView,itemNameView;
        CheckBox itemView;
        Group group1,group2;
        ImageView headView;
        ImageView arrowView;
        LevelUnitEntity entity;

        public LevelUnitHolder(View view) {
            super(view);
            mView = view;

            arrowView=view.findViewById(R.id.arrowView);
            titleNameView=view.findViewById(R.id.titleNameView);
            titleCheckView=view.findViewById(R.id.titleCheckView);
            itemView=view.findViewById(R.id.itemView);
            itemNameView=view.findViewById(R.id.itemNameView);
            titleNameView.setOnClickListener(this);
            titleCheckView.setOnClickListener(this);
            itemView.setOnCheckedChangeListener(this);

            group1=view.findViewById(R.id.group1);
            group2=view.findViewById(R.id.group2);
            headView=view.findViewById(R.id.headView);
            headView.setVisibility(View.GONE);

        }

        @Override
        public void updateUI(Object object) {
            entity= (LevelUnitEntity) object;
            if (showTitle(getAdapterPosition())) {
                group1.setVisibility(View.VISIBLE);
            }else{
                group1.setVisibility(View.GONE);
            }
            if (entity.isShowItem()) {
                if (isPerson) {
                    headView.setVisibility(View.VISIBLE);
                    Util.setAvatar(context,entity.getAvatarUrl(),headView);
                }
                group2.setVisibility(View.VISIBLE);
            }else{
                group2.setVisibility(View.GONE);
                if (isPerson) {
                    headView.setVisibility(View.GONE);
                }
            }

            titleNameView.setText(entity.getTopName());
            itemNameView.setText(entity.getName());

            if (topArray.get(getAdapterPosition())) {
                arrowView.setRotation(90);
            }else{
                arrowView.setRotation(0);
            }
            if (itemArray.get(getAdapterPosition())) {
               itemView.setChecked(true);
            }else{
                itemView.setChecked(false);
            }



        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.titleNameView:
                    topArray.put(getAdapterPosition(),!topArray.get(getAdapterPosition()));
                    for (Object object : list) {
                        if (((LevelUnitEntity)object).getTopId()==entity.getTopId()) {
                            ((LevelUnitEntity)object).setShowItem(topArray.get(getAdapterPosition()));
                        }
                    }
                    break;
                case R.id.titleCheckView:
                    topCheckArray.put(getAdapterPosition(),!topCheckArray.get(getAdapterPosition()));
                    for (Object object : list) {
                        if (((LevelUnitEntity)object).getTopId()==entity.getTopId()) {
                            ((LevelUnitEntity)object).setCheck(topCheckArray.get(getAdapterPosition()));
                        }
                    }

                    break;
            }
            notifyDataSetChanged();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            itemArray.put(getAdapterPosition(),isChecked);
            entity.setCheck(isChecked);
        }


        private boolean showTitle(int position){
            int preType=position==0?-1:((LevelUnitEntity)list.get(position-1)).getTopId();
            int curType=((LevelUnitEntity)list.get(position)).getTopId();
            if (preType==curType) {
                return false;
            }
            return true;
        }


    }


    SparseBooleanArray checkArray=new SparseBooleanArray();
    private class LevelUnitTagHolder extends BaseViewHolder implements CompoundButton.OnCheckedChangeListener{
        View mView;
        CheckBox checkBox;
        TagFlowLayout tagFlowLayout;
        TagAdapter tagAdapter;
        List<LevelUnitTagEntity.ChildItem> tagList=new ArrayList<>();

        LevelUnitTagEntity entity;
        public LevelUnitTagHolder(View view) {
            super(view);
            mView = view;
            checkBox=view.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(this);
            tagFlowLayout=view.findViewById(R.id.tagFlowLayout);
            tagFlowLayout.setVisibility(View.GONE);
            tagAdapter=new TagAdapter<LevelUnitTagEntity.ChildItem>(tagList) {
                @Override
                public View getView(FlowLayout parent, int position, final LevelUnitTagEntity.ChildItem childItem) {
                    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_createvision_level_tag,parent, false);
                    TextView nameView=view.findViewById(R.id.nameView);
                    nameView.setText(childItem.getName());
                    if (isPerson) {
                        ImageView headView=view.findViewById(R.id.headView);
                        Util.setAvatar(context,childItem.getAvatarUrl(),headView);
                    }
                    CheckBox checkBox=view.findViewById(R.id.itemView);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            childItem.setCheck(isChecked);
                        }
                    });
                    checkBox.setChecked(childItem.isCheck());
                    return view;
                }
            };
            tagFlowLayout.setAdapter(tagAdapter);
        }

        @Override
        public void updateUI(Object object) {
            entity= (LevelUnitTagEntity) object;
            checkBox.setText(entity.getName());
            checkBox.setChecked(checkArray.get(entity.getId()));
            tagList.clear();
            tagList.addAll(entity.getChildren());
            tagAdapter.notifyDataChanged();

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            checkArray.put(entity.getId(),isChecked);
            tagFlowLayout.setVisibility(isChecked?View.VISIBLE:View.GONE);
        }
    }





}
