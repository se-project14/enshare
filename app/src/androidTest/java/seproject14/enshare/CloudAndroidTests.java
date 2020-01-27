package seproject14.enshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;

import seproject14.enshare.ui.MainActivity;
import seproject14.enshare.ui.cloud.ViewAlbum;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class CloudAndroidTests {

    @Rule
    public IntentsTestRule<MainActivity> mainActivityActivityTestRule =
            new IntentsTestRule<>(MainActivity.class, false, false);

    @Test
    public void testNavigateToCloud() throws Exception {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_cloud));
    }

    @Before
    public void setup() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        mainActivityActivityTestRule.launchActivity(intent);
    }

    @Test
    public void testCloudIsLoaded() throws Exception {
        testNavigateToCloud();
        onView(withId(R.id.cloudGridView))
                .check(matches(isClickable()));
    }

    @Test
    public void testAlbumIsLoaded() throws Exception {
        testCloudIsLoaded();
        Thread.sleep(8000);
        onData(anything()).inAdapterView(withId(R.id.cloudGridView)).atPosition(0).perform(click());
        intended(hasComponent(ViewAlbum.class.getName()));
    }

    @Test
    public void testImageIsLoaded() throws Exception {
        testAlbumIsLoaded();
        onData(anything()).inAdapterView(withId(R.id.albumView)).atPosition(0).perform(click());
        intended(hasComponent(ViewAlbum.class.getName()));
    }

    @Test
    public void testDownloadImage() throws Exception {
        testImageIsLoaded();
        int countImages = 0;
        File dir = new File(Environment.getExternalStorageDirectory() + "/enshare/cloud");
        if (dir.exists() && dir.isDirectory()) {
            countImages = dir.listFiles().length;
        }
        onView(withId(R.id.menu_item_save)).perform(click());

        assert (dir.exists() && dir.isDirectory() && dir.listFiles().length == countImages+1);
    }

}
