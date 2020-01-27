package seproject14.enshare.ui.cloud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Albums implements Serializable {

    private ArrayList<Long> images;
    private Date date;


    public Albums(ArrayList<Long> images, Date date) {
        this.images = images;
        this.date = date;
    }

    public ArrayList<Long> getImages() {
        return images;
    }

    public Date getDate() {
        return date;
    }

    public void addImage(long img){
        images.add(img);
    }
}