package seproject14.enshare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import seproject14.enshare.R;

public class LandingPage extends AppCompatActivity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.landingpage);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToMainActivity();

            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void goToMainActivity() {
        Intent mainIntent = new Intent(LandingPage.this, MainActivity.class);
        LandingPage.this.startActivity(mainIntent);
        LandingPage.this.finish();
    }
}
