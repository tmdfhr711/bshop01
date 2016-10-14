package example.android.laioh.bshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import example.android.laioh.bshop.R;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener{

    private Button myshop_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        init();
    }

    private void init(){
        myshop_button = (Button) findViewById(R.id.mypage_myshop_button);

        myshop_button.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mypage_myshop_button:
                Intent intent = new Intent(MyPageActivity.this, MyShopListActivity.class);
                startActivity(intent);
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