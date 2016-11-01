package example.android.laioh.bshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import example.android.laioh.bshop.R;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout myshop, mycoupon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        init();
    }

    private void init(){
        myshop = (LinearLayout) findViewById(R.id.mypage_myshop);
        mycoupon = (LinearLayout) findViewById(R.id.mypage_mycoupon);

        myshop.setOnClickListener(this);
        mycoupon.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mypage_myshop:
                Intent ShopList = new Intent(MyPageActivity.this, MyShopListActivity.class);
                startActivity(ShopList);
                finish();
                break;

            case R.id.mypage_mycoupon:
                Intent couponList = new Intent(MyPageActivity.this, MyCouponListActivity.class);
                startActivity(couponList);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
