package seproject14.enshare;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import seproject14.enshare.ui.gallery.GalleryUtil;
import seproject14.enshare.ui.gallery.ImageModel;

import static junit.framework.TestCase.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class GalleryUtilTest {

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    private Activity activity;

    @Before
    public void setup() throws Exception{
        activity = Robolectric.buildActivity( FragmentActivity.class )
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void testImageModelCreation(){
        File file = getFileFromPath(this, "ex1.jpg");
        assert (file.exists());
        ImageModel model = GalleryUtil.getImageModel(file, activity);
        assertEquals (model.getImagePath(),file.getPath());
        assertEquals (model.getImageLocation(),"Unknown Location");
        assertEquals (model.getDateString(),"2017:05:22 13:00:34");
        assertEquals (model.getImageLatitude(),null);
        assertEquals (model.getImageLongitude(),null);
        assertEquals (model.getShortLocation(),"Unknown");
        assertEquals (model.getImageDate().toString(),"Mon May 22 13:00:34 CEST 2017");
    }


    @Test
    public void sortbyDateTest() throws Exception {
        ArrayList<ImageModel> toBeSorted = new ArrayList<ImageModel>();
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex1.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex2.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex3.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex4.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex5.jpg"), activity));


        ArrayList<ImageModel> sortedFiles = GalleryUtil.sortByDate(toBeSorted);
        int result = sortedFiles.get(0).getDateString().compareTo(sortedFiles.get(sortedFiles.size()-1).getDateString());
        if(result >= 0){
            assert(true);
        }
        else{
            assert(false);
        }
    }

    @Test
    public void sortbyLocationTest() throws Exception {
        ArrayList<ImageModel> toBeSorted = new ArrayList<ImageModel>();
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex1.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex2.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex3.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex4.jpg"), activity));
        toBeSorted.add(GalleryUtil.getImageModel(getFileFromPath(this, "ex5.jpg"), activity));

        GalleryUtil util = new GalleryUtil();
        ArrayList<ImageModel> sortedFiles = util.sortByLocation(toBeSorted);
        int result = sortedFiles.get(0).getImageLocation().compareTo(sortedFiles.get(sortedFiles.size()-1).getShortLocation());
        if(result <= 0){
            assert(true);
        }
        else{
            assert(false);
        }
    }

}