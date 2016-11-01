package example.android.laioh.bshop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.adapter.MyCouponListAdapter;
import example.android.laioh.bshop.adapter.MyShopListAdapter;
import example.android.laioh.bshop.model.Coupon;
import example.android.laioh.bshop.model.ShopInformation;
import example.android.laioh.bshop.util.RbPreference;
import example.android.laioh.bshop.util.RequestHandler;

public class MyCouponListActivity extends AppCompatActivity {


    private MyCouponListAdapter mAdapter;
    private ArrayList<Coupon> items;

    private ListView coupon_listview;
    public RbPreference mPref = new RbPreference(MyCouponListActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon_list);

        init();
        getCouponDataFromServer();
    }

    private void init(){
        coupon_listview = (ListView) findViewById(R.id.mycoupon_listview);
        items = new ArrayList<>();
    }

    private void getCouponDataFromServer(){
        GetMyCouponListTask task = new GetMyCouponListTask();
        task.execute(mPref.getValue("user_id",""));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyCouponListActivity.this, MyPageActivity.class);
        startActivity(intent);
        finish();
    }

    class GetMyCouponListTask extends AsyncTask<String, Void, Void> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_mycoupon_list.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MyCouponListActivity.this);
            loading.setMessage("목록 불러오는중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new MyCouponListAdapter(MyCouponListActivity.this, items);
            coupon_listview.setAdapter(mAdapter);
            loading.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            String query = params[0];

            data.put("user_id", query);

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            items.clear();
            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    String couponid = order.get("couponid").toString();
                    String shopname = order.get("shopname").toString();
                    String photo = order.get("shopphoto").toString();
                    String eventname = order.get("eventname").toString();
                    String eventcontent = order.get("eventcontent").toString();
                    String iscoupon = order.get("iscoupon").toString();

                    items.add(new Coupon(couponid, shopname, photo, eventname, eventcontent, iscoupon));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
