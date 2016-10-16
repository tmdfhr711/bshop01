package example.android.laioh.bshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import example.android.laioh.bshop.R;

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

                break;

            case R.id.coupon_layout:
            case R.id.coupon_cancel:
                finish();
                break;
        }
    }
}
