package example.android.laioh.bshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wizturn.sdk.peripheral.Peripheral;

import java.util.ArrayList;
import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.activity.BeaconInfoWriteActivity;
import example.android.laioh.bshop.activity.EventRegistActivity;
import example.android.laioh.bshop.model.Beacon;

/**
 * Created by Lai.OH on 2016-09-20.
 */
public class BeaconScanListAdapter extends BaseAdapter {

    private final String LOG_TAG = BeaconScanListAdapter.class.getSimpleName();
    private final String COLON = " : ";
    private final String unknown;

    private String shopname;
    private String shopid;

    private boolean isConnectingButtonTouching = false;

    private ArrayList<Peripheral> items = new ArrayList<Peripheral>();
    private HashMap<String, Peripheral> itemMap = new HashMap<String, Peripheral>();
    private LayoutInflater inflater;

    public BeaconScanListAdapter(Context context, String name, String id) {
        unknown = context.getString(R.string.unknown);
        inflater = LayoutInflater.from(context);
        this.shopname = name;
        this.shopid = id;
    }

    public synchronized void addOrUpdateItem(Peripheral peripheral) {
        if(itemMap.containsKey(peripheral.getBDAddress())) {
            itemMap.get(peripheral.getBDAddress()).setRssi(peripheral.getRssi());
        } else {
            items.add(peripheral);
            itemMap.put(peripheral.getBDAddress(), peripheral);
        }

        // Regardless of a matching device is found or not,
        // the following method must be invoked. by Jayjay
        if(!isConnectingButtonTouching) {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        items.clear();
        itemMap.clear();

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final Context context = parent.getContext();

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.custom_beaconlist_view, null);

            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.ble_name);
            holder.address = (TextView)convertView.findViewById(R.id.ble_address);
            holder.major = (TextView)convertView.findViewById(R.id.ble_major);
            holder.minor = (TextView)convertView.findViewById(R.id.ble_minor);
            holder.distance = (TextView)convertView.findViewById(R.id.ble_distance);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final Peripheral peripheral = items.get(position);
        if (peripheral != null) {
            holder.name.setText("비콘 이름 : " + peripheral.getBDName());
            holder.address.setText("Mac Address : " + peripheral.getBDAddress());
            holder.major.setText("Major : " + String.valueOf(peripheral.getMajor()));
            holder.minor.setText("Minor : " + String.valueOf(peripheral.getMinor()));
            holder.distance.setText(String.valueOf(peripheral.getDistance()) + " m");
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, String.valueOf(peripheral.getMinor()), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EventRegistActivity.class);
                intent.putExtra("peripheral", peripheral);
                intent.putExtra("name", shopname);
                intent.putExtra("id", shopid);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();

                if(action == MotionEvent.ACTION_DOWN) {
                    isConnectingButtonTouching = true;
                } else if(action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_CANCEL) {
                    isConnectingButtonTouching = false;
                }

                return false;
            }
        });


        return convertView;
    }

    private class ViewHolder{
        public TextView name;
        public TextView address;
        public TextView major;
        public TextView minor;
        public TextView distance;
    }
}
