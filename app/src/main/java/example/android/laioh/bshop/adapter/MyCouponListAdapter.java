package example.android.laioh.bshop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.model.Coupon;

/**
 * Created by Lai.OH on 2016-10-17.
 */
public class MyCouponListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Coupon> items;

    public MyCouponListAdapter(Context context, ArrayList<Coupon> items) {
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
            convertView = View.inflate(context, R.layout.custom_mycoupon_list_item, null);

            holder = new ViewHolder();
            holder.shopname = (TextView)convertView.findViewById(R.id.mycoupon_shopname);
            holder.eventname = (TextView)convertView.findViewById(R.id.mycoupon_name);
            holder.eventcontent = (TextView)convertView.findViewById(R.id.mycoupon_content);
            holder.shopimage = (ImageView) convertView.findViewById(R.id.mycoupon_photo);
            holder.iscoupon = (Button) convertView.findViewById(R.id.mycoupon_isbutton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final Coupon coupon = items.get(position);

        if (coupon != null) {
            holder.shopname.setText(coupon.getShopname());
            holder.eventname.setText(coupon.getEventname());
            holder.eventcontent.setText(coupon.getEventcontent());
            String getIsCoupon = coupon.getIscoupon();
            if (getIsCoupon.equals("1")) {
                holder.iscoupon.setText("사용가능");
                holder.iscoupon.setEnabled(true);
            } else {
                holder.iscoupon.setText("사용완료");
                holder.iscoupon.setEnabled(false);
            }
            Picasso.with(context).load(coupon.getShopimage()).resize(640, 0).into(holder.shopimage);
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView shopname;
        public TextView eventname;
        public TextView eventcontent;
        public ImageView shopimage;
        public Button iscoupon;
    }
}
