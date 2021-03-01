package com.example.cyan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.example.cyan.MyApplication;
import com.example.cyan.R;
import com.example.cyan.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chunyu Li
 * @File: MapActivity.java
 * @Package com.example.cyan.activity
 * @date 12/13/20 6:44 PM
 * @Description: Use Baidu SDK to render a map and locate your position
 */

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private Button buttonSaveLocation;
    private BDLocation location;
    private boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        setEvent();
        initMap();
        initLocation();
        initPermission();
    }

    private void initView() {
        mapView = findViewById(R.id.mapView);
        buttonSaveLocation = findViewById(R.id.buttonSaveLocation);
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
    }

    private void setEvent() {
        buttonSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUrlSearch urlSearch = ShareUrlSearch.newInstance();
                OnGetShareUrlResultListener listener = new MyOnGetShareUrlResultListener();
                urlSearch.setOnGetShareUrlResultListener(listener);
                urlSearch.requestLocationShareUrl(new LocationShareURLOption()
                        .location(new LatLng(location.getLatitude(), location.getLongitude()))
                        .name(MyApplication.getUser().getUsername()) // The name pf shared location
                        .snippet("The location of " + MyApplication.getUser().getUsername())); // The extra information of shared location
                Util.showSnackBar("blue", buttonSaveLocation, "Your location has been saved!", MapActivity.this);
                urlSearch.destroy();
            }
        });
    }

    private void initMap() {
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
    }

    private void initLocation() {
        locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // open GPS
        option.setCoorType("bd09ll"); // set the type of location
        option.setScanSpan(1000);
        locationClient.setLocOption(option);
        MyLocationListener myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
    }

    private void initPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this, permissions, 1);
        } else {
            locationClient.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Util.showSnackBar("red", buttonSaveLocation, "You have to agree with all permission to use location function!", MapActivity.this);
                            return;
                        }
                    }
                    locationClient.start();
                } else {
                    Util.showSnackBar("red", buttonSaveLocation, "Unknown error happened!", MapActivity.this);
                }
                break;
            default:
                break;
        }
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
    }

    class MyOnGetShareUrlResultListener implements OnGetShareUrlResultListener {
        @Override
        public void onGetPoiDetailShareUrlResult(ShareUrlResult shareUrlResult) {

        }

        @Override
        public void onGetLocationShareUrlResult(ShareUrlResult shareUrlResult) {
            String shareUrl = shareUrlResult.getUrl();
            MyApplication.setShareUrl(shareUrl);
        }

        @Override
        public void onGetRouteShareUrlResult(ShareUrlResult shareUrlResult) {

        }
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // mapView does not process new location after being destroyed
            if (location == null || mapView == null) {
                return;
            }
            MapActivity.this.location = location;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // set the direction
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation)
                navigateTo(location);
        }
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
}