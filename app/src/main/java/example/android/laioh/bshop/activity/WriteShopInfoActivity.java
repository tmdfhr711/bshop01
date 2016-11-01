package example.android.laioh.bshop.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.util.GpsInfo;
import example.android.laioh.bshop.util.RbPreference;
import example.android.laioh.bshop.util.RequestHandler;

public class WriteShopInfoActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {


    private ImageView shop_image;
    private EditText shop_name;
    private EditText shop_phone;
    private EditText category;
    private Button regi_button;
    private TextView shop_address;


    private String selected_Image_path;
    public Bitmap shop_photo;

    private static LatLng nowAddress;
    private GoogleMap mGoogleMap;
    private GpsInfo mGps;
    private double mLat;
    private double mLon;
    private String mNowAddressKorea;

    private RbPreference mPref = new RbPreference(WriteShopInfoActivity.this);
    private static final int GET_PICTURE_URI = 101;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGps = new GpsInfo(this);
        this.mLat = mGps.getLatitude();
        this.mLon = mGps.getLongitude();
        nowAddress = new LatLng(mLat,mLon);

        mNowAddressKorea = mGps.getAddress(getApplicationContext(), mLat, mLon);
        mGoogleMap.setMyLocationEnabled(true);
        Marker nowPosition = mGoogleMap.addMarker(new MarkerOptions().position(nowAddress).title(mNowAddressKorea));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowAddress, 15));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        shop_address.setText(mNowAddressKorea);
        //Toast.makeText(this, mNowAddressKorea, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_shop_info);

        init();
    }

    private void init(){
        shop_image = (ImageView) findViewById(R.id.shopinfo_mainImage);
        shop_name = (EditText) findViewById(R.id.shopinfo_name);
        shop_phone = (EditText) findViewById(R.id.shopinfo_phone);
        category = (EditText) findViewById(R.id.shopinfo_cate);
        regi_button = (Button) findViewById(R.id.shopinfo_regibutton);
        shop_address = (TextView) findViewById(R.id.shopinfo_address);

        regi_button.setOnClickListener(this);
        shop_image.setOnClickListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ImageChooseUtil chooseImage = new ImageChooseUtil(data, getApplicationContext());

        switch (requestCode) {
            case GET_PICTURE_URI :
                if (resultCode == RESULT_OK) {

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selected_Image_path = cursor.getString(columnIndex);
                    cursor.close();

                    shop_photo = BitmapFactory.decodeFile(selected_Image_path);
                    shop_image.setImageBitmap(shop_photo);


                }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopinfo_mainImage:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GET_PICTURE_URI);
                break;

            case R.id.shopinfo_regibutton:
                UploadBbsImgTask bbs_photo_upload = new UploadBbsImgTask();
                bbs_photo_upload.execute();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(WriteShopInfoActivity.this, MyPageActivity.class);
        startActivity(intent);
        finish();
    }

    class UploadBbsImgTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = "http://210.117.181.66:8080/BShop/_bshop_upload_image.php";
        private ProgressDialog dialog = new ProgressDialog(WriteShopInfoActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("사진파일 올리는중...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Log.e("response String before", s);
            String getBbs_photo_url =s;
            //getPhotoPath = s.replace("[", "").replace("]", "");

            Log.e("response String after", getBbs_photo_url);
            /*Toast.makeText(getApplicationContext(), "file uploaded",
                    Toast.LENGTH_LONG).show();*/


            if (getBbs_photo_url != null && !getBbs_photo_url.equals("") && !getBbs_photo_url.equals("failed")) {
                try {
                    //Picasso.with(getApplicationContext()).load(getBbs_photo_url).error(R.drawable.ic_menu_noprofile).into(bbs_photo);
                    Picasso.with(getApplicationContext()).load(getBbs_photo_url).resize(640, 0).into(shop_image);
                    ShopInfoInsertTask task = new ShopInfoInsertTask();
                    task.execute(getBbs_photo_url, shop_name.getText().toString(), shop_phone.getText().toString(), category.getText().toString(), mNowAddressKorea, String.valueOf(mLat), String.valueOf(mLon));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                shop_image.setImageResource(R.drawable.ic_menu_gallery);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection conn = null;
            MultipartEntity entity = null;
            ByteArrayOutputStream bos = null;
            ByteArrayBody bab = null;
            OutputStream os = null;

            try {
                URL url = new URL(webAddressToPost);
                conn = (HttpURLConnection) url.openConnection();
                //Http 접속
                conn.setConnectTimeout(10000);
                //접속 timeuot시간 설정
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                //conn.addRequestProperty("Cookie", mPref.getValue("auth", ""));

                entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


                //File file = new File(selected_Image_path);
                //byte[] fileData = new byte[(int) file.length()];

                FileInputStream fis = new FileInputStream(selected_Image_path);
                byte[] fileData = new byte[(int) fis.available()];
                DataInputStream dis = new DataInputStream(fis);
                dis.readFully(fileData);
                dis.close();
                bab = new ByteArrayBody(fileData, "shop.jpg");
                entity.addPart("imgfiles", bab);
                fis.close();


                /*bos = new ByteArrayOutputStream();
                user_photo_bm.compress(Bitmap.CompressFormat.JPEG, 85, bos);
                byte[] data = bos.toByteArray();
                bab = new ByteArrayBody(data, "bbsimg.jpg");
                entity.addPart("imgfiles", bab);*/


                conn.addRequestProperty("Content-length", entity.getContentLength() + "");
                conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

                os = conn.getOutputStream();
                entity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.e("HTTP OK", "HTTP OK");
                    String result = readStream(conn.getInputStream());

                    //int i = 0;
                    Log.e("result", result);
                    //JSONObject responseJSON = new JSONArray(result).getJSONObject(0);

                    //String filename = responseJSON.get("photo_path").toString();
                    //Log.e("result", filename);
                    return result;
                } else {
                    Log.e("HTTP CODE", "HTTP CONN FAILED");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            entity = null;
            conn = null;
            bab = null;

            return null;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

    }

    class ShopInfoInsertTask extends AsyncTask<String, Void, String> {

        final String urlString = "http://210.117.181.66:8080/BShop/_bshop_shopinfo_insert.php";
        private ProgressDialog dialog = new ProgressDialog(WriteShopInfoActivity.this);
        RequestHandler rh = new RequestHandler();
        @Override
        protected void onPreExecute() {
            dialog.setMessage("가게정보 저장하는중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")) {
                dialog.dismiss();
                Intent intent = new Intent(WriteShopInfoActivity.this, MyPageActivity.class);
                startActivity(intent);
                finish();
            } else {
                dialog.dismiss();
                Toast.makeText(WriteShopInfoActivity.this, "파일 업로드중 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
            Log.e("ShopInfoInsertTask", s);
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            data.put("userid", mPref.getValue("user_id", "aaaaa"));
            data.put("photo", params[0]);
            data.put("shopname", params[1]);
            data.put("shopphone", params[2]);
            data.put("category", params[3]);
            data.put("address",params[4]);
            data.put("lat",params[5]);
            data.put("lon",params[6]);

            String result = rh.sendPostRequest(urlString, data);

            return result;
        }
    }
}
