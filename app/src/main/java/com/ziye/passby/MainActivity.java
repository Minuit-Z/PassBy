package com.ziye.passby;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Fragments.FriendsFragment;
import Fragments.MapArFragment;
import Fragments.StepsFragment;
import utils.Constants;
import utils.ParseJson;
import utils.SpUtil;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    private ImageView img_weather;
    private TextView tv_weather;
    private TextView tv_name;
    private ImageView img_pic;
    private BDLocation location1;
    private LocationManager locationManager;
    private String[] styleName = new String[]{"太原", "武乡", "默认"};

    private LocationClient locationClient = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.SHOW_RESPONSE: //展示天气信息
                    String response = (String) msg.obj;
                    String[] resultArray = response.split("!");
                    // 在这里进行UI操作，将结果显示到界面上
                    Log.i("_____天气____", resultArray.length + "");
                    if (resultArray.length != 1) {
                        tv_weather.setText(resultArray[0] + resultArray[1]);
                        img_weather.setImageResource(Constants.weatherIcon[Integer.parseInt(resultArray[3])]);
                    }
            }
        }
    };

    FragmentManager fragmentManager;
    NavigationView navigationView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Explode explode = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            explode = new Explode();
            explode.setDuration(500);
            getWindow().setExitTransition(explode);
            getWindow().setEnterTransition(explode);
        }

        fragmentManager = getSupportFragmentManager();

        setupView();

        if (savedInstanceState == null)
            showHome();

        initData();
    }



    /**
     * 通过百度地图的定位信息,获取聚合数据的天气信息
     *
     * @time 2016/9/7 17:14
     * @author ziye
     */
    private void initData() {

        locationClient = new LocationClient(this);
        //设置定位条件
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                /**
                 * 自动切换主题代码
                 */
//                if (location.getCity().equals("太原市") && SpUtil.getStyleCode(MainActivity.this) != 3) {
//                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
//                    builder2.setTitle("检测到您正在处于太原");
//                    builder2.setMessage("是否切换到太原主题");
//                    builder2.setPositiveButton("切换", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //切换北京主题
//                            Toast.makeText(MainActivity.this, "change", Toast.LENGTH_SHORT).show();
//                            SpUtil.setStyleCode(MainActivity.this, 3);
//                            Intent i3 = new Intent(MainActivity.this, MainActivity.class);
//                            startActivity(i3);
//                            overridePendingTransition(0, 0);
//                            finish();
//                        }
//                    });
//                    builder2.setNegativeButton("忽略", null);
//                    builder2.show();
//                }
                sendRequestWithHttpURLConnection(location);
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置GPS优先  // 设置GPS优先
//        option.setScanSpan();
        option.setIsNeedAddress(true);
        option.disableCache(true);//禁止启用缓存定位
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.requestLocation();
        sendRequestWithHttpURLConnection(location1);
    }

    /**
     * 获取经纬度信息
     *
     * @time 2016/9/7 10:59
     * @author ziye
     */

    private Location getLocation() {
        String contextService = Context.LOCATION_SERVICE;
        //通过系统服务，取得LocationManager对象
        locationManager = (LocationManager) getSystemService(contextService);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return location;
    }

    private void setupView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });

        //把NavigationView的header布局获取到,设置为v
        View v = navigationView.getHeaderView(0);

        tv_name = (TextView) v.findViewById(R.id.tv_name);
        tv_weather = (TextView) v.findViewById(R.id.tv_weather);
        img_pic = (ImageView) v.findViewById(R.id.img_pic);
        img_weather = (ImageView) v.findViewById(R.id.img_weather);

        tv_name.setText(SpUtil.getUserNamefromSP(MainActivity.this, null));
        switch (tv_name.getText().toString().trim()){
            case "ziye":
                img_pic.setImageResource(R.drawable.drawer_avatar);
             break;
            case "mtf":
                img_pic.setImageResource(R.drawable.imgm);
                break;
            case "王蕊芳":
                img_pic.setImageResource(R.drawable.imgw);
                break;
            case "wby":
                img_pic.setImageResource(R.drawable.imgwb);
                break;
            case "任星凯":
                img_pic.setImageResource(R.drawable.imgr);
                break;
            default:
                img_pic.setImageResource(R.drawable.drawer_avatar);
        }
        switch (SpUtil.getStyleCode(MainActivity.this)) {
            case 1:
                //默认主题
                v.findViewById(R.id.drawer_root).setBackgroundResource(R.mipmap.default_style);
                break;
            case 2:
                //武乡
                v.findViewById(R.id.drawer_root).setBackgroundResource(R.mipmap.wx_1);

                break;
            case 3:
                //太原
                v.findViewById(R.id.drawer_root).setBackgroundResource(R.mipmap.ty);
                break;
            default:
                v.findViewById(R.id.drawer_root).setBackgroundResource(R.drawable.drawer_background);
        }

    }

    private void showHome() {
        selectDrawerItem(navigationView.getMenu().getItem(0));
        drawerLayout.openDrawer(GravityCompat.START);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void selectDrawerItem(MenuItem menuItem) {
        boolean specialToolbarBehaviour = false;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.drawer_step:
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_null);
                fragmentClass = StepsFragment.class;
                break;
            case R.id.drawer_mapAR:
                fragmentClass = MapArFragment.class;
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_ar);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //切换ar界面
                        Intent i = new Intent();
                        i.setAction("ziye.PassBy.AR");
                        i.addCategory("ziye.PassBy.AR");
                        startActivity(i);
                        return true;
                    }
                });
                specialToolbarBehaviour = true;
                break;
            case R.id.drawer_share:
                fragmentClass = FriendsFragment.class;
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_add_share);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //添加好友界面
                        startActivity(new Intent(MainActivity.this, AddFriendActivity.class));
                        return true;
                    }
                });
                break;
            case R.id.drawer_style:
                //切换主题,
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.ic_ar);
                builder.setTitle("选择需要的主题");
                builder.setItems(styleName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (styleName[which]) {
                            case "默认":
                                Toast.makeText(MainActivity.this, "默认主题", Toast.LENGTH_SHORT).show();
                                SpUtil.setStyleCode(MainActivity.this, 1);
                                Intent i = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(i);
                                overridePendingTransition(0, 0);
                                finish();
                                //重启activity
                                break;
                            case "太原":
                                Toast.makeText(MainActivity.this, "太原主题", Toast.LENGTH_SHORT).show();
                                SpUtil.setStyleCode(MainActivity.this, 3);
                                Intent i2 = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(i2);
                                overridePendingTransition(0, 0);
                                finish();
                                break;
                            case "武乡":
                                Toast.makeText(MainActivity.this, "武乡主题", Toast.LENGTH_SHORT).show();
                                SpUtil.setStyleCode(MainActivity.this, 2);
                                Intent i3 = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(i3);
                                overridePendingTransition(0, 0);
                                finish();
                                break;
                        }
                    }
                });
                builder.show();

            default:
                fragmentClass = StepsFragment.class;
                break;
        }

        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentTransaction t;
            t = fragmentManager.beginTransaction();
            t.addToBackStack("tag");
            t.replace(R.id.content_frame, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setToolbarElevation(specialToolbarBehaviour);
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setToolbarElevation(boolean specialToolbarBehaviour) {
        if (specialToolbarBehaviour) {
            toolbar.setElevation(0.0f);
            frameLayout.setElevation(getResources().getDimension(R.dimen.elevation_toolbar));
        } else {
            toolbar.setElevation(getResources().getDimension(R.dimen.elevation_toolbar));
            frameLayout.setElevation(0.0f);
        }
    }

    public void showSnackbarMessage(View v) {
        EditText et_snackbar = (EditText) findViewById(R.id.et_snackbar);
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);
        View view = findViewById(R.id.coordinator_layout);
        if (et_snackbar.getText().toString().isEmpty()) {
            textInputLayout.setError(getString(R.string.alert_text));
        } else {
            textInputLayout.setErrorEnabled(false);
            et_snackbar.onEditorAction(EditorInfo.IME_ACTION_DONE);
            Snackbar.make(view, et_snackbar.getText().toString(), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(android.R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Do nothing
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        drawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.menu_add_share);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 获取Json数据(天气)
     *
     * @time 2016/8/5 14:43
     * @author ziye
     */
    public StringBuilder getJson(URL url, HttpURLConnection connection, BDLocation location) {

        StringBuilder response = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            // 下面对获取到的输入流进行读取
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 连接网络,通过Location信息获取天气情况,信息通过子线程handler发送
     *
     * @param location
     * @time 2016/8/5 14:42
     * @author ziye
     */
    protected void sendRequestWithHttpURLConnection(final BDLocation location) {

        if (location != null) {
            new Thread() {
                @Override
                public void run() {
                    URL url;
                    HttpURLConnection connection = null;
                    try {
                        url = new URL("http://v.juhe.cn/weather/geo?format=2&" +
                                "key=b8f14e3b635af168027d9fe093cb9204&" +
                                "lon=" + location.getLongitude() + "&" +
                                "lat=" + location.getLatitude());
                        StringBuilder response = getJson(url, connection, location);
                        String resultArray;
                        resultArray = ParseJson.parseWeatherWithJSON(response.toString());
                        // 将服务器返回的结果存放到Message中
                        Message message = new Message();
                        message.what = Constants.SHOW_RESPONSE;
                        message.obj = resultArray;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            location1 = location;
        }
    }
}
