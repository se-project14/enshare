package seproject14.enshare;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import androidx.fragment.app.*;
import seproject14.enshare.ui.settings.SettingsFragment;

@RunWith(RobolectricTestRunner.class)
public class SettingsTest {
    private SettingsFragment settingsFragment;
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

    @Before
    public void setup() throws Exception{
         settingsFragment = new SettingsFragment();
    }

    @Test
    public void settingsFragmentShouldNotBeNull() throws Exception
    {
        assertNotNull(settingsFragment);
    }

    @Test
    public void getAnonymousStateTest(){
        boolean state = settingsFragment.getAnonymousState();
        assertEquals(false, state);
    }

}
