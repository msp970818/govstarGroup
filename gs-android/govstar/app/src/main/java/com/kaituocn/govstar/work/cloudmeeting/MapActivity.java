package com.kaituocn.govstar.work.cloudmeeting;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.kaituocn.govstar.R;
import com.kaituocn.govstar.util.Util;

public class MapActivity extends AppCompatActivity {

    MapView mMapView ;
    AMap aMap;

    GeocodeSearch geocoderSearch;

    String address;
    String latlonStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setTransparentStatusBar(getWindow());
        setContentView(R.layout.activity_map);

        TextView titleView=findViewById(R.id.titleView);
        titleView.setText("选择会议室位置");
        final View backView=findViewById(R.id.backView);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView actionView = findViewById(R.id.actionView);
        actionView.setVisibility(View.VISIBLE);
        actionView.setText("确定");
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("address",address);
                intent.putExtra("latlon",latlonStr);
                setResult(RESULT_OK,intent);
                finish();
            }
        });


        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;
//        myLocationStyle.interval(3000);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Bundle bundle= location.getExtras();
                if (bundle != null&&bundle.getString("PoiName")!=null&&!TextUtils.isEmpty(bundle.getString("PoiName"))) {
                    address=bundle.getString("PoiName");
                    LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
                    latlonStr=latLonPoint.toString();
                    Toast.makeText(getBaseContext(),address , Toast.LENGTH_LONG).show();
                }else{
                    LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
                    latlonStr=latLonPoint.toString();
                    getAddress(latLonPoint);
                }
//                System.out.println("getExtras========="+location.getExtras());
//                System.out.println("location======"+location.toString());
            }
        });
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
                aMap.clear();
                aMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                latlonStr=latLonPoint.toString();
                getAddress(latLonPoint);

            }
        });

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null&&!TextUtils.isEmpty(result.getRegeocodeAddress().getFormatAddress())) {
                        address = result.getRegeocodeAddress().getFormatAddress();
                        Toast.makeText(getBaseContext(),address , Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getBaseContext(),"无法获取地址" , Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getBaseContext(),"无法获取地址" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });


    }


    private void getAddress(LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
