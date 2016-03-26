package android.bishe.activity;


import android.bishe.Utils.MyOrientationListener;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.bishe.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.view.WindowManager;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class BaiDuActivity extends ActionBarActivity {


    private MapView mMapView;
    private BaiduMap mBaiduMap;
    static boolean isDisplayScreen = false;
    // 判断是否全屏，一定要用static修饰；否则失败。因为要保存上一次的状态；

    private Context context;

    // 定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;

    // 自定义定位图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;

    private LocationMode mLocationMode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_bai_du);

        this.context = this;

        initView();

        // 初始化定位
        initLocation();
    }

    private void initLocation() {


        mLocationMode = LocationMode.NORMAL;

        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);// 设置定位一秒钟请求一次；

        mLocationClient.setLocOption(option);

        // 初始化图标
        mIconLocation = BitmapDescriptorFactory
                .fromResource(R.drawable.navi_map_gps_locked);

        myOrientationListener = new MyOrientationListener(context);

        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {

            @Override
            public void onOrientationChanged(float x) {

                mCurrentX = x;
            }
        });


    }

    private void initView() {


        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        // 改变显示的比例尺
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate after = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(after);
        
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 开始定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }

        // 开启方向传感器
        myOrientationListener.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        // 停止方向传感器
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bai_du, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int item_id = item.getItemId();
        // int isDisplayScreen = getWindow().getAttributes().flags;
        switch (item_id) {
            case R.id.map_common:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

                break;
            case R.id.map_satellite:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("交通地图(on)");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("交通地图(off)");
                }
                break;

            case R.id.map_heat:

                if (mBaiduMap.isBaiduHeatMapEnabled()) {
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                    item.setTitle("热力图(on)");
                } else {
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                    item.setTitle("热力图(off)");
                }

                break;

            case R.id.display_fullscreen:

                if (isDisplayScreen == false) {// 当前非全屏，设置成全屏
                    // requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // 此处不能取消标题栏，否则会出错；
                    getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    isDisplayScreen = true;
                    item.setTitle("关闭全屏");
                } else {// 当前全屏，设置成非全屏
                    getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    item.setTitle("打开全屏");
                    isDisplayScreen = false;
                }

                break;

            case R.id.id_map_location:
                centerToMyLocation();

                break;

            case R.id.id_map_mode_common:
                mLocationMode = LocationMode.NORMAL;

                break;

            case R.id.id_map_mode_following:
                mLocationMode = LocationMode.FOLLOWING;
                break;

            case R.id.id_map_mode_compass:
                mLocationMode = LocationMode.COMPASS;
                break;

        }
        return super.onOptionsItemSelected(item);


    }






    // 定位到我的位置
    private void centerToMyLocation() {
        LatLng latlng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
        mBaiduMap.animateMapStatus(msu);

    }





    /**
     * 实现定位监听器BDLocationListener，重写onReceiveLocation方法；
     *
     * @author chenyufeng
     *
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();

            mBaiduMap.setMyLocationData(data);

            // 设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mLocationMode, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);

            // 更新经纬度
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            if (isFirstIn) {
                LatLng latlng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;

                Toast.makeText(context, location.getAddrStr(),
                        Toast.LENGTH_SHORT).show();
                Log.i("TAG",
                        location.getAddrStr() + "\n" + location.getAltitude()
                                + "" + "\n" + location.getCity() + "\n"
                                + location.getCityCode() + "\n"
                                + location.getCoorType() + "\n"
                                + location.getDirection() + "\n"
                                + location.getDistrict() + "\n"
                                + location.getFloor() + "\n"
                                + location.getLatitude() + "\n"
                                + location.getLongitude() + "\n"
                                + location.getNetworkLocationType() + "\n"
                                + location.getProvince() + "\n"
                                + location.getSatelliteNumber() + "\n"
                                + location.getStreet() + "\n"
                                + location.getStreetNumber() + "\n"
                                + location.getTime() + "\n");

                // 弹出对话框显示定位信息；
                Builder builder = new Builder(context);
                builder.setTitle("为您获得的定位信息：");
                builder.setMessage("当前位置：" + location.getAddrStr() + "\n"
                        + "城市编号：" + location.getCityCode() + "\n" + "定位时间："
                        + location.getTime() + "\n" + "当前纬度："
                        + location.getLatitude() + "\n" + "当前经度："
                        + location.getLongitude());
                builder.setPositiveButton("确定", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }


}
