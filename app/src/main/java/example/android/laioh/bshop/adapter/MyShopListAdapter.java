package example.android.laioh.bshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.activity.BeaconInfoWriteActivity;
import example.android.laioh.bshop.activity.BeaconScanListActivity;
import example.android.laioh.bshop.model.ShopInformation;

/**
 * Created by Lai.OH on 2016-10-15.
 */
public class MyShopListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShopInformation> items;

    public MyShopListAdapter(Context context, ArrayList<ShopInformation> items) {
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
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.custom_myshop_list_item, null);

            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.custom_shopname);
            holder.address = (TextView)convertView.findViewById(R.id.custom_shopaddress);
            //holder.phone = (TextView)convertView.findViewById(R.id.ble_major);
            holder.photo = (ImageView) convertView.findViewById(R.id.custom_shopphoto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
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
                showDialog(context, shop.getName(), shop.getId());
            }
        });
    /*
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
        });*/

        return convertView;
    }

    private class ViewHolder{
        public TextView name;
        public TextView phone;
        public ImageView photo;
        public TextView address;
    }

    private void showDialog(final Context context, final String name, final String id){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(name)
                .setMessage("어떤 작업을 진행하실래요??")
                .setCancelable(false)
                .setPositiveButton("비콘등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, BeaconScanListActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("id", id);
                        context.startActivity(intent);
                        dialog.dismiss();
                        ((Activity)context).finish();
                    }
                })
                .setNegativeButton("가게정보수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, BeaconScanListActivity.class);
                        context.startActivity(intent);
                        dialog.dismiss();
                        ((Activity)context).finish();
                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
