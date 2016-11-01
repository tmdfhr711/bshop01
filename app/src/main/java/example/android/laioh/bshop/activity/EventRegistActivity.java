package example.android.laioh.bshop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wizturn.sdk.peripheral.Peripheral;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.util.RequestHandler;

public class EventRegistActivity extends AppCompatActivity implements View.OnClickListener {
    private Peripheral mPeripheral;

    private String shopname;
    private String shopid;

    private EditText eventname_edt;
    private EditText eventcontent_edt;
    private TextView shopname_tv;
    private TextView macaddress_tv;
    private Button submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_regist);

        init();
        getDataFromIntent();
        showData();
    }

    private void init(){
        eventname_edt = (EditText) findViewById(R.id.eventregi_eventname);
        eventcontent_edt = (EditText) findViewById(R.id.eventregi_eventcontent);
        shopname_tv = (TextView) findViewById(R.id.eventregi_shopname);
        macaddress_tv = (TextView) findViewById(R.id.eventregi_address);
        submit_btn = (Button) findViewById(R.id.eventregi_submit);

        submit_btn.setOnClickListener(this);
    }

    private void getDataFromIntent(){
        mPeripheral = getIntent().getParcelableExtra("peripheral");

        this.shopname = getIntent().getStringExtra("name");
        this.shopid = getIntent().getStringExtra("id");

        if (mPeripheral == null) {
            Toast.makeText(this, "정보를 얻어오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showData(){
        shopname_tv.setText(this.shopname);
        macaddress_tv.setText("등록할 비콘 MAC Address : " + mPeripheral.getBDAddress());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eventregi_submit:
                RegistEventInfoTask task = new RegistEventInfoTask();
                task.execute(mPeripheral.getBDAddress(), this.shopid, this.shopname, eventname_edt.getText().toString(), eventcontent_edt.getText().toString());
                break;
        }
    }


    class RegistEventInfoTask extends AsyncTask<String, Void, String> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_beacon_event_regi.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        String resultVal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(EventRegistActivity.this);
            loading.setMessage("비콘 등록중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(EventRegistActivity.this, "비콘등록 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EventRegistActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EventRegistActivity.this, "비콘 등록중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            data.put("userid", "user1");
            data.put("mac", params[0]);
            data.put("shpoid", params[1]);
            data.put("shopname", params[2]);
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
