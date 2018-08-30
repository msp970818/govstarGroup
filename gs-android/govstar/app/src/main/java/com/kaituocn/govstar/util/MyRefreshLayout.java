package com.kaituocn.govstar.util;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaituocn.govstar.R;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

public class MyRefreshLayout {


    public static void decorate(final TwinklingRefreshLayout refreshLayout, final RefreshListener refreshListener){
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setMaxHeadHeight(60);
        refreshLayout.setHeaderHeight(50);
        refreshLayout.setOverScrollHeight(30);
//        refreshLayout.setOverScrollTopShow(false);
//        refreshLayout.setOverScrollRefreshShow(false);
//        refreshLayout.setEnableRefresh(false);
        refreshLayout.setHeaderView(new IHeaderView() {
            TextView infoView;
            ImageView imageView;
            @Override
            public View getView() {
                View view = LayoutInflater.from(refreshLayout.getContext())
                        .inflate(R.layout.layout_refresh, null);
                infoView=view.findViewById(R.id.infoView);
                imageView=view.findViewById(R.id.imageView);
//                imageView.setVisibility(View.GONE);
                Glide.with(imageView.getContext()).load(R.drawable.loading).asGif().preload();
                return view;
            }

            @Override
            public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
                if (fraction < 1f) infoView.setText("下拉刷新");
                if (fraction > 1f)  infoView.setText("释放刷新");
                imageView.setVisibility(View.GONE);
            }

            @Override
            public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {

            }

            @Override
            public void startAnim(float maxHeadHeight, float headHeight) {
                infoView.setText("正在与政务云数据同步中…");
                Glide.with(imageView.getContext()).load(R.drawable.loading).asGif().into(imageView);
                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(OnAnimEndListener animEndListener) {
                animEndListener.onAnimEnd();
//                infoView.setText("同步完成！");
//                refreshLayout.finishRefreshing();
            }

            @Override
            public void reset() {
//                infoView.setText("正在与政务云数据同步中…");
            }
        });
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout layout) {
                refreshListener.refresh();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
            }
        });

    }

    public interface RefreshListener{
        void refresh();
    }
}
