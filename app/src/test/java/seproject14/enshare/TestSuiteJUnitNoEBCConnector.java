package seproject14.enshare;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that verifies the jUnit test cases with coverage - for all the classes in the folder, except the EBCConnectorTest class.
 *
 * To run this test suite comment out the code from line numbers
 *
 * 91 till 126 in the class GalleryFragment.java
 *
 * 162 and 168 in the class MainActivity.java
 *
 * 146 to 163 in the class GalleryFragment.java
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SettingsTest.class,
        MainActivityTest.class,
        CloudFragmentTest.class,
        EnshareDbHelperTest.class,
        GalleryUtilTest.class,
        GalleryFragmentTest.class
})
public class TestSuiteJUnitNoEBCConnector {
}
