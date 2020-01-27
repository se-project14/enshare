package seproject14.enshare.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.LayoutInflater;
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import seproject14.enshare.R;
import seproject14.enshare.service.EBCConnector;
import seproject14.enshare.ui.MainActivity;

/**
 * Author s8subala
 * Gallery Fragment to display the images in the ebc folder of the device which contains the images which were taken by enshare
 */
public class GalleryFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback{

    public GridView gridView;

    public static ArrayList<ImageModel> listImageModel;

    private boolean[] thumbnailsSelection;

    private boolean selectionMode = false;

    public static GalleryFragment.GridAdapter gridAdapter;

    public static EBCConnector ebcConnector = new EBCConnector();

    public static boolean flag = false;

    private ActionMode actionMode;

    private Menu sortMenu;

    // Folder path from which the images are to be chosen for display in the gallery view.
    public static String IMG_FOLDER_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ebc" ;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        menu.add(Menu.NONE, R.id.sort_by_date, Menu.NONE, R.string.action_sort_by_date);
        menu.add(Menu.NONE, R.id.sort_by_location, Menu.NONE, R.string.action_sort_by_location);

        sortMenu = menu;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        checkPermissions();
        setHasOptionsMenu(true);
        root.setBackgroundResource(R.drawable.enshare_no_image);
        if(savedInstanceState != null){
            listImageModel = (ArrayList<ImageModel>)savedInstanceState.getSerializable("list");
        }
        else {
            listImageModel = GalleryUtil.initializeGallery(getActivity());
            if(!listImageModel.isEmpty()){
                root.setBackgroundResource(0);
            }
        }
        thumbnailsSelection = new boolean[listImageModel.size()];
        gridView = (GridView) root.findViewById(R.id.gridView);
        gridAdapter = new GridAdapter(listImageModel);
        gridView.setAdapter(gridAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());

        if(!ebcConnector.isInitialized()){
            ebcConnector.initialize(MainActivity._staticInstance);
        }

