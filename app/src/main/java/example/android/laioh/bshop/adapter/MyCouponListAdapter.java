package example.android.laioh.bshop.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import example.android.laioh.bshop.R;
import example.android.laioh.bshop.activity.MyCouponListActivity;
import example.android.laioh.bshop.model.Coupon;
import example.android.laioh.bshop.util.RequestHandler;

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

        holder.iscoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UseMyCouponTask task = new UseMyCouponTask();
                task.execute(coupon.getCouponid());
                holder.iscoupon.setText("사용완료");
                holder.iscoupon.setEnabled(false);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        public TextView shopname;
        public TextView eventname;
        public TextView eventcontent;
        public ImageView shopimage;
        public Button iscoupon;
    }

    class UseMyCouponTask extends AsyncTask<String, Void, String> {

        final String SERVER_URL = "http://210.117.181.66:8080/BShop/_bshop_use_coupon.php";
        RequestHandler rh = new RequestHandler();
        private ProgressDialog loading;

        String resultVal;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(context);
            loading.setMessage("쿠폰 사용중...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Toast.makeText(context, "쿠폰 사용완료", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "쿠폰을 사용하는데 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {


            HashMap<String,String> data = new HashMap<>();

            data.put("couponid", params[0]);

            String result = rh.sendPostRequest(SERVER_URL,data);
            Log.e("result Data", result.toString());

            try {
                JSONArray ja = new JSONArray(result);
                Log.e("ja.length()", String.valueOf(ja.length()));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    resultVal = order.get("result").toString();
                    //Log.e("RegiCouponTask", order.get("shopphoto").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultVal;
        }
    }
}
