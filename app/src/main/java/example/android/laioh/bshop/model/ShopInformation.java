package example.android.laioh.bshop.model;

/**
 * Created by Lai.OH on 2016-10-15.
 */
public class ShopInformation {
    private String id;
    private String name;
    private String phone;
    private String photo;
    private String address;
    private String lat;
    private String lon;
    private String cate;

    public ShopInformation(String id, String name, String phone, String photo, String address, String lat, String lon, String cate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.photo = photo;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.cate = cate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getCate() {
        return cate;
    }
}
