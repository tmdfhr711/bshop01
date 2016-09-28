package example.android.laioh.bshop.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wizturn.sdk.peripheral.Peripheral;

import example.android.laioh.bshop.R;

public class BeaconInfoWriteActivity extends AppCompatActivity {

    private Peripheral mPeripheral;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info_write);
    }
}
