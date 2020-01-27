package seproject14.enshare.ui.gallery;


import java.io.Serializable;
import java.util.Date;

import seproject14.enshare.ui.MainActivity;

/**
 * Model class to hold the image instance and corresponding metadata.
 */
public class ImageModel implements Serializable {

    String imagePath;

    Date imageDate;

    String dateString;

    String imageLocation;

    String shortLocation;

    Double imageLongitude;

    Double imageLatitude;

    String imageUser = MainActivity.userAccountName;

    public ImageModel(String path, Date date, String location, Double latitude, Double longitude, String shortLoc, String dateStr){
        this.imageDate = date;

        this.imageLocation = location;

        this.imagePath = path;

        this.imageLatitude = latitude;

        this.imageLongitude = longitude;

        this.shortLocation = shortLoc;

        this.dateString = dateStr;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getShortLocation() {
        return shortLocation;
    }

    public void setShortLocation(String shortLocation) {
        this.shortLocation = shortLocation;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getImageDate() {
        return imageDate;
    }

    public void setImageDate(Date imageDate) {
        this.imageDate = imageDate;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getImageUser() {
        return imageUser;
    }

    public Double getImageLongitude() {
        return imageLongitude;
    }

    public void setImageLongitude(Double imageLongitude) {
        this.imageLongitude = imageLongitude;
    }

    public Double getImageLatitude() {
        return imageLatitude;
    }

    public void setImageLatitude(Double imageLatitude) {
        this.imageLatitude = imageLatitude;
    }

}
