package example.android.laioh.bshop.activity;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.adapter.MainShopListAdapter;
import example.android.laioh.bshop.adapter.MyShopListAdapter;
import example.android.laioh.bshop.model.ShopInformation;
import example.android.laioh.bshop.util.GpsInfo;
import example.android.laioh.bshop.util.RbPreference;
import example.android.laioh.bshop.util.RequestHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    private CentralManager centralManager;
    private final int REQUEST_ENABLE_BT = 1000;
    private Button beacon_sacn;

    private ListView shop_listview;
    private MapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private GpsInfo mGps;
    private Double mLat;
    private Double mLon;
    private static LatLng nowAddress;
    private String mNowAddressKorea;

    private int BOTTOM_CASEVAL1 = 1;
    private int BOTTOM_CASEVAL2 = 2;


    private RbPreference mPref = new RbPreference(this);
    private int mNotificationID;

    private boolean layoutFlag = true;

    private RelativeLayout mapview_layout;
    private LinearLayout navigation_view;
    private TextView user_login_tv;
    private ImageView user_profile_iv;
    private TextView user_nick_tv;

    private MainShopListAdapter mAdapter;
    private ArrayList<ShopInformation> items;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGps = new GpsInfo(this);
        this.mLat = mGps.getLatitude();
        this.mLon = mGps.getLongitude();
        nowAddress = new LatLng(mLat,mLon);

        mNowAddressKorea = mGps.getAddress(getApplicationContext(), mLat, mLon);
        mGoogleMap.setMyLocationEnabled(true);
        Marker nowPosition = mGoogleMap.addMarker(new MarkerOptions().position(nowAddress).title(mNowAddressKorea));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowAddress, 15));
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38, 127),7));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    private void getMarkerItem(ArrayList<ShopInformation> item){

        for (ShopInformation markerItem : item){
            //Log.e("count : " , Integer.toString(count++));
            addMarker(markerItem, false);
        }
    }

    private Marker addMarker(ShopInformation markerItem, boolean isSelecteMarker){
        LatLng position = new LatLng(Double.parseDouble(markerItem.getLat()), Double.parseDouble(markerItem.getLon()));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(markerItem.getName());
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        //markerOptions.snippet(markerItem.getId());

        /*Log.e("Marker id : ", Integer.toString(markerItem.getId()));
        Log.e("Marker dialect : ", markerItem.getDialect());
        Log.e("Marker lat : ", Double.toString(markerItem.getLatitude()));
        Log.e("Marker lon : ", Double.toString(markerItem.getLongitude()));*/


        Log.e("Marker Options", String.valueOf(markerOptions));
        return mGoogleMap.addMarker(markerOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*beacon_sacn = (Button) findViewById(R.id.scan_button);
        beacon_sacn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BeaconScanListActivity.class);
                startActivity(intent);
            }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //mPref.removeAllValue();
                if (layoutFlag) {
                    shop_listview.setVisibility(View.INVISIBLE);
                    mapview_layout.setVisibility(View.VISIBLE);
                    layoutFlag = false;
                } else {
                    getShopListFromServer();
                    shop_listview.setVisibility(View.VISIBLE);
                    mapview_layout.setVisibility(View.INVISIBLE);
                    layoutFlag = true;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        user_login_tv = (TextView) header.findViewById(R.id.navigation_user_login);
        user_nick_tv = (TextView) header.findViewById(R.id.navigation_user_nick);
        user_profile_iv = (ImageView) header.findViewById(R.id.navigation_user_profile_imageView);

        navigation_view = (LinearLayout) header.findViewById(R.id.navigation_view);


        navigation_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSettingDialog();
            }
        });

        centralManager = CentralManager.getInstance();
        centralManager.init(getApplicationContext());


        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, final Peripheral peripheral) {
                // TODO do something with the scanned peripheral(beacon)
                Log.i("ExampleActivity", "peripheral : " + peripheral);
                String getperi = peripheral.getBDAddress();
                String getaddress = mPref.getValue(getperi, "");
                if (!getperi.equals(getaddress)) {
                    GetBeaconEventInfoTask task = new GetBeaconEventInfoTask();
                    task.execute(getperi);
                }
            }
        });

        if(!centralManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        init();
        centralManager.startScanning();
        checkForLogin();

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","all");
        } else if (id == R.id.nav_cafe) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","카페");
        } else if (id == R.id.nav_food) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","음식점");
        } else if (id == R.id.nav_clothes) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","의류");
        } else if (id == R.id.nav_bakery) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","베이커리");
        } else if (id == R.id.nav_fitness) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","휘트니스");
        } else if (id == R.id.nav_alcohol) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","술집");
        } else if (id == R.id.nav_sports) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","스포츠");
        } else if (id == R.id.nav_cosmetics) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","화장품");
        } else if (id == R.id.nav_icecream) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","아이스크림");
        } else if (id == R.id.nav_faststore) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","편의점");
        } else if (id == R.id.nav_copy) {
            GetShopListTask task = new GetShopListTask();
            task.execute("*","제본");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void init() {
        //setCentralManager();
        shop_listview = (ListView) findViewById(R.id.main_shop_listview);
        mapview_layout = (RelativeLayout) findViewById(R.id.mapview_layout);
        items = new ArrayList<>();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (layoutFlag) {
            shop_listview.setVisibility(View.VISIBLE);
            mapview_layout.setVisibility(View.INVISIBLE);
        } else {
            shop_listview.setVisibility(View.INVISIBLE);
            mapview_layout.setVisibility(View.VISIBLE);

        }
        getShopListFromServer();
        //getMarkerItem();
        terminateIfNotBLE();
        turnOnBluetooth();
    }


    private void terminateIfNotBLE() {
        if(!centralManager.isBLESupported()) {
            //Toast.makeText(this, R.string.error_ble_not_support, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void turnOnBluetooth() {
        if(!centralManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void userSettingDialog(){
        /*
         * 로그인이 되어있는 경우 navigation header를 눌러 원하는 action을 구분하는 함수
         *
         * 로그아웃 : 이미 회원가입을 한 상태이므로 login값을 logout으로 바꿔준다.
         * 마이페이지 : 마이페이지 화면으로 화면 전환
         */
        openBottomSheet(R.string.bottom_sheet_title_mypage, R.string.bottom_sheet_mypage, R.string.bottom_sheet_logout,BOTTOM_CASEVAL1);
    }

    private void checkForLogin(){

        /*
         * 회원가입 유무에 따른 action 설정 함수
         * 로그인 상태 ("login","login")
         * 로그아웃 상태 ("login","logout")
         * 미가입 회원 ("login","")
         *
         * 각 상태를 확인 후 dialog를 띄워 해당 action을 수행
         */
        String getLoginCheck = mPref.getValue("login","");
        if(getLoginCheck.equals("")){
            //기존 회원이 아닌경우

            openBottomSheet(R.string.bottom_sheet_title_member, R.string.bottom_sheet_login, R.string.bottom_sheet_signup,BOTTOM_CASEVAL2);

        } else if (getLoginCheck.equals("logout")) {

            openBottomSheet(R.string.bottom_sheet_title_member, R.string.bottom_sheet_login, R.string.bottom_sheet_signup,BOTTOM_CASEVAL2);

        } else if (getLoginCheck.equals("login")){
            //현재 로그인 되어있는 경우
            //new CreateAuthUtil(getApplicationContext()).execute(mPref.getValue("user_num", ""), mPref.getValue("device_id", ""), mPref.getValue("gcm_reg_id", ""));
            user_login_tv.setText(mPref.getValue("user_id", ""));
            user_nick_tv.setText(mPref.getValue("user_nick", ""));
        }
    }


    private void openBottomSheet(int titleVal, int cateVal1, int cateVal2, final int caseVal){
        Log.e("openBottomSheet", "Open");
        /*
         * Create by Lai.OH 2016.07.27
         *
         * 밑에서 올라오는 화면 구성 함수
         * 로그인, 회원가입, 로그아웃, 마이페이지 메뉴를 관리 할 수 있는 서랍형식의 레이아웃생성
         * params (BottomSheet Title, First Button text, Second Button text, BottomSheet Caseval)
         * BottomSheet Case :
         *      1 : 기존의 회원인경우 마이페이지 및 로그아웃 설정
         *      2 : 기존의 회원이거나 회원이 아닌경우 로그인 및 회원가입 설정
         */

        View view = getLayoutInflater().inflate(R.layout.custom_bottom_sheet, null);
        TextView title_tv = (TextView) view.findViewById(R.id.bottomsheet_title);
        TextView cate1_tv = (TextView) view.findViewById(R.id.bottomsheet_cate1);
        TextView cate2_tv = (TextView) view.findViewById(R.id.bottomsheet_cate2);
        TextView calcel_tv = (TextView) view.findViewById(R.id.bottomsheet_cancel);

        title_tv.setText(titleVal);
        cate1_tv.setText(cateVal1);
        cate2_tv.setText(cateVal2);

        final Dialog mBottomSheetDialog = new Dialog(MainActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView (view);
        mBottomSheetDialog.setCancelable (false);
        mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);
        mBottomSheetDialog.show ();

        cate1_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (caseVal) {
                    case 1 :
                        intent = new Intent(MainActivity.this, MyPageActivity.class);
                        startActivity(intent);
                        mBottomSheetDialog.dismiss();
                        finish();

                        break;
                    case 2 :
                        intent = new Intent(MainActivity.this, SigninActivity.class);
                        startActivity(intent);
                        mBottomSheetDialog.dismiss();
                        finish();
                        break;
                }
            }
        });

        cate2_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (caseVal) {
                    case 1 :
                        mPref.removeAllValue();
                        mPref.put("login","logout");
                        finish();
                        break;
                    case 2 :
                        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(intent);
                        mBottomSheetDialog.dismiss();
                        finish();
                        break;
                }
            }
        });

        calcel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (caseVal) {
                    case 2 :
                        mBottomSheetDialog.dismiss();
                        finish();
                        break;
                    default:
                        mBottomSheetDialog.dismiss();
                }
            }
        });
    }




    class GetBeaconEventInfoTask extends AsyncTask<String, Void, String> {

        private String beaconid = "";
        private String shopid = "";
        private String shopname = "";
        private String eventname = "";
        private String eventcontent = "";
        private String event = "";

        private String mac;
        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_get_beacon_info.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (event.equals("1")) {
                mPref.put(mac, mac);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(getApplicationContext(), NotifiCouponActivity.class);
                intent.putExtra("beaconid", beaconid);
                intent.putExtra("shopid", shopid);
                intent.putExtra("shopname", shopname);
                intent.putExtra("eventname", eventname);
                intent.putExtra("eventcontent", eventcontent);
                int requestID = (int) System.currentTimeMillis();
                PendingIntent mPendingIntent = PendingIntent.getActivity(
                        getApplicationContext(), requestID, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_menu_send)
                        .setContentTitle(shopname + "에서 쿠폰이 왔어요!!")
                        .setContentText("쿠폰을 받으시려면 클릭해주세요~")
                        .setAutoCancel(true)
                        .setContentIntent(mPendingIntent);

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                builder.setStyle(inboxStyle);
                nm.notify(mNotificationID, builder.build());
                mNotificationID = (mNotificationID - 1) % 1000 + 9000;
            }

        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            mac = params[0];

            data.put("mac", mac);

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    beaconid = order.get("beaconid").toString();
                    shopid = order.get("shopid").toString();
                    shopname = order.get("shopname").toString();
                    eventname = order.get("eventname").toString();
                    eventcontent = order.get("eventcontent").toString();
                    event = order.get("event").toString();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return event;
        }
    }

    private void getShopListFromServer(){
        GetShopListTask task = new GetShopListTask();
        task.execute("*","all");
    }

    class GetShopListTask extends AsyncTask<String, Void, Void> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_myshop_list.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MainActivity.this);
            loading.setMessage("목록 불러오는중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new MainShopListAdapter(MainActivity.this, items);
            shop_listview.setAdapter(mAdapter);
            getMarkerItem(items);
            loading.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            String query = params[0];
            String flag = params[1];

            data.put("user_id", query);
            data.put("flag", flag);

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            items.clear();
            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    String id = order.get("id").toString();
                    String name = order.get("name").toString();
                    String phone = order.get("phone").toString();
                    String photo = order.get("photo").toString();
                    String address = order.get("address").toString();
                    String lat = order.get("lat").toString();
                    String lon = order.get("lon").toString();
                    String cate = order.get("cate").toString();

                    items.add(new ShopInformation(id, name, phone, photo, address, lat,lon,cate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
