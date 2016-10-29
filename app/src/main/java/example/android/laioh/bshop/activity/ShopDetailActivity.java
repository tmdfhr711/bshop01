package example.android.laioh.bshop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.adapter.MyShopListAdapter;
import example.android.laioh.bshop.model.ShopInformation;
import example.android.laioh.bshop.util.GpsInfo;
import example.android.laioh.bshop.util.RequestHandler;

public class ShopDetailActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {

    private TextView shop_name;
    private TextView shop_phone;
    private TextView event_name;
    private TextView event_content;
    private TextView shop_address;
    private Button coupon;
    private ImageView shop_photo;

    private Double shop_lat;
    private Double shop_lon;


    //SHOP_FLAG = 0 : 일반 상세정보화면
    //SHOP_FLAG = 1 : 비콘수신 상세정보화면
    private int SHOP_FLAG = 0;
    private String getShopname;

    private static LatLng nowAddress;
    private GoogleMap mGoogleMap;
    private GpsInfo mGps;
    private double mLat;
    private double mLon;
    private String mNowAddressKorea;

    private String name, phone, photo, address, lat, lon, eventname, eventcontent, cate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        init();
        getDataFromIntent();
        showData();
    }

    private void init(){
        shop_name = (TextView) findViewById(R.id.shopdetail_shopname);
        shop_phone = (TextView) findViewById(R.id.shopdetail_phone);
        event_name = (TextView) findViewById(R.id.shopdetail_eventname);
        event_content = (TextView) findViewById(R.id.shopdetail_eventcontent);
        shop_address = (TextView) findViewById(R.id.shopdetail_address);
        coupon = (Button) findViewById(R.id.shopdetail_coupon);
        shop_photo = (ImageView) findViewById(R.id.shopdetail_photo);

        coupon.setOnClickListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void getDataFromIntent(){
        Intent intent = getIntent();
        this.SHOP_FLAG = intent.getIntExtra("flag", 2);
        this.getShopname = intent.getStringExtra("shopname");
        //Toast.makeText(ShopDetailActivity.this, this.getShopname, Toast.LENGTH_LONG).show();
        //Log.e("shopDe", this.getShopname);
        shop_name.setText(getShopname);
        if (SHOP_FLAG == 0) {
            coupon.setEnabled(false);
        } else if (SHOP_FLAG == 1){
            coupon.setEnabled(true);
        } else if (SHOP_FLAG == 2){
            Toast.makeText(this, "쿠폰정보를 받아오는데 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void showData(){
        GetDetailShopInfoTask task = new GetDetailShopInfoTask();
        task.execute(this.getShopname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopdetail_coupon :
                RegistMyCouponTask task = new RegistMyCouponTask();
                task.execute("user1", name, photo, eventname, eventcontent);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGps = new GpsInfo(this);
        this.mLat = mGps.getLatitude();
        this.mLon = mGps.getLongitude();
        nowAddress = new LatLng(mLat,mLon);

        mNowAddressKorea = mGps.getAddress(getApplicationContext(), mLat, mLon);
        mGoogleMap.setMyLocationEnabled(true);
        //Marker nowPosition = mGoogleMap.addMarker(new MarkerOptions().position(nowAddress).title(mNowAddressKorea));

        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowAddress, 15));
        //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        //shop_address.setText(mNowAddressKorea);
        //Toast.makeText(this, mNowAddressKorea, Toast.LENGTH_SHORT).show();
    }

    class GetDetailShopInfoTask extends AsyncTask<String, Void, Void> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_shop_detailinfo.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(ShopDetailActivity.this);
            loading.setMessage("가게정보 받아오는중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            shop_name.setText(name);
            shop_phone.setText("전화번호 : " + phone);
            Picasso.with(ShopDetailActivity.this).load(photo).resize(640, 0).into(shop_photo);
            shop_address.setText(address);
            event_name.setText(eventname);
            event_content.setText(eventcontent);

            nowAddress = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowAddress, 15));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            Marker nowPosition = mGoogleMap.addMarker(new MarkerOptions().position(nowAddress).title(name));

            loading.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            String query = params[0];

            data.put("shopname", query);
            data.put("flag", String.valueOf(SHOP_FLAG));

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    name = order.get("name").toString();
                    phone = order.get("phone").toString();
                    photo = order.get("photo").toString();
                    address = order.get("address").toString();
                    lat = order.get("lat").toString();
                    lon = order.get("lon").toString();
                    cate = order.get("cate").toString();
                    eventname = order.get("eventname").toString();
                    eventcontent = order.get("eventcontent").toString();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class RegistMyCouponTask extends AsyncTask<String, Void, String> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_regist_mycoupon.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        String resultVal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(ShopDetailActivity.this);
            loading.setMessage("쿠폰 내려받는중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(ShopDetailActivity.this, "쿠폰을 내려 받았습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ShopDetailActivity.this, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            data.put("userid", params[0]);
            data.put("shopname", params[1]);
            data.put("shopphoto", params[2]);
            data.put("eventname", params[3]);
            data.put("eventcontent", params[4]);

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    resultVal = order.get("result").toString();
                    //Log.e("RegiCouponTask", order.get("shopphoto").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultVal;
        }
    }
}
