package example.android.laioh.bshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.connection.BLEConnection;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

import java.util.ArrayList;
import java.util.HashSet;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.adapter.BeaconScanListAdapter;
import example.android.laioh.bshop.model.Beacon;

public class BeaconScanListActivity extends AppCompatActivity {

    private ListView listview;
    private BeaconScanListAdapter mAdapter;
    private ArrayList<Beacon> mBeaconList;


    private CentralManager centralManager;

    private final int REQUEST_ENABLE_BT = 1000;

    private String shopname;
    private String shopid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_scan_list);

        init();
        getDataFromIntent();
        centralManager = CentralManager.getInstance();
        //centralManager.init(this);


        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, final Peripheral peripheral) {
                // TODO do something with the scanned peripheral(beacon)
                Log.i("BeaconScanListActivity", "peripheral : " + peripheral);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.addOrUpdateItem(peripheral);
                    }
                });
            }
        });

        mAdapter = new BeaconScanListAdapter(this, this.shopname, this.shopid);
        listview.setAdapter(mAdapter);
    }

    private void init(){
        listview = (ListView) findViewById(R.id.scanlist_listview);
        mBeaconList = new ArrayList<>();
    }

    private void getDataFromIntent(){
        Intent intent = getIntent();
        shopname = intent.getStringExtra("name");
        shopid = intent.getStringExtra("id");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BeaconScanListActivity.this, MyShopListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        centralManager.stopScanning();

    }

    @Override
    protected void onPause() {
        super.onPause();
        centralManager.stopScanning();
    }

    @Override
    protected void onResume(){
        super.onResume();
        centralManager.startScanning();
    }

}
