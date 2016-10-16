package example.android.laioh.bshop.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.adapter.MyShopListAdapter;
import example.android.laioh.bshop.model.ShopInformation;
import example.android.laioh.bshop.util.RbPreference;
import example.android.laioh.bshop.util.RequestHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CentralManager centralManager;
    private final int REQUEST_ENABLE_BT = 1000;
    private Button beacon_sacn;

    private RbPreference mPref = new RbPreference(this);
    private int mNotificationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        beacon_sacn = (Button) findViewById(R.id.scan_button);
        beacon_sacn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BeaconScanListActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, BeaconScanListActivity.class);
                startActivity(intent);
                finish();
                mPref.removeAllValue();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        centralManager = CentralManager.getInstance();
        centralManager.init(getApplicationContext());


        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, final Peripheral peripheral) {
                // TODO do something with the scanned peripheral(beacon)
                Log.i("ExampleActivity", "peripheral : " + peripheral);
                String getperi = peripheral.getBDAddress();
                String getaddress = mPref.getValue(peripheral.getBDAddress(), "");
                if (!getperi.equals(getaddress)) {
                    GetBeaconEventInfoTask task = new GetBeaconEventInfoTask();
                    task.execute(getperi);
                }
            }
        });

        if(!centralManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        init();
        centralManager.startScanning();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, WriteShopInfoActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void init() {
        //setCentralManager();
        terminateIfNotBLE();
        turnOnBluetooth();
    }


    private void terminateIfNotBLE() {
        if(!centralManager.isBLESupported()) {
            //Toast.makeText(this, R.string.error_ble_not_support, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void turnOnBluetooth() {
        if(!centralManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    class GetBeaconEventInfoTask extends AsyncTask<String, Void, String> {

        private String beaconid = "";
        private String shopid = "";
        private String shopname = "";
        private String eventname = "";
        private String eventcontent = "";
        private String event = "";

        private String mac;
        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_get_beacon_info.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (event.equals("1")) {
                mPref.put(mac, mac);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(getApplicationContext(), NotifiCouponActivity.class);
                intent.putExtra("beaconid", beaconid);
                intent.putExtra("shopid", shopid);
                intent.putExtra("shopname", shopname);
                intent.putExtra("eventname", eventname);
                intent.putExtra("eventcontent", eventcontent);
                PendingIntent mPendingIntent = PendingIntent.getActivity(
                        getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_menu_send)
                        .setContentTitle(shopname + "에서 쿠폰이 왔어요!!")
                        .setContentText("쿠폰을 받으시려면 클릭해주세요~")
                        .setAutoCancel(true)
                        .setContentIntent(mPendingIntent);

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                builder.setStyle(inboxStyle);
                nm.notify(mNotificationID, builder.build());
                mNotificationID = (mNotificationID - 1) % 1000 + 9000;

            }
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            mac = params[0];

            data.put("mac", mac);

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    beaconid = order.get("beaconid").toString();
                    shopid = order.get("shopid").toString();
                    shopname = order.get("shopname").toString();
                    eventname = order.get("eventname").toString();
                    eventcontent = order.get("eventcontent").toString();
                    event = order.get("event").toString();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return event;
        }
    }

}
