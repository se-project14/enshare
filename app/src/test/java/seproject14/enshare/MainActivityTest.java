package seproject14.enshare;

import android.content.Intent;

import android.provider.MediaStore;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import android.content.pm.PackageManager;
import androidx.test.core.app.ApplicationProvider;
import seproject14.enshare.ui.MainActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * jUnit Test class that verifies if the main activity is loaded and corresponsing functions.
 *
 * To run this test comment out the code from line numbers
 *
 * 162 in the class MainActivity.java
 *
 * 168 in the class MainActivity.java
 *
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    private MainActivity activity;
    private FloatingActionButton pressMeButton;
    private PackageManager packageManager;

    @Before
    public void setUp() throws Exception {
            activity = Robolectric.buildActivity(MainActivity.class)
                    .create()
                    .resume()
                    .get();
            pressMeButton = (FloatingActionButton) activity.findViewById(R.id.takePhotoButton);
            packageManager = ApplicationProvider.getApplicationContext().getPackageManager();

    }

    @Test
    public void shouldHaveStartedDrawerView() throws Exception {
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        assertNotNull(drawer);
    }

    @Test
    public void shouldHaveStartedNavigationView() throws Exception {
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        assertNotNull(navigationView);
    }

    @Test
    public void shouldHaveStartedMainActivity() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveCorrectAppName() throws Exception {
        String hello = activity.getResources().getString(R.string.app_name);
        assertEquals(hello, "Enshare");
    }

    @Test
    public void shouldStartCameraWhenButtonIsClicked()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        assertNotNull(pressMeButton.performClick());
    }


}
