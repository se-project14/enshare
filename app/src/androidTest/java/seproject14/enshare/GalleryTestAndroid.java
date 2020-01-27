package seproject14.enshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.Gravity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import seproject14.enshare.ui.MainActivity;
import seproject14.enshare.ui.gallery.GalleryUtil;
import seproject14.enshare.ui.gallery.GalleryViewPictureActivity;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * jUnit Test class that verifies the gallery functionalities.
 *
 * To run this test comment out the code from line numbers
 *
 * 111 in the class GalleryFragment.java
 *
 * 162 and 168 in the class MainActivity.java
 *
 *
 */
@RunWith(AndroidJUnit4.class)
public class GalleryTestAndroid {
    Context context;

    @Rule
    public IntentsTestRule<MainActivity> mainActivityActivityTestRule =
            new IntentsTestRule<>(MainActivity.class, false, false);

    @Test
    public void testNavigateToGallery() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_gallery));

    }

    @Before
    public void setup() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        Activity mainActivity = mainActivityActivityTestRule.launchActivity(intent);
    }

    @Test
    public void testGalleryIsLoaded() throws Exception {
        testNavigateToGallery();
        onView(withId(R.id.gridView))
                .check(matches(isClickable()));
    }

    @Test
    public void testImageIsLoaded() throws Exception {
        testGalleryIsLoaded();
        onData(anything()).inAdapterView(withId(R.id.gridView)).atPosition(0).perform(click());
        intended(hasComponent(GalleryViewPictureActivity.class.getName()));
        onView(withId(R.id.imageView2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testImageDetailsLoaded() throws Exception {
        testImageIsLoaded();
        onView(withId(R.id.menu_item_image_info)).perform(click());
        onView(withId(R.id.Time)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.timeInformation)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.Location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.locationInformation)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.User)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.userInformation)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testGetLocationFromCoordinates(){
        String location = "Via Madonna Laura, 52100 Arezzo AR, Italy";
        Double latitude = 43.46744833333334;
        Double longitude = 11.885126666663888;
        assert (GalleryUtil.getLocationFromCoordinates(latitude, longitude, context).equals(location));
    }

    //To be moved to android tests
    @Test
    public void testDeleteImage(){
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "ebc");
        File testFile = (folder.listFiles()[0]);
        GalleryUtil.deleteImage(testFile);

        assert(!testFile.exists());
    }
}
