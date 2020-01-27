package seproject14.enshare.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class EBCStarter extends BroadcastReceiver {
    private EBCConnector ebcCon = new EBCConnector();

    private static final String TAG = "EnShareApp|EBCStarter";

    @Override
    public void onReceive(Context context, Intent intent) {
        String logMessage =  "EBCStarter";
        Log.d(TAG, logMessage);
        Toast.makeText(context, logMessage, Toast.LENGTH_LONG).show();

        if (this.ebcCon.isInitialized()) {
            this.ebcCon.initialize(context);
        }
        if (this.ebcCon.isLoggedIn()) {
            this.ebcCon.startService();
        }
        this.ebcCon.startEncounterConfirmations();
    }
}
