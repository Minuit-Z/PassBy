package com.ziye.passby;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class Zhoubian extends Activity {

    public LocationClient locationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    public MapView mapView;
    BaiduMap baiduMap;

    private RelativeLayout relativeLayout;
    private Context context;

    //构建Marker图标
    public BitmapDescriptor bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_zhoubian);

        this.context = this;
        mapView = (MapView) findViewById(R.id.mapView);
        relativeLayout = (RelativeLayout) findViewById(R.id.id_maker_ly);
        bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_openmap_mark);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);//打开定位图层

        //声明Client
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myListener);
        initLocation();
        final LatLng pointHcXf = new LatLng(35.519979, 112.588586);
        final LatLng pointNucMain = new LatLng(38.020234, 112.455875);
        final LatLng pointNucDehuai = new LatLng(38.016534, 112.44902);
        final LatLng pointWuXiang = new LatLng(36.840085,112.864219);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ImageView imageView = (ImageView) relativeLayout
                        .findViewById(R.id.id_info_img);
                InfoWindow infoWindow;
                TextView tv = new TextView(context);
                tv.setPadding(30, 20, 30, 50);
                tv.setTextColor(Color.parseColor("#ffffff"));
                Log.i("99999999999999999", marker.getPosition().toString());
                if (marker.getPosition().toString().equals(pointHcXf.toString())) {
                    imageView.setImageResource(R.mipmap.hcxf);
                }else if (marker.getPosition().toString().equals(pointNucDehuai.toString())){
                    imageView.setImageResource(R.mipmap.dehuai);
                }else if (marker.getPosition().toString().equals(pointNucMain.toString())){
                    imageView.setImageResource(R.mipmap.zhulou);
                }else if(marker.getPosition().toString().equals(pointWuXiang.toString())){
                    imageView.setImageResource(R.mipmap.wxvideo);
                }
                final LatLng latLng = marker.getPosition();
                infoWindow = new InfoWindow(tv, latLng, -47);
                baiduMap.showInfoWindow(infoWindow);
                relativeLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                relativeLayout.setVisibility(View.GONE);
                baiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


    }



    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 初始化配置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系

//        option.setScanSpan(10000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.requestLocation();
    }

    /**
     * 监听类
     */
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            sb.append("\nstreet : ");
            sb.append(location.getStreet());
            sb.append("\nstreetNum : ");
            sb.append(location.getStreetNumber());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }

            Log.e("log", sb.toString());
            Log.e("addr", location.getAddrStr());
//            Toast.makeText(getApplicationContext(), location.getAddrStr() + "city" + location.getCity(), Toast.LENGTH_LONG).show();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);

            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        }
    }
    //35.5199790000,112.5885860000皇城相府
    //38.0165340000,112.4490200000德怀楼
    //主楼112.455875,38.020234
    //112.864219,36.840085武乡

    /**
     * 找到AR景点
     *
     * @param v
     */
    public void mark(View v) {
        //定义Maker坐标点
        //皇城相府
        LatLng pointHcXf = new LatLng(35.5199790000, 112.5885860000);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(pointHcXf)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);

        //德怀楼
        LatLng deHuaiLou = new LatLng(38.0165340000, 112.4490200000);
        OverlayOptions option2 = new MarkerOptions()
                .position(deHuaiLou)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option2);

        //主楼
        LatLng zhuLou = new LatLng(38.020234, 112.455875);
        OverlayOptions option3 = new MarkerOptions()
                .position(zhuLou)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option3);
        //武乡
        LatLng wuxiang = new LatLng(36.840085, 112.864219);
        OverlayOptions option4 = new MarkerOptions()
                .position(wuxiang)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option4);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(10.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 定位我的位置
     *
     * @param view
     */
    public void loc(View view) {
        initLocation();
    }
}
