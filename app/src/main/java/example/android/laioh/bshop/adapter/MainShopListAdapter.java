package example.android.laioh.bshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.activity.BeaconInfoWriteActivity;
import example.android.laioh.bshop.activity.ShopDetailActivity;
import example.android.laioh.bshop.model.ShopInformation;

/**
 * Created by Lai.OH on 2016-10-29.
 */
public class MainShopListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShopInformation> items;

    public MainShopListAdapter(Context context, ArrayList<ShopInformation> items) {
        this.context = context;
        this.items = items;
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
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.custom_myshop_list_item, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.custom_shopname);
            holder.address = (TextView) convertView.findViewById(R.id.custom_shopaddress);
            //holder.phone = (TextView)convertView.findViewById(R.id.ble_major);
            holder.photo = (ImageView) convertView.findViewById(R.id.custom_shopphoto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ShopInformation shop = items.get(position);

        if (shop != null) {
            holder.name.setText(shop.getName());
            holder.address.setText(shop.getAddress());
            Picasso.with(context).load(shop.getPhoto()).resize(640, 0).into(holder.photo);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailActivity.class);
                intent.putExtra("shopname", shop.getName());
                intent.putExtra("flag", 0);
                context.startActivity(intent);
            }
        });

        /*convertView.setOnTouchListener(new View.OnTouchListener() {
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
        });*/

        return convertView;
    }

    private class ViewHolder {
        public TextView name;
        public TextView phone;
        public ImageView photo;
        public TextView address;
    }
}
