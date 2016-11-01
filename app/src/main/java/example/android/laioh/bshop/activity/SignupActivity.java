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
import android.widget.RadioButton;
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

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText user_id_edt;
    private EditText user_password_edt;
    private EditText user_pass_check_edt;
    private EditText user_nick_edt;

    private Button sign_up_btn;

    public static RbPreference mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }

    private void init(){
        user_id_edt = (EditText) findViewById(R.id.signup_user_id);
        user_password_edt = (EditText) findViewById(R.id.signup_user_password);
        user_pass_check_edt = (EditText) findViewById(R.id.signup_user_pass_check);
        user_nick_edt = (EditText) findViewById(R.id.signup_user_nick);

        sign_up_btn = (Button) findViewById(R.id.signup_button);
        sign_up_btn.setOnClickListener(this);
    }

    private void signupModule(String userId, String userPass, String userNick){
        mPref = new RbPreference(SignupActivity.this);
        if (userId != null && userPass != null && userNick != null){
            SignupTask task = new SignupTask();
            task.execute(userId,userPass,userNick);

        } else {
            Toast.makeText(SignupActivity.this, "입력 실패", Toast.LENGTH_SHORT);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button :
                sign_up_btn.setEnabled(false);
                if(user_id_edt.getText().toString().length() != 0 && user_password_edt.getText().toString().length() != 0 && user_nick_edt.getText().toString().length() != 0){

                    if (user_password_edt.getText().toString().equals(user_pass_check_edt.getText().toString())) {
                        signupModule(user_id_edt.getText().toString(),user_password_edt.getText().toString(),user_nick_edt.getText().toString());
                    } else {
                        Toast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        sign_up_btn.setEnabled(true);
                    }

                } else {
                    sign_up_btn.setEnabled(true);
                    Toast.makeText(SignupActivity.this, "모든정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    class SignupTask extends AsyncTask<String, Void, String> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_signup.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        private String getUserId, getUserNick;
        String resultVal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(SignupActivity.this);
            loading.setMessage("회원가입중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(SignupActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                mPref.put("user_id",getUserId);
                mPref.put("user_nick", getUserNick);
                mPref.put("login","login");
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "회원가입중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            getUserId = params[0];
            getUserNick = params[2];

            data.put("userid", params[0]);
            data.put("pass", getMD5Hash(params[1]));
            data.put("nick", params[2]);

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
