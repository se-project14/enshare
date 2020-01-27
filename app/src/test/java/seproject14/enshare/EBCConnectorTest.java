package seproject14.enshare;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import seproject14.enshare.service.EBCConnector;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class EBCConnectorTest {
    private Activity activity;

    private final String GOOGLE_TOKEN = "qwertzuiopasdfghjklyxcvbnm";

    @Before
    public void setup() throws Exception{
        activity = Robolectric.buildActivity( FragmentActivity.class )
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void ebcConnectorShouldNotBeInitialized() throws Exception
    {
        EBCConnector ebcCon = new EBCConnector();
        assertTrue("should be uninitialized", ebcCon.isInitialized() == false);
    }

    @Test
    public void ebcConnectorInitialization() throws Exception
    {
        EBCConnector ebcCon = new EBCConnector();
        ebcCon.initialize(this.activity);
        assertTrue("should be initialized", ebcCon.isInitialized() == true);
    }

    @Test
    public void ebcConnectorLoginLogout() throws Exception
    {
        EBCConnector ebcCon = new EBCConnector();
        ebcCon.initialize(this.activity);
        assertTrue("should not be logged in", ebcCon.isLoggedIn() == false);
        ebcCon.login(GOOGLE_TOKEN);
        assertTrue("should be logged in", ebcCon.isLoggedIn() == true);
        ebcCon.logout();
        assertTrue("should not be logged in", ebcCon.isLoggedIn() == false);
    }

    @Test
    public void ebcConnectorBackgroundService() throws Exception
    {
        EBCConnector ebcCon = new EBCConnector();
        ebcCon.initialize(this.activity);
        ebcCon.login(GOOGLE_TOKEN);

        // this is just checking for those methods to not throw any exceptions.
        // Actual testing of the service has to be done manually.
        ebcCon.startEncounterConfirmations();
        ebcCon.stopEncounterConfirmations();
    }

    @Test
    public void ebcConnectorMessaging() throws Exception
    {
        EBCConnector ebcCon = new EBCConnector();
        ebcCon.initialize(this.activity);
        ebcCon.login(GOOGLE_TOKEN);
        ebcCon.startEncounterConfirmations();

        // this is just checking for those methods to not throw any exceptions.
        // Actual testing of the service has to be done manually.
        ebcCon.sendString("Test Message", new Date(0), 0.0d, 0.0d);
        ebcCon.receiveImages(new EBCConnector.ReceiveImagesCallback() {
            @Override
            public void executeCallback() {
                System.out.println("OKAY, ALL FINE.");
            }
        });
    }

    @Test
    public void ebcConnectorDatabase() throws Exception
    {
        EBCConnector ebcCon = new EBCConnector();
        ebcCon.initialize(this.activity);

        // Ideally, we would test if the size of the returned list is 2 and 3 respectively,
        // But since in the test environment, no messages have been received yet, we have to
        // test for size == 0
        assertTrue(ebcCon.getImages(false, 0, 2).size() == 0);
        assertTrue(ebcCon.getImages(true, 0, 3).size() == 0);

        // Test for invalid id
        assertTrue("should return null", ebcCon.getImage(-1) == null);
    }
}
