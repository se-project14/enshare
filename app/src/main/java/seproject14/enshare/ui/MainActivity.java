package seproject14.enshare.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import seproject14.enshare.R;
import seproject14.enshare.service.EBCConnector;
import seproject14.enshare.ui.gallery.GalleryFragment;
import seproject14.enshare.ui.gallery.GalleryUtil;

import static org.mpisws.encounters.EncounterBasedCommunication.REQUEST_ENABLE_BT;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int RESULT_NOT_OK = 0;
    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton takePhotoButton;
    private ActionBar actionBar;
    private NavigationView navigationView;
    private static final int IMAGE_CAPTURE_CODE = 101;
    public static GoogleAccountCredential mCredential;
    int RC_SIGN_IN = 0;
    private EBCConnector ebcCon = new EBCConnector();

    private static final String[] SCOPES = {"openid", "profile"};
    private static final String TAG = "EnShareApp";
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final int REQUEST_PERMISSION_GET_LOCATION = 1004;
    static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1005;
    private static final String PREF_ACCOUNT_NAME = "EBCUName";
    private static final String U_NAME = "UserName";
    public static String userAccountName = "Anonymous";
    public static DrawerLayout drawer;
    private boolean flag = false;
    private String accountName;
    private boolean dontAskAgainFlagLocation = false;
    private boolean dontAskAgainFlagStorage = false;

    public static MainActivity _staticInstance = null;

    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            checkLocationIfEnabled();
        }
        flag = false;
    }

    @Override
    protected void onStop() {
        flag = true;
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Started::::", "Started");
        super.onCreate(savedInstanceState);
        MainActivity._staticInstance = this;

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_cloud, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // initialize the EbcConnector
        if (!this.ebcCon.isInitialized()) {
            this.ebcCon.initialize(MainActivity.this);
        }

        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        initializeApp();

        takePhotoButton = findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWriteToDiskPermissions();
                dispatchTakePictureIntent();
            }
        });

        this.actionBar = this.getSupportActionBar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    /**
     * This method initialises the app by asking all the required permissions and switching
     * the required service on.
     */
    private void initializeApp() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (!isGooglePlayServicesAvailable()) {

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            chooseAccount();

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && !dontAskAgainFlagLocation) {
            getLocationPermissions();
            checkLocationIfEnabled();
        } else if (bluetoothManager.getAdapter() == null || !bluetoothManager.getAdapter().isEnabled()) {
            Log.v(TAG, "Bluetooth not enabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && !dontAskAgainFlagStorage) {
            Log.v(TAG, "Getting write permissions");
            getWriteToDiskPermissions();
            //initializeApp();
        } else {
            //Toast.makeText(MainActivity.this, "Google Login ", Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String token = mCredential.getToken();
                        Log.d(TAG, "********token is" + token);
                        //runOnUiThread(() -> Toast.makeText(MainActivity.this, "Google Token " + token, Toast.LENGTH_LONG).show());
                        setAccountName("");
                        // login with the google token
                        // and start the ebc service
                        ebcCon.login(token);
                        if (getAccountName() != null || getAccountName() != "") {
                            setAccountName(getAccountName());
                        } else {
                            setAccountName("");
                        }
                        ebcCon.startService();
                        ebcCon.startEncounterConfirmations();
                    } catch (UserRecoverableAuthException e) {
                        // start the user recoverable action
                        startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                    } catch (IOException | GoogleAuthException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public boolean setAccountName(String name) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.account_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.account_email);
        if ((name.equals(""))) {


            try {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String name2 = getAccountName();
                        if (name2 != null) {
                            name2 = accountName.split("@")[0];
                        }
                        navUsername.setText(name2);
                        navEmail.setText(accountName);
                        userAccountName = name2;

                        //  setSharedPreferences(name2);
                    }
                });
                Thread.sleep(300);

                return true;
            } catch (InterruptedException e) {

                e.printStackTrace();
                return false;
            }


        } else {
            try {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        navUsername.setText(name.split("@")[0]);
                        navEmail.setText(accountName);
                        accountName = name;
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(U_NAME, name);
                        editor.apply();
                        userAccountName = name.split("@")[0];
                    }
                });
                Thread.sleep(300);
                return true;
            } catch (InterruptedException e) {

                e.printStackTrace();
                return false;
            }
        }

    }

    private void setSharedPreferences(String name) {
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, name);
        editor.apply();
    }


    public String getAccountName() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String accountName = prefs.getString(U_NAME, "");
        if (accountName == null || accountName == "") {
            accountName = prefs.getString(PREF_ACCOUNT_NAME, "");
        }
        userAccountName = accountName;
        return accountName;
    }

    String mCurrentPhotoPath;


    /**
     * Creates an empty image file
     *
     * @return created file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/ebc");
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                "example",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Method which creates an empty image file and starts the camera activity
     */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                checkLocationIfEnabled();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE);
            }
        }
    }

    /**
     * Method which chooses the user account and stores the username in the preferences.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                Log.d(TAG, "Set account name! " + mCredential.getSelectedAccountName());
                initializeApp();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account to link with ebc library." +
                            " If you select,  \"Never ask again\", this app will not work and you have re-install it., ",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    /**
     * Gets the location permission
     */
    public void getLocationPermissions() {
        // Request the FINE_LOCATION permission via a user dialog
        EasyPermissions.requestPermissions(
                this,
                "This app needs to access your location. Without this permission, " +
                        "this app will not work properly. If Deny and Don't ask again is selected, " +
                        "you will have to reinstall the app and grant location permissions or do it manually in the settings",
                REQUEST_PERMISSION_GET_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void enableLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Permission");
        builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();

    }

    private void checkLocationIfEnabled() {
        Log.d(TAG, "********calling checkLocationIfEnabled ************");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled, isNetworkProviderEnabled;
        isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGpsProviderEnabled && !isNetworkProviderEnabled) {
            enableLocation();
        }
    }


    /**
     * Gets the Write to device storage permission
     */
    public void getWriteToDiskPermissions() {

        EasyPermissions.requestPermissions(
                this,
                "This app needs to read and save photos to your device."
                , REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }


    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == RESULT_OK) {
            Bundle extra = data.getExtras();
            String oneTimeToken = extra.getString("authtoken");
            Log.d(TAG, "*******token" + oneTimeToken);
        }
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. " +
                                    "Please install Google Play Services on your device " +
                                    "and relaunch this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    initializeApp();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSharedPreferences(accountName);
                        mCredential.setSelectedAccountName(accountName);
                        setAccountName("");
                        initializeApp();
                    }
                } else {
                    chooseAccount();
                }

                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) initializeApp();
                break;
            case REQUEST_ENABLE_BT:
                initializeApp();
                break;
        }

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            galleryAddPic();
        }

        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_NOT_OK) {
            boolean checkFileDelete = new File(mCurrentPhotoPath).delete();
            Log.d(TAG, "Delete status : : : : " + checkFileDelete);
        }

    }


    /**
     * Adds the photo taken to the gallery if the result_code was acceptable.
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        if(GalleryFragment.listImageModel != null){
            GalleryFragment.listImageModel.add(GalleryUtil.getImageModel(f, this));
        }
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
        if (requestCode == REQUEST_PERMISSION_GET_ACCOUNTS) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission
                System.exit(0);
            }
        }
        if (requestCode == REQUEST_PERMISSION_GET_LOCATION) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && !dontAskAgainFlagLocation) {
                dontAskAgainFlagLocation = true;
                initializeApp();

            }
        }

        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && !dontAskAgainFlagStorage) {
                dontAskAgainFlagStorage = true;
                initializeApp();

            }
        }


    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if (requestCode == REQUEST_PERMISSION_GET_LOCATION || requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
            initializeApp();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (requestCode == REQUEST_PERMISSION_GET_LOCATION) {
            getLocationPermissions();
        }
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            getWriteToDiskPermissions();
        }


    }


    /**
     * Method to check if Google Play services are online in the required device
     *
     * @return true or false
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Method to check whether internet connection is active and connected or not
     *
     * @return true or false
     */

    public boolean isInternetConnectionExist() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            return true;
        } else return false;
    }

    /**
     * Methdd to acquire the Google play services if not available
     * if(isGooglePlayServicesAvailable returns false)
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode, MainActivity.this);
        }
    }


    /**
     * Method to display the error dialog if there is any error while
     * acquiring the Google play services
     *
     * @param connectionStatusCode
     * @param activity
     */
    public static void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode, Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public FloatingActionButton getTakePhotoButton() {
        return this.takePhotoButton;
    }

    public NavigationView getNavigationView() {
        return this.navigationView;
    }

    public ActionBar getOurActionBar() {
        return this.actionBar;
    }
}
