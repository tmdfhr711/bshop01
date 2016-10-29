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
                /*RegistMyCouponTask task = new RegistMyCouponTask();
                task.execute("user1", getBeaconid, getShopid, getShopname, getEventname, getEventcontent);*/
                Intent intent = new Intent(NotifiCouponActivity.this, ShopDetailActivity.class);
                intent.putExtra("shopname", getShopname);
                intent.putExtra("flag", 1);
                startActivity(intent);
                break;

            case R.id.coupon_layout:
            case R.id.coupon_cancel:
                finish();
                break;
        }
    }
}
