package example.android.laioh.bshop.model;

/**
 * Created by Lai.OH on 2016-10-17.
 */
public class Coupon {
    private String couponid;
    private String shopname;
    private String shopimage;
    private String eventname;
    private String eventcontent;
    private String iscoupon;

    public Coupon(String couponid, String shopname, String shopimage, String eventname, String eventcontent, String iscoupon) {
        this.couponid = couponid;
        this.shopname = shopname;
        this.shopimage = shopimage;
        this.eventname = eventname;
        this.eventcontent = eventcontent;
        this.iscoupon = iscoupon;
    }

    public String getCouponid() {
        return couponid;
    }

    public void setCouponid(String couponid) {
        this.couponid = couponid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShopimage() {
        return shopimage;
    }

    public void setShopimage(String shopimage) {
        this.shopimage = shopimage;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getEventcontent() {
        return eventcontent;
    }

    public void setEventcontent(String eventcontent) {
        this.eventcontent = eventcontent;
    }

    public String getIscoupon() {
        return iscoupon;
    }

    public void setIscoupon(String iscoupon) {
        this.iscoupon = iscoupon;
    }
}
