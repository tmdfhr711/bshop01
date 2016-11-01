package example.android.laioh.bshop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.adapter.MyShopListAdapter;
import example.android.laioh.bshop.model.ShopInformation;
import example.android.laioh.bshop.util.RbPreference;
import example.android.laioh.bshop.util.RequestHandler;

public class MyShopListActivity extends AppCompatActivity {

    private MyShopListAdapter mAdapter;
    private ArrayList<ShopInformation> items;

    private ImageView addshop_iv;
    private ListView shop_listview;
    public RbPreference mPref = new RbPreference(MyShopListActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shop_list);

        init();
        getShopListFromServer();
    }

    private void init(){
        shop_listview = (ListView) findViewById(R.id.myshop_listview);
        addshop_iv = (ImageView) findViewById(R.id.myshop_add);

        addshop_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyShopListActivity.this, WriteShopInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
        items = new ArrayList<>();
}

    private void getShopListFromServer(){
        GetMyShopListTask task = new GetMyShopListTask();
        task.execute(mPref.getValue("user_id",""));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyShopListActivity.this, MyPageActivity.class);
        startActivity(intent);
        finish();
    }

    class GetMyShopListTask extends AsyncTask<String, Void, Void> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_myshop_list.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MyShopListActivity.this);
            loading.setMessage("목록 불러오는중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new MyShopListAdapter(MyShopListActivity.this, items);
            shop_listview.setAdapter(mAdapter);
            loading.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            String query = params[0];

            data.put("user_id", query);
            data.put("flag", "myshop");

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            items.clear();
            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    String id = order.get("id").toString();
                    String name = order.get("name").toString();
                    String phone = order.get("phone").toString();
                    String photo = order.get("photo").toString();
                    String address = order.get("address").toString();
                    String lat = order.get("lat").toString();
                    String lon = order.get("lon").toString();
                    String cate = order.get("cate").toString();

                    items.add(new ShopInformation(id, name, phone, photo, address, lat,lon,cate));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
