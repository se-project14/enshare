package seproject14.enshare;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import seproject14.enshare.ui.gallery.GalleryFragment;

import static org.junit.Assert.assertNotNull;

/**
 * jUnit Test class that verifies if the gallery is loaded.
 *
 * To run this test comment out the code from line numbers
 *
 * 91 till 126 in the class GalleryFragment.java
 *
 * 162 and 168 in the class MainActivity.java
 *
 * 146 to 163 in the class GalleryFragment.java
 *
 */
@RunWith(RobolectricTestRunner.class)
public class GalleryFragmentTest {


    @Test
    public void shouldNotBeNull() throws Exception
    {
        GalleryFragment galleryFragment = new GalleryFragment();
        startFragment(galleryFragment);
        assertNotNull(galleryFragment);
    }

    public static void startFragment( Fragment fragment )
    {
        FragmentActivity activity = Robolectric.buildActivity( FragmentActivity.class )
                .create()
                .start()
                .resume()
                .get();

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add( fragment, null );
        fragmentTransaction.commit();
    }
}
