package example.android.laioh.bshop.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import example.android.laioh.bshop.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CentralManager centralManager;
    private final int REQUEST_ENABLE_BT = 1000;
    private Button beacon_sacn;
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
            }
        });
        if(!centralManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //init();
        //centralManager.startScanning();

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
            // Handle the camera action
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


    /*private void setCentralManager() {
        centralManager = CentralManager.getInstance();
        centralManager.init(getApplicationContext());
        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, final Peripheral peripheral) {
                Log.d(LOG_TAG, "onPeripheralScan() : peripheral : " + peripheral);
                //Log.d(LOG_TAG, String.valueOf(peripheral.getRssi()));
                runOnUiThread(new Runnable() {
                    public void run() {
                        //listAdapter.addOrUpdateItem(peripheral);
                        //peripheral.getBDName();
                        //peripheral.getDistance();
                        //peripheral.getBDAddress();
                        //TextView dist = (TextView)findViewById(R.id.Distance);

                        double distance = peripheral.getDistance();
                        int rssi = peripheral.getRssi();
                        String UUID = peripheral.getProximityUUID();


                        Context con = MainActivity.this;
                        String ns = Context.NOTIFICATION_SERVICE;
                        NotificationManager mNotificationManager = (NotificationManager)getSystemService(ns);

                        int icon = R.drawable.ic_launcher;
                        CharSequence tickerText = "여기는 오목대 입니다";
                        CharSequence tickerText2 = "여기는 전동성당 입니다";
                        CharSequence tickerText3 = "여기는 경기전 입니다";
                        CharSequence tickerText4 = "여기는 풍남문 입니다";


                        long when = System.currentTimeMillis();

                        Notification.Builder builder = new Notification.Builder(con);
                        builder.setSmallIcon(icon).setTicker(tickerText).setWhen(when);

                        Notification.Builder builder2 = new Notification.Builder(con);
                        builder2.setSmallIcon(icon).setTicker(tickerText2).setWhen(when);

                        Notification.Builder builder3 = new Notification.Builder(con);
                        builder3.setSmallIcon(icon).setTicker(tickerText3).setWhen(when);

                        Notification.Builder builder4 = new Notification.Builder(con);
                        builder4.setSmallIcon(icon).setTicker(tickerText4).setWhen(when);


                        @SuppressWarnings("deprecation")
                        Notification notification = builder.getNotification();
                        //notification.defaults |= Notification.DEFAULT_VIBRATE;
                        //notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                        notification.flags = Notification.FLAG_AUTO_CANCEL;


                        Notification notification2 = builder2.getNotification();
                        //notification2.defaults |= Notification.DEFAULT_VIBRATE;
                        //notification2.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                        notification2.flags = Notification.FLAG_AUTO_CANCEL;


                        Notification notification3 = builder3.getNotification();
                        //notification2.defaults |= Notification.DEFAULT_VIBRATE;
                        //notification2.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                        notification3.flags = Notification.FLAG_AUTO_CANCEL;

                        Notification notification4 = builder4.getNotification();
                        //notification2.defaults |= Notification.DEFAULT_VIBRATE;
                        //notification2.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                        notification4.flags = Notification.FLAG_AUTO_CANCEL;


                        Context context = getApplicationContext();
                        CharSequence contentTitle = "안내를 받으려면 클릭해주세요.";
                        CharSequence contentText = "오목대";

                        Context context2 = getApplicationContext();
                        CharSequence contentTitle2 = "안내를 받으려면 클릭해주세요.";
                        CharSequence contentText2 = "전동성당";

                        Context context3 = getApplicationContext();
                        CharSequence contentTitle3 = "안내를 받으려면 클릭해주세요.";
                        CharSequence contentText3 = "경기전";

                        Context context4 = getApplicationContext();
                        CharSequence contentTitle4 = "안내를 받으려면 클릭해주세요.";
                        CharSequence contentText4 = "풍남문";

						*//*

						Context context5 = getApplicationContext();
						CharSequence contentTitle5 = "안내를 받으려면 클릭해주세요.";
						CharSequence contentText5 = "향교";

						Context context6 = getApplicationContext();
						CharSequence contentTitle6 = "안내를 받으려면 클릭해주세요.";
						CharSequence contentText6 = "전통 한지원";

						Context context7 = getApplicationContext();
						CharSequence contentTitle7 = "안내를 받으려면 클릭해주세요.";
						CharSequence contentText7 = "최명희 문학관";

						Context context8 = getApplicationContext();
						CharSequence contentTitle8 = "안내를 받으려면 클릭해주세요.";
						CharSequence contentText8 = "한옥 생활 체험관";

						*//*

                        //Intent notificationIntent = new Intent(con, NotificationActivity.class);
                        //PendingIntent contentIntent = PendingIntent.getActivity(con, 0, notificationIntent, 0);


                        //Intent notificationIntent1 = new Intent(con,Notification2Activity.class);
                        //PendingIntent contentIntent1 = PendingIntent.getActivity(con, 0, notificationIntent1, 0);

                        //Intent notificationIntent2 = new Intent(con,Notification3Activity.class);
                        //PendingIntent contentIntent2 = PendingIntent.getActivity(con, 0, notificationIntent2, 0);

                        //Intent notificationIntent3 = new Intent(con,Notification4Activity.class);
                        //PendingIntent contentIntent3 = PendingIntent.getActivity(con, 0, notificationIntent3, 0);


                        if(UUID.equalsIgnoreCase("00000000-0000-0000-0000-000000000000"))
                        {
                            if(distance < 20.0)
                            {
                                flag_church = 0;
                                flag_gyeonggi = 0;
                                flag_pungnam = 0;

                                if(flag_omok == 0)
                                {
                                    Intent notificationIntent = new Intent(MainActivity.this, NotificationActivity.class);
                                    startActivity(notificationIntent);
                                }
                                //PendingIntent contentIntent = PendingIntent.getActivity(con, 0, notificationIntent, 0);
                                //notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
                                //mNotificationManager.notify(HELLO_ID, notification);
                                flag_omok++;
                            }
                        }

                        else if(UUID.equalsIgnoreCase("00000000-0000-0000-0000-000000000001"))
                        {
                            if(distance < 20.0)
                            {
                                flag_omok = 0;
                                flag_gyeonggi = 0;
                                flag_pungnam = 0;

                                if(flag_church == 0)
                                {
                                    Intent notificationIntent1 = new Intent(MainActivity.this,Notification2Activity.class);
                                    startActivity(notificationIntent1);
                                }
                                flag_church++;
                                //PendingIntent contentIntent1 = PendingIntent.getActivity(con, 0, notificationIntent1, 0);
                                //notification2.setLatestEventInfo(context2, contentTitle2, contentText2, contentIntent1);
                                //mNotificationManager.notify(HELLO_ID, notification2);
                            }
                        }


                        else if(UUID.equalsIgnoreCase("00000000-0000-0000-0000-000000000002"))
                        {
                            if(distance < 20.0)
                            {
                                flag_omok = 0;
                                flag_church = 0;
                                flag_pungnam = 0;

                                if(flag_gyeonggi == 0)
                                {
                                    Intent notificationIntent2 = new Intent(MainActivity.this,Notification3Activity.class);
                                    startActivity(notificationIntent2);
                                }
                                flag_gyeonggi++;
                                //notification3.setLatestEventInfo(context3, contentTitle3, contentText3, contentIntent2);
                                //mNotificationManager.notify(HELLO_ID, notification3);
                            }
                        }

                        else if(UUID.equalsIgnoreCase("00000000-0000-0000-0000-000000000003"))
                        {
                            if(distance < 20.0)
                            {
                                flag_omok = 0;
                                flag_church = 0;
                                flag_gyeonggi = 0;

                                if(flag_pungnam == 0)
                                {
                                    Intent notificationIntent3 = new Intent(MainActivity.this,Notification4Activity.class);
                                    startActivity(notificationIntent3);
                                }
                                flag_pungnam++;
                                //notification4.setLatestEventInfo(context4, contentTitle4, contentText4, contentIntent3);
                                //mNotificationManager.notify(HELLO_ID, notification4);
                            }
                        }


                    }
                });

            }
        });
    }*/


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


}
