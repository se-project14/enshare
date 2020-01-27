package seproject14.enshare;

import org.junit.Test;

import java.util.Date;

import seproject14.enshare.ui.cloud.CloudFragment;
import seproject14.enshare.ui.cloud.CloudViewPictureActivity;

public class CloudFragmentTest {

    @Test
    public void testRemoveTime() {
        Date date = new Date();
        date.setDate(5);
        date.setMonth(5);
        date.setYear(98);
        date.setHours(12);
        date.setMinutes(32);
        date.setSeconds(12);
        date = CloudFragment.removeTime(date);
        assert (date.toString().equals("Fri Jun 05 00:00:00 CEST 1998"));
    }

    @Test
    public void testRemoveTime2() {
        Date date = new Date();
        date.setTime(999999999);
        date = CloudFragment.removeTime(date);
        assert (date.toString().equals("Mon Jan 12 00:00:00 CET 1970"));
    }

    @Test
    public void testFormatDate() {
        Date date = new Date();
        date.setDate(26);
        date.setMonth(1);
        date.setYear(120);
        date.setHours(19);
        date.setMinutes(12);
        date.setSeconds(32);
        String formatedDate = CloudViewPictureActivity.formatDate(date);
        assert (formatedDate.equals("26 February 2020 19:12:32"));
    }
}
