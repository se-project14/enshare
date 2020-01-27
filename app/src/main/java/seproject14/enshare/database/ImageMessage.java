package seproject14.enshare.database;

import java.io.Serializable;
import java.util.Date;

public class ImageMessage implements Serializable {
    public static final long TYPE_SENT = 0;
    public static final long TYPE_RECEIVED = 1;
    public static final long TYPE_SAVED = 2;

    private long id;
    private long type;
    private Date date;
    private double lat;
    private double lon;
    private String msg;
    private String username = "Anonymous";

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLatitude() {
        return this.lat;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return this.lon;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }

    public String getMessage() {
        return this.msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        return String.format("{ID:%s,TYPE:%s,DATE:%s,LAT:%s,LON:%s,MSG:%s,USER:%s}",
                this.id, this.type, this.date, this.lat, this.lon, this.msg, this.username);
    }
}