        // Single click on the image thumbnail opens the image in preview mode and displays the image to the user with important metadata.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!selectionMode){
                    startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), GalleryViewPictureActivity.class).putExtra("img", listImageModel.get(i).getImagePath()));
                }else{
                    gridAdapter = new GridAdapter(listImageModel);
                    gridView.setAdapter(gridAdapter);
                    gridAdapter.notifyDataSetChanged();
                }
            }
        });

        return root;
    }

    @Override
    public void onPause(){
        if(selectionMode){
            setMultiSelectModeOff();
            actionMode.finish();
        }
        super.onPause();

    }

    @Override
    public void onResume(){
//        listImageModel = GalleryUtil.initializeGallery(getActivity());

        View view = getView();
        updateAdapter();
        if(view != null) {
            if (listImageModel.size() == 0) {
                view.setBackgroundResource(R.drawable.enshare_no_image);
            } else {
                view.setBackgroundResource(0);
                if (!flag) {
                    try {
                        listImageModel = GalleryUtil.sortByDate(listImageModel);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    listImageModel = GalleryUtil.sortByLocation(listImageModel);
                }
                updateAdapter();
            }
        }
        super.onResume();
    }

    /**
     * Adapter class to handle the images in the Gridview of the Fragment.
     */
    class GridAdapter extends BaseAdapter {
        private ArrayList<ImageModel> imageModels;

        public GridAdapter(ArrayList<ImageModel> models) {
            this.imageModels = models;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CheckableLayout checkableLayout;
            ImageView imageView;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            if(convertView == null) {
                imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(((width+10)/3) ,((width+10)/3) ));
                checkableLayout = new CheckableLayout(getActivity().getApplicationContext());
                checkableLayout.setLayoutParams(new ViewGroup.LayoutParams(((width+10)/3) ,((width+10)/3) ));
                checkableLayout.addView(imageView);
            } else{
                checkableLayout = (CheckableLayout) convertView;
                imageView = (ImageView) checkableLayout.getChildAt(0);
            }
            //Glide is used for smooth display and handling of resources for the image displayed.
            Glide.with(Objects.requireNonNull(getActivity()).getApplicationContext())
                    .load(((ImageModel)getItem(position)).getImagePath())
                    .transition(DrawableTransitionOptions.withCrossFade(600))
                    .into(imageView);

            return checkableLayout;
        }


        public final int getCount() {
            return listImageModel.size();
        }

        public final Object getItem(int position) {
            return listImageModel.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }


    @Override //Override from ActivityCompat.OnRequestPermissionsResultCallback Interface
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
            }
            return;
        }
    }

    /**
     * Method to check if the permissions required by the app to read the image files are granted.
     */
    private void checkPermissions(){
        if(Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //Requesting permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

        public void setChecked(boolean checked) {
            mChecked = checked;
            setBackground(checked ? getResources().getDrawable(R.color.colorSelection) : null);
            setPadding(12,12,12,12);
        }

        public boolean isChecked() {
            return mChecked;
        }

        public void toggle() {
            setChecked(!mChecked);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_date:
                try {
                    listImageModel = GalleryUtil.sortByDate(listImageModel);
                    updateAdapter();
                    changeMenuColor(item, Color.BLUE);
                    changeMenuColor(sortMenu.getItem(1), Color.DKGRAY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sort_by_location:
                listImageModel = GalleryUtil.sortByLocation(listImageModel);
                updateAdapter();
                changeMenuColor(item, Color.BLUE);
                changeMenuColor(sortMenu.getItem(0), Color.DKGRAY);
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to change the color of the selected sorting metric.
     * @param item  menu item whose color is to be updated
     * @param color color of the menu item
     */
    public void changeMenuColor(MenuItem item, int color){
        SpannableString s = new SpannableString(item.getTitle().toString());
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        item.setTitle(s);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list",listImageModel);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            listImageModel = (ArrayList<ImageModel>)savedInstanceState.getSerializable("list");
        }
    }


    /**
     * Inner class having implementations for the MultiChoice mode options and listeners for the same.
     */
    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
            int selectCount = gridView.getCheckedItemCount();
            if(thumbnailsSelection != null) {
                thumbnailsSelection[position] = !thumbnailsSelection[position];
            }
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One photo selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " photos selected");
                    break;
            }
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            actionMode = mode;
            selectionMode = true;
            actionMode.setTitle("Select Photos");
            actionMode.setSubtitle("One photo selected");
            MenuInflater inf = actionMode.getMenuInflater();
            inf.inflate(R.menu.menu, menu);
            menu.findItem(R.id.menu_item_image_info).setVisible(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            if(thumbnailsSelection != null) {
                final int len = thumbnailsSelection.length;
                int countSelectedImages = 0;
                ArrayList<String> selectedImages = new ArrayList<>();
                ArrayList<ImageModel> selectedImageModels = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    if (thumbnailsSelection[i]) {
                        countSelectedImages++;
                        selectedImages.add(listImageModel.get(i).getImagePath());
                        selectedImageModels.add(listImageModel.get(i));
                    }
                }
                int id = item.getItemId();

                if (id == R.id.menu_item_share) {

                    if (countSelectedImages == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please select at least one photo", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Sharing " + countSelectedImages + " photos...", Toast.LENGTH_LONG).show();

                        for(ImageModel selectedImageModel : selectedImageModels){
                            GalleryUtil.shareImage(selectedImageModel, ebcConnector, getActivity());
                        }
                    }
                    actionMode.finish();
                } else if (id == R.id.menu_item_delete) {

                    AlertDialog diaBox = showDeleteConfirmationDialog(getActivity(), selectedImages);
                    diaBox.show();
                    actionMode.finish();
                }
            }
            selectionMode = false;
            return true;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            setMultiSelectModeOff();
        }
    }

    /**
     * Method that sets the multi select mode off, when the user moves out of the gallery screen.
     */
    private void setMultiSelectModeOff() {
        selectionMode = false;
        MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }

    /**
     * Dialog that confirms user's selection to delete items.
     * @return alert dialog with options to delete or cancel selection.
     */
    private AlertDialog showDeleteConfirmationDialog(Activity activity, ArrayList<String> selectImages)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete the selection?")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ArrayList<ImageModel> deletedImageModels = new ArrayList<>();
                        for(int i = 0 ; i < selectImages.size(); i++ ){
                            String deletedImage = selectImages.get(i);
                            File deletedFile = new File(deletedImage);
                            GalleryUtil.deleteImage(deletedFile);
                        }
                        for (ImageModel imageModel : listImageModel) {
                            String deletedImage = imageModel.getImagePath();
                            if (deletedImage != null && selectImages.contains(deletedImage)) {
                                deletedImageModels.add(imageModel);
                            }
                        }
                        refreshGalleryAfterDelete(deletedImageModels);
                        updateAdapter();
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

    /**
     * This method deletes the image models from the list of image models.
     * @param deletedImageModels list of deleted image models.
     */
    private void refreshGalleryAfterDelete(ArrayList<ImageModel> deletedImageModels) {
        if(!deletedImageModels.isEmpty()) {
            for(ImageModel imageModel : deletedImageModels){
                listImageModel.remove(imageModel);
            }
            View view = getView();
            if(listImageModel.isEmpty() && view != null){
                view.setBackgroundResource(R.drawable.enshare_no_image);
            }
        }
    }

    /**
     * Method to update the adapter and refresh the view with the latest set of images.
     */
    private void updateAdapter(){
        gridAdapter.notifyDataSetChanged();
        gridView.setAdapter(gridAdapter);
        thumbnailsSelection = new boolean[listImageModel.size()];
    }
}
