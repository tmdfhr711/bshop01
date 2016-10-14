package example.android.laioh.bshop.activity;

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
    private Button stop_button;
    private CentralManager centralManager;
    private final int REQUEST_ENABLE_BT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_scan_list);

        init();

        centralManager = CentralManager.getInstance();
        centralManager.init(getApplicationContext());


        centralManager.setPeripheralScanListener(new PeripheralScanListener() {
            @Override
            public void onPeripheralScan(Central central, final Peripheral peripheral) {
                // TODO do something with the scanned peripheral(beacon)
                Log.i("BeaconScanListActivity", "peripheral : " + peripheral);
                /*boolean check = false;
                for(int i = 0; i < mBeaconList.size(); i++) {
                    if (mBeaconList.get(i).getAddress().equals(peripheral.getBDAddress())) {
                        check = true;
                    }
                }
                if (!check) {
                    BLEConnection conn = new BLEConnection(BeaconScanListActivity.this, peripheral,)
                    mBeaconList.add(new Beacon(peripheral.getBDName(), peripheral.getBDAddress(), peripheral.getModelNumber(), String.valueOf(peripheral.getMajor()), String.valueOf(peripheral.getMinor()), String.valueOf(peripheral.getDistance())));
                }*/
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.addOrUpdateItem(peripheral);
                    }
                });
            }
        });

        //centralManager.startScanning();

        mAdapter = new BeaconScanListAdapter(this);
        listview.setAdapter(mAdapter);
    }

    private void init(){
        listview = (ListView) findViewById(R.id.scanlist_listview);
        stop_button = (Button) findViewById(R.id.stop_scan);

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centralManager.stopScanning();
                //ArrayList 중복제거
                //HashSet<Beacon> listSet = new HashSet<Beacon>(mBeaconList);

                //ArrayList<Beacon> beaconList = new ArrayList<Beacon>(listSet);
                /*mAdapter = new BeaconScanListAdapter(BeaconScanListActivity.this, mBeaconList);
                listview.setAdapter(mAdapter);*/
            }
        });
        mBeaconList = new ArrayList<>();
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
