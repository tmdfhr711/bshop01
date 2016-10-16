package example.android.laioh.bshop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.adapter.MyCouponListAdapter;
import example.android.laioh.bshop.model.Coupon;
import example.android.laioh.bshop.util.RequestHandler;

public class NotifiCouponActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView title;
    private TextView name;
    private TextView content;

    private Button cancel;
    private Button submit;

    private String getBeaconid;
    private String getShopid;
    private String getShopname;
    private String getEventname;
    private String getEventcontent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notifi_coupon);

        init();
        getDataFromIntent();
        ShowData();
    }

    private void init(){
        title = (TextView) findViewById(R.id.coupon_title);
        name = (TextView) findViewById(R.id.coupon_name);
        content = (TextView) findViewById(R.id.coupon_content);
        cancel = (Button) findViewById(R.id.coupon_cancel);
        submit = (Button) findViewById(R.id.coupon_submit);

        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    private void getDataFromIntent(){
        Intent intent = getIntent();

        getBeaconid = intent.getStringExtra("beaconid");
        getShopid = intent.getStringExtra("shopid");
        getShopname = intent.getStringExtra("shopname");
        getEventname = intent.getStringExtra("eventname");
        getEventcontent = intent.getStringExtra("eventcontent");

    }

    private void ShowData(){
        title.setText(getShopname + "에서 쿠폰이 왔어요~");
        name.setText("이벤트명 : " + getEventname);
        content.setText("내용 : " + getEventcontent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coupon_submit :
                RegistMyCouponTask task = new RegistMyCouponTask();
                task.execute("user1", getBeaconid, getShopid, getShopname, getEventname, getEventcontent);
                break;

            case R.id.coupon_layout:
            case R.id.coupon_cancel:
                finish();
                break;
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
            loading = new ProgressDialog(NotifiCouponActivity.this);
            loading.setMessage("쿠폰 내려받는중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(NotifiCouponActivity.this, "쿠폰을 내려 받았습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(NotifiCouponActivity.this, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            data.put("userid", params[0]);
            data.put("beaconid", params[1]);
            data.put("shopid", params[2]);
            data.put("shopname", params[3]);
            data.put("eventname", params[4]);
            data.put("eventcontent", params[5]);

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
