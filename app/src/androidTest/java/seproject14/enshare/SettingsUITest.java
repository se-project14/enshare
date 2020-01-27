////*************  To run this test successfully, you need to comment these lines in
/////////////////  MainActivity.java.
////////////////   Line 141 ->  this.ebcCon.initialize(MainActivity.this);
///////////////    Line 150 -> initializeApp();


package seproject14.enshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import seproject14.enshare.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class SettingsUITest {
    Context context;
    private Activity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @Test
    public void testNavigateToSettings() throws Exception {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));

    }

    @Before
    public void setup() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        mainActivity = mainActivityActivityTestRule.launchActivity(intent);
    }

    @Test
    public void testSettingsIsLoaded() throws Exception {
        testNavigateToSettings();
    }

    @Test
    public void testAnonymousIsClickable() throws Exception {
        testNavigateToSettings();
        onView(withText(R.string.anonymus))
                .perform(click())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSetUserNameIsClickable() throws Exception {
        testNavigateToSettings();
        onView(withText(R.string.name))
                .perform(click())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSharingOptionsIsClickable() throws Exception {
        testNavigateToSettings();
        onView(withText(R.string.sharing_options))
                .perform(click())
                .check(matches(isDisplayed()));
    }

}