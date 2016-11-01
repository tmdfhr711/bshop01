package example.android.laioh.bshop.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wizturn.sdk.central.BluetoothGattWriter;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.OnConnectListener;
//import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.central.task.ChangeTask;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralAccessListener;
import com.wizturn.sdk.peripheral.PeripheralChangeEvent;
import com.wizturn.sdk.peripheral.PeripheralEvent;

import java.util.Observable;
import java.util.Observer;

import example.android.laioh.bshop.R;

public class BeaconInfoWriteActivity extends AppCompatActivity implements Observer, View.OnClickListener{

    private Peripheral mPeripheral;
    private CentralManager mCentralManager;

    private String shopname;
    private String shopid;

    private EditText name_edt;
    private EditText address_edt;
    private EditText uuid_edt;
    private EditText major_edt;
    private EditText minor_edt;

    private Button config_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info_write);

        init();
    }

    private void init(){
        name_edt = (EditText) findViewById(R.id.write_bdname);
        major_edt = (EditText) findViewById(R.id.write_major);
        config_button = (Button) findViewById(R.id.write_configbutton);

        config_button.setOnClickListener(this);
        name_edt.setOnClickListener(this);

        setCentralManager();
        getDataFromIntent();
        setView();
        connect();
    }
    private void setCentralManager(){
        mCentralManager = CentralManager.getInstance();
    }

    private void getDataFromIntent(){
        mPeripheral = getIntent().getParcelableExtra("peripheral");

        this.shopname = getIntent().getStringExtra("name");
        this.shopid = getIntent().getStringExtra("id");
        
        if (mPeripheral == null) {
            Toast.makeText(this, "정보를 얻어오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

        mPeripheral = mCentralManager.getPeripheral(mPeripheral.getBDAddress());
        Log.e("mPeripheral", mPeripheral.toString());
        mPeripheral.addObserver(this);

    }

    private void connect(){
        mCentralManager.connect(mPeripheral, onConnectListener);
    }

    private void setView(){
        EditText editText;

        String bdname = mPeripheral.getBDName();
        name_edt.setText(bdname);

        editText = (EditText) findViewById(R.id.write_address);
        String address = mPeripheral.getBDAddress();
        editText.setText(address);

        editText = (EditText) findViewById(R.id.write_uuid);
        String uuid = mPeripheral.getProximityUUID();
        editText.setText(uuid);

        String major = String.valueOf(mPeripheral.getMajor());
        major_edt.setText(major);

        editText = (EditText) findViewById(R.id.write_minor);
        String minor = String.valueOf(mPeripheral.getMinor());
        editText.setText(minor);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    protected void onDestroy() {
        if(mPeripheral != null) {
            mPeripheral.deleteObserver(this);
        }

        if(mCentralManager.isConnected()) {
            mCentralManager.disconnect();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.write_configbutton:
                BluetoothGattWriter writer = mCentralManager.getBluetoothGattWriter();

                //final BLEConnection conn = mCentralManager.getBluetoothGattWriter();
                writer.setPeripheralAccessListener(accessListener);

                String major = major_edt.getText().toString();
                writer.changeBdName("TEST");
                Log.e("Major", String.valueOf(mPeripheral.getMajor()));
        }
    }

    private OnConnectListener onConnectListener = new OnConnectListener() {
        @Override
        public void onDisconnected(final Peripheral peripheral) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(mCentralManager != null)
                        mCentralManager.disconnect();

                    Log.e("onDisconnected", "disconnected");
                    //toast.setText(R.string.error_disconneted);
                    //toast.show();
                    /*try {
                        //stopConnectingAnimation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectMenuItem.setTitle(R.string.actionbar_disconnected);
                            }
                        }, 100);
                    } catch(Exception e) {
                        Log.e(LOG_TAG, "onDisconnected() : exception : " + e.getMessage());
                    }*/
                }
            });
        }

        @Override
        public void onError(final Peripheral peripheral) {
            runOnUiThread(new Runnable() {
                public void run() {
                    /*toast.setText(R.string.error_connection_wrong);
                    toast.show();*/

                    /*try {
                        stopConnectingAnimation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectMenuItem.setTitle(R.string.actionbar_disconnected);
                            }
                        }, 100);
                    } catch(Exception e) {
                        Log.e(LOG_TAG, "onError() : exception : " + e.getMessage());
                    }*/
                }
            });

        }

        @Override
        public void onConnected(final Peripheral peripheral,
                                final boolean needAuthentication) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Log.e("onConnected", "Connected");
                    Toast.makeText(BeaconInfoWriteActivity.this, "연결성공", Toast.LENGTH_SHORT).show();
                    //toast.setText(R.string.toast_connected);
                    //toast.show();

                    try {
                        //stopConnectingAnimation();
                        //connectMenuItem.setTitle(R.string.actionbar_connected);

                        //setGroup2();
                        //setGroup3();
                        if(needAuthentication) {
                            //showDialogFragment(DIALOG_AUTHENTICATION);
                        }
                    } catch(Exception e) {
                        //Log.e(LOG_TAG, "onConnected() : exception : " + e.getMessage());
                    }
                }
            });
        }
    };






    private PeripheralAccessListener accessListener = new PeripheralAccessListener() {
        @Override
        public void onChangingCompleted(final Peripheral peripheral,
                                        final PeripheralChangeEvent event) {
            Log.d("accessListener", "accessListener.onChangingCompleted() : peripheral : " + peripheral + ", writeEvent : " + event);
            final int changeEvent = event.getEvent();
            switch(changeEvent) {
                case PeripheralChangeEvent.CHANGE_ADVERTISEMENT_INTERVAL:
                    break;
                case PeripheralChangeEvent.CHANGE_MAJOR:
                    break;
                case PeripheralChangeEvent.CHANGE_MINOR:
                    break;
                case PeripheralChangeEvent.CHANGE_PROXIMITY_UUID:
                    break;
                case PeripheralChangeEvent.CHANGE_TX_POWER:
                    break;
                case PeripheralChangeEvent.CHANGE_PASSWORD:
                    break;
                case PeripheralChangeEvent.CHANGE_UNKNWON:
                    break;
            }

            // updates ui
            runOnUiThread(new Runnable() {
                public void run() {
                    /*setGroup1();
                    setGroup2();
                    setGroup3();*/
                    //setView();
                }
            });
        }

        @Override
        public void onChangingFailed(final Peripheral peripheral,
                                     final PeripheralChangeEvent event) {
            Log.d("accessListener", "accessListener.onChangingFailed() : peripheral : " + peripheral + ", writeEvent : " + event);
            final int changeEvent = event.getEvent();
            switch(changeEvent) {
                case PeripheralChangeEvent.CHANGE_ADVERTISEMENT_INTERVAL:
                    Log.e("Event", "CHANGE_ADVERTISEMENT_INTERVAL");
                    break;
                case PeripheralChangeEvent.CHANGE_MAJOR:
                    Log.e("Event", "CHANGE_MAJOR");
                    break;
                case PeripheralChangeEvent.CHANGE_MINOR:
                    Log.e("Event", "CHANGE_MINOR");
                    break;
                case PeripheralChangeEvent.CHANGE_PROXIMITY_UUID:
                    Log.e("Event", "CHANGE_PROXIMITY_UUID");
                    break;
                case PeripheralChangeEvent.CHANGE_TX_POWER:
                    Log.e("Event", "CHANGE_TX_POWER");
                    break;
                case PeripheralChangeEvent.CHANGE_PASSWORD:
                    Log.e("Event", "CHANGE_PASSWORD");
                    break;
                case PeripheralChangeEvent.CHANGE_UNKNWON:
                    Log.e("Event", "CHANGE_UNKNWON");
                    break;
            }
        }

        @Override
        public void onPasswordChangingCompleted(final Peripheral peripheral) {
            Log.d("accessListener", "accessListener.onPasswordChangingCompleted() : peripheral : " + peripheral);
            runOnUiThread(new Runnable() {
                public void run() {
                    /*toast.setText(R.string.toast_password_change_success);
                    toast.show();*/
                }
            });
        }

        @Override
        public void onPasswordChangingFailed(final Peripheral peripheral) {
            Log.d("accessListener", "accessListener.onPasswordChangingFailed() : peripheral : " + peripheral);
            runOnUiThread(new Runnable() {
                public void run() {
                    /*toast.setText(R.string.toast_password_change_failure);
                    toast.show();*/
                }
            });
        }

        @Override
        public void onAuthenticatingCompleted(final Peripheral peripheral) {
            Log.d("accessListener", "accessListener.onAuthenticatingCompleted() : peripheral : " + peripheral);
            runOnUiThread(new Runnable() {
                public void run() {
                    /*toast.setText(R.string.toast_authorization_success);
                    toast.show();*/
                }
            });
        }

        @Override
        public void onAuthenticatingFailed(final Peripheral peripheral) {
            Log.d("accessListener", "accessListener.onAuthenticatingFailed() : peripheral : " + peripheral);
            runOnUiThread(new Runnable() {
                public void run() {
                    /*toast.setText(R.string.toast_authorization_failure);
                    toast.show();*/
                }
            });
        }

        @Override
        public void onReadingCompleted(Peripheral peripheral,
                                       PeripheralEvent event) {
            Log.d("accessListener", "accessListener.onReadingCompleted() : peripheral : " + peripheral);
        }

        @Override
        public void onReadingFailed(Peripheral peripheral, PeripheralEvent event) {
            Log.d("accessListener", "accessListener.onReadingFailed() : peripheral : " + peripheral);
        }
    };
}
