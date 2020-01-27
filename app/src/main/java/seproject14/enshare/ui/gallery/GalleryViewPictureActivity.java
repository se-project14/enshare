package seproject14.enshare.ui.gallery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import seproject14.enshare.R;
import seproject14.enshare.ui.DialogPictureView;
/**
 * Class that displays the clicked gallery image in full screen view.
 */
public class GalleryViewPictureActivity extends AppCompatActivity {

    public ImageView imageView;
    public ExifInterface exif;
    File imageFile;
    int currentPosition;
    ArrayList<String> imgList;

    private float x1, x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.image_container);
        layout.setBackground(new ColorDrawable(Color.BLACK));
        String imagePath = getIntent().getStringExtra("img");
        imageFile = new File(imagePath);
        imgList = new ArrayList<>();
        initializeImageModels();
        if(imgList != null){
            currentPosition = imgList.indexOf(imagePath);
        } else{
            currentPosition = 0;
        }

        imageView = (ImageView) findViewById(R.id.imageView2);
        loadImage(imagePath);

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

    /**
     * Method to initialise the image list with the current selection.
     */
    private void initializeImageModels() {
        for(ImageModel model : GalleryFragment.listImageModel){
            imgList.add(model.getImagePath());
        }
    }

    /**
     * Method that loads the specified image path into the view.
     * @param imagePath path of the image.
     */
    private void loadImage(String imagePath) {
        //Loading image from below url into imageView
        imageFile = new File(imagePath);
        Glide.with(getApplicationContext())
                .load(imagePath)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_delete){
            AlertDialog dialog = showDeleteConfirmationDialog(this);
            dialog.show();
        } else if(item.getItemId() == R.id.menu_item_share){
            Toast.makeText(getApplicationContext(),"Sharing image : "+imageFile.getName(), Toast.LENGTH_LONG).show();

            for(ImageModel shareModel : GalleryFragment.listImageModel){
                if(imageFile.getPath().equals(shareModel.getImagePath())){
                    GalleryUtil.shareImage(shareModel, GalleryFragment.ebcConnector, this);
                }
            }
        } else if(item.getItemId() == R.id.menu_item_image_info){
            Intent intent = new Intent(getApplicationContext(), DialogPictureView.class);
            intent.putExtra("date", GalleryFragment.listImageModel.get(currentPosition).getDateString());
            intent.putExtra("location", GalleryFragment.listImageModel.get(currentPosition).getImageLocation());
            intent.putExtra("user", GalleryFragment.listImageModel.get(currentPosition).getImageUser());
            startActivity(intent);
        } else{
            // handle arrow click here
            if (item.getItemId() == android.R.id.home) {
                finish(); // close this activity and return to preview activity (if there is any)
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Dialog that confirms user's selection to delete items.
     * @return alert dialog with options to delete or cancel selection.
     */
    private AlertDialog showDeleteConfirmationDialog(Activity activity)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete the selection?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(imageFile != null) {
                            Toast.makeText(getApplicationContext(),"Deleting image..."+imageFile.getName(), Toast.LENGTH_SHORT).show();
                            GalleryUtil.deleteImage(imageFile);
                            GalleryFragment.listImageModel.remove(currentPosition);
                            GalleryFragment.gridAdapter.notifyDataSetChanged();
                            activity.finish();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .create();

        return alertDialog;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (deltaX < 0) {
                    onSwipeLeft();
                }else if(deltaX >0){
                    onSwipeRight();
                }
                break;
        }


        return super.onTouchEvent(event);
    }


    public void onSwipeRight() {
        String newImagePath = "";
        currentPosition = currentPosition - 1;
        if(currentPosition >= 0){
            newImagePath = imgList.get(currentPosition);
            loadImage(newImagePath);
        } else{
            currentPosition = 0;
        }
    }

    public void onSwipeLeft() {
        String newImagePath = "";
        currentPosition = currentPosition + 1;
        if(currentPosition < imgList.size()){
            newImagePath = imgList.get(currentPosition);
            loadImage(newImagePath);
        } else{
            currentPosition = imgList.size() - 1;
        }
    }

}