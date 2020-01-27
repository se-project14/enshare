package seproject14.enshare.ui.cloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import seproject14.enshare.R;
import seproject14.enshare.database.ImageMessage;
import seproject14.enshare.ui.DialogPictureView;
import seproject14.enshare.ui.ZoomableImageView;
import seproject14.enshare.ui.gallery.GalleryUtil;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CloudViewPictureActivity extends AppCompatActivity {

    private Intent intent;
    private ImageMessage imgMessage;
    private ZoomableImageView touch;
    private Bitmap image;
    private String time;
    private String location;
    private String user;
    private long id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cloud_image);

        intent = getIntent();
        id = intent.getLongExtra("img", 0);

        // use this when actually receiving from cloud works
//        image = ebcConnector.getImage(images.get(i)).second;
//        imgMessage = ebcConnector.getImage(images.get(i)).first;

        // use this for the mocked pictures
        image = CloudUtil.getBitmapById(id, getApplicationContext()).second;
        imgMessage = CloudUtil.getBitmapById(id, getApplicationContext()).first;

        Log.d(TAG, "IMAGE WIDTH" + image.getWidth());
        touch = findViewById(R.id.imageCloudView);
        touch.setImageBitmap(image);

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(image)
                .into(touch);

        user = imgMessage.getUsername();
        Log.d("", "CloudViewPictureActivity: date: " + imgMessage.getDate());
        time = formatDate(imgMessage.getDate());
        double latitude = imgMessage.getLatitude();
        double longitude = imgMessage.getLongitude();
        location = GalleryUtil.getLocationFromCoordinates(latitude, longitude,this);


        // add back arrow to toolbar and set background color to black
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        // Set Status- and Navigationbar color
        Window window = this.getWindow();
        if (window != null) {
            window.setStatusBarColor(Color.BLACK);
            window.setNavigationBarColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= 28) {
                window.setNavigationBarDividerColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_cloud, menu);
        menu.findItem(R.id.menu_item_info).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_save) {
            Toast.makeText(getApplicationContext(),"Downloading...", Toast.LENGTH_LONG).show();
            CloudUtil.saveImage(image,this);
        } else if(item.getItemId() == R.id.menu_item_info){
            Intent intent = new Intent(getApplicationContext(), DialogPictureView.class);
            intent.putExtra("date", time);
            intent.putExtra("location", location);
            intent.putExtra("user", user);
            startActivity(intent);
        } else{
            // handle arrow click here
            if (item.getItemId() == android.R.id.home) {
                finish(); // close this activity and return to preview activity (if there is any)
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /** Change th format of the date to make it easier to read for the user.
     * @param date date we want to format
     * @return formated date as a string
     */
    public static String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");

        return dateFormat.format(date);
    }
}
