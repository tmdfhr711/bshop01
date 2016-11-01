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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.util.RbPreference;
import example.android.laioh.bshop.util.RequestHandler;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{
    private Button signin_button;
    private EditText user_id_edt;
    private EditText user_pass_edt;

    private RbPreference mPref = new RbPreference(SigninActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        init();
    }
    private void init(){
        signin_button = (Button) findViewById(R.id.signin_button);
        user_id_edt = (EditText) findViewById(R.id.signin_userid);
        user_pass_edt = (EditText) findViewById(R.id.signin_user_password);
        signin_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_button :
                signin_button.setEnabled(false);
                String userid = user_id_edt.getText().toString();
                String userpass = user_pass_edt.getText().toString();

                SigninTask task = new SigninTask();
                task.execute(userid, userpass);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    class SigninTask extends AsyncTask<String, Void, String> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_signin.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        private String getUserId, getUserNick;
        String resultVal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(SigninActivity.this);
            loading.setMessage("로그인중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(SigninActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
                mPref.put("user_id",getUserId);
                mPref.put("user_nick", getUserNick);
                mPref.put("login","login");
                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (result.equals("nopass")) {
                signin_button.setEnabled(true);
                Toast.makeText(SigninActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            } else {
                signin_button.setEnabled(true);
                Toast.makeText(SigninActivity.this, "아이디가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            data.put("userid", params[0]);
            data.put("pass", getMD5Hash(params[1]));

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    resultVal = order.get("result").toString();
                    getUserId = order.get("userid").toString();
                    getUserNick = order.get("nick").toString();

                    //Log.e("RegiCouponTask", order.get("shopphoto").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultVal;
        }
    }

    /*
     * Create Lai.OH
     * String 값을 받아와 MD5 형식으로 바꾼 뒤 Return 하는 함수
     */
    public static String getMD5Hash(String s) {
        MessageDigest m = null;
        String hash = null;

        try {
            m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            hash = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }
}
