package seproject14.enshare.ui.cloud;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import seproject14.enshare.R;

public class ViewAlbum extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, Checkable {

    private GridView gridView;
    private ViewAlbum.GridAdapter gridAdapter;
    private ArrayList<Long> images;
    private Date date;
    private Albums album;
    private Context context = this;
    private boolean markingON;
    private boolean[] marked;
    private boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_album);

        checkPermissions();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        album = (Albums) bundle.get("album");
        date = album.getDate();
        images = album.getImages();

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        marked = new boolean[images.size()];

        gridView = findViewById(R.id.albumView);
        gridAdapter = new ViewAlbum.GridAdapter();
        gridView.setAdapter(gridAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!markingON) {
                    // passing through the image id of the image clicked on
                    Intent intent = new Intent(getApplicationContext(), CloudViewPictureActivity.class);
                    intent.putExtra("img", images.get(i));
                    startActivity(intent);
                } else {
                    gridAdapter.notifyDataSetChanged();
                }
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                markingON = true;
                view.findViewById(R.id.tool_bar).setVisibility(View.VISIBLE);
                return true;
            }
        });
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

            getSupportActionBar().setTitle(dateFormat.format(date));
        }
    }

    @Override
    public void setChecked(boolean check) {
        checked = check;
        gridView.setForeground(checked ? getResources().getDrawable(R.color.colorSelection) : null);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    /**
     * GridAdapter code to display the images images inside a gridView
     */
    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int i) {
            // use this when actually receiving from cloud
            // return ThumbnailUtils.extractThumbnail(ebcConnector.getImage(images.get(i)).second, gridView.getColumnWidth(), gridView.getColumnWidth());

            // use this for the mocked pictures
            return ThumbnailUtils.extractThumbnail(CloudUtil.getBitmapById(images.get(i), getApplicationContext()).second, gridView.getColumnWidth(), gridView.getColumnWidth());

        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            CheckableLayout checkableLayout;

            ImageView imageView;
            if(view == null) {
                imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(gridView.getColumnWidth(),gridView.getColumnWidth()));

                checkableLayout = new CheckableLayout(getApplicationContext());
                checkableLayout.setLayoutParams(new GridView.LayoutParams(gridView.getColumnWidth(),gridView.getColumnWidth()));

                checkableLayout.addView(imageView);
            } else{
                checkableLayout = (CheckableLayout) view;
                imageView = (ImageView) checkableLayout.getChildAt(0);
            }

            //Loading image from bitmap into imageView
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(getItem(i))
                    .into(imageView);

            return checkableLayout;
        }
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //Requesting permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override //Override from ActivityCompat.OnRequestPermissionsResultCallback Interface
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                }
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
        ActionMode mode;
        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            int markedImgCount = gridView.getCheckedItemCount();
            if(marked != null) {
                marked[i] = !marked[i];
            }

            // text in selection toolbar
            if (markedImgCount == 1) {
                actionMode.setSubtitle("One item selected");
            } else {
                actionMode.setSubtitle(markedImgCount + " items selected");
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mode = actionMode;
            mode.setTitle("Select Items");
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_cloud, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (marked != null) {
                final int length = marked.length;
                int markedImgCount = gridView.getCheckedItemCount();
                ArrayList<Long> markedImg = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    if (marked[i]) {
                        markedImg.add(images.get(i));
                    }
                }
                int id = menuItem.getItemId();

                if (id == R.id.menu_item_save){

                    if(markedImgCount == 0) {
                        Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Downloading " + markedImgCount + " images...", Toast.LENGTH_LONG).show();
                        Log.d("SelectedImages", markedImg.toString());
                        for (Long imgID:markedImg) {
                            // use this when actually receiving from cloud
//                            CloudUtil.saveImage(ebcConnector.getImage(imgID).second, context);

                            // use this for the mocked pictures
                            CloudUtil.saveImage(CloudUtil.getBitmapById(imgID, getApplicationContext()).second, context);
                        }
                    }
                    actionMode.finish();

                }
            }
            markingON = false;
            if (marked != null) {
                for (int i = 0; i < marked.length; i++) {
                    if (marked[i]) {
                        marked[i] = false;
                    }
                }
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
        }

    }

    /**
     * Checkable layout implementation to highlight the selected images in multi-select mode.
     */
    public class CheckableLayout extends FrameLayout implements Checkable {
        private boolean mChecked;

        public CheckableLayout(Context context) {
            super(context);
        }

        @Override
        public void setChecked(boolean checked) {
            mChecked = checked;
            setBackground(checked ? getResources().getDrawable(R.color.colorSelection) : null);
            setPadding(12,12,12,12);
        }

        @Override
        public boolean isChecked() {
            return mChecked;
        }

        @Override
        public void toggle() {
            setChecked(!mChecked);
        }

    }
}
