package seproject14.enshare;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import seproject14.enshare.database.EnshareDbHelper;
import seproject14.enshare.database.ImageMessage;
import seproject14.enshare.service.EBCConnector;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class EnshareDbHelperTest {
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
    public void enshareDbHelperInsertion() throws Exception
    {
        EnshareDbHelper db = new EnshareDbHelper(this.activity);
        assertTrue("database should be empty", db.getAll(EnshareDbHelper.DB_SORT_DATE_ASC, 999, 0).size() == 0);

        long type = ImageMessage.TYPE_RECEIVED;
        Date date = new Date(123456789l * 1000l);
        double lat = 1.2d;
        double lon = 3.4d;
        String text = "Secret Message XYZ";
        String user = "test_user";

        long id = db.insert(type, date, lat, lon, text, user);
        assertTrue("database size should be 1", db.getAll(EnshareDbHelper.DB_SORT_DATE_ASC, 99, 0).size() == 1);
        assertTrue("database size should still be 1", db.getAll(EnshareDbHelper.DB_SORT_DATE_ASC, 0, 0).size() == 1);
        assertTrue("database size should 0 from offset 1", db.getAll(EnshareDbHelper.DB_SORT_DATE_ASC, 0, 1).size() == 0);

        assertTrue("sent size should be 0", db.getSent(EnshareDbHelper.DB_SORT_DATE_ASC, 99, 0).size() == 0);
        assertTrue("sent size should still be 0", db.getSent(EnshareDbHelper.DB_SORT_DATE_ASC, 0, 0).size() == 0);

        assertTrue("received size should be 1", db.getReceived(EnshareDbHelper.DB_SORT_DATE_ASC, 99, 0).size() == 1);
        assertTrue("received size should still be 1", db.getReceived(EnshareDbHelper.DB_SORT_DATE_ASC, 0, 0).size() == 1);

        ImageMessage message = db.getById(id);
        assertTrue("types should match", message.getType() == type);
        assertTrue("dates should match", message.getDate().equals(date));
        assertTrue("latitudes should match", message.getLatitude() == lat);
        assertTrue("longitudes should match", message.getLongitude() == lon);
        assertTrue("messages should match", message.getMessage().equals(text));
        assertTrue("users should match", message.getUsername().equals(user));
    }
}
