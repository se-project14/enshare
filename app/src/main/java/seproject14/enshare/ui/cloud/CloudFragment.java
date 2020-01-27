package seproject14.enshare.ui.cloud;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import seproject14.enshare.R;
import seproject14.enshare.database.ImageMessage;
import seproject14.enshare.service.EBCConnector;
import seproject14.enshare.ui.MainActivity;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CloudFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private GridView gridView;
    private GridAdapter adapter;
    private ArrayList<Pair<ImageMessage, Bitmap>> imgBitmapPairList;
    private LinkedList<Albums> albumList;
    private boolean exists;

    private static EBCConnector ebcConnector;


    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cloud_gallery, container, false);
        checkPermissions();
        imgBitmapPairList = new ArrayList<>();
        albumList = new LinkedList<>();
        ebcConnector = new EBCConnector();
        if(!ebcConnector.isInitialized()){
            ebcConnector.initialize(MainActivity._staticInstance);
        }

        getImageFiles();

        adapter = new CloudFragment.GridAdapter(albumList);
        gridView = view.findViewById(R.id.cloudGridView);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), ViewAlbum.class);
                intent.putExtra("album", albumList.get(i));
                startActivity(intent);
            }
        });

        exists = true;

        return view;
    }

    @Override
    public void onDestroyView() {
        exists = false;

        super.onDestroyView();
    }

    class GridAdapter extends BaseAdapter {
        LinkedList<Albums> albumsList;

        public GridAdapter(LinkedList<Albums> list) {
            this.albumsList = list;
        }

        @Override
        public int getCount() {
            return albumList.size();
        }

        @Override
        public Object getItem(int i) {
            // use this when actually receiving from cloud
//            return ebcConnector.getImage(albumList.get(i).getImages().get(0)).second;

            // use this for the mocked pictures
            return CloudUtil.getBitmapById(albumList.get(i).getImages().get(0), getContext()).second;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public int getCountAlbumSize(int i) {
            return albumList.get(i).getImages().size();
        }

        public String getTitle(int i) {
            Date date = albumList.get(i).getDate();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            return dateFormat.format(date);
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.album_item, viewGroup, false);
            ImageView imageView = view.findViewById(R.id.albumViewC);
            TextView textCount = view.findViewById(R.id.gallery_count);
            TextView textTitle = view.findViewById(R.id.gallery_title);

            textCount.setText(String.valueOf(getCountAlbumSize(i)));
            textTitle.setText(getTitle(i));

            //Loading image into imageView
            Glide.with(getActivity().getApplicationContext())
                    .asBitmap()
                    .load(getItem(i))
                    .into(imageView);
            return view;
        }
    }

    /**
     * Getting images from the cloud and put them into the albumList.
     */
    private void getImageFiles() {
        EBCConnector.ReceiveImagesCallback callback = new EBCConnector.ReceiveImagesCallback() {
            @Override
            public void executeCallback() {
                // use this when actually receiving from cloud
//                List<Pair<ImageMessage, Bitmap>> temp = ebcConnector.getImages(false, 0, 0);
//                List<Pair<ImageMessage, Bitmap>> imgList = new ArrayList<Pair<ImageMessage, Bitmap>>();
//                for (Pair<ImageMessage, Bitmap> t: temp) {
//                    imgList.add(pair);
//                }

                // use this for getting the mocked pictures
                if (exists){
                    List<Pair<ImageMessage, Bitmap>> imgList = new ArrayList<>();


                    imgList.add(CloudUtil.getBitmapById(00000, getContext()));
                    imgList.add(CloudUtil.getBitmapById(44444, getContext()));
                    imgList.add(CloudUtil.getBitmapById(55555, getContext()));
                    imgList.add(CloudUtil.getBitmapById(66666, getContext()));
                    imgList.add(CloudUtil.getBitmapById(77777, getContext()));
                    imgList.add(CloudUtil.getBitmapById(88888, getContext()));
                    imgList.add(CloudUtil.getBitmapById(99999, getContext()));

                    Log.d(TAG, "imgList: " + imgList);
                    Log.d(TAG, "imgBitmapPairList: " + imgBitmapPairList);

                    imgBitmapPairList.addAll(imgList);

                    Log.d(TAG, "imgBitmapPairList AFTER loop: " + imgBitmapPairList);

                    albumList = createAlbum(imgBitmapPairList);
                    Message message = handler.obtainMessage();
                    message.what = 99;
                    message.obj = imgBitmapPairList;
                    handler.sendMessage(message);

                    Log.d(TAG, "albumList: " + albumList);
                }
            }
        };
        try {
            ebcConnector.receiveImages(callback);
        } catch (Exception e) {
            Log.d(TAG, "getImageFiles: failed receiving images()\nFailed with exception: " + e);
        }
    }

    private void checkPermissions(){
        if(Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //Requesting permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

    /**
     * Take image files and sort them into albums by date
     * Albums are sorted by date
     * Images without time stamp are left out (we don't want them)
     *
     * @param imagesPairs List of image files
     * @return list of created Albums
     */
    private LinkedList<Albums> createAlbum(ArrayList<Pair<ImageMessage, Bitmap>> imagesPairs) {
        LinkedList<Albums> albums = new LinkedList<>();

        if (imagesPairs != null) {
            for (Pair<ImageMessage, Bitmap> imgPairs:imagesPairs) {
                ImageMessage imageMessage = imgPairs.first;
                Date date = getDate(imageMessage);
                long id = imageMessage.getId();

                // sort out images without time stamp
                if (date != null) {
                    if (albums.size() == 0) {
                        ArrayList<Long> imgArray = new ArrayList<>();
                        imgArray.add(id);
                        Albums a = new Albums(imgArray, date);
                        albums.add(a);
                    } else {
                        int where = 0;
                        boolean changed = false;
                        // compare image date to album date and sort image in right album
                        for (Albums i:albums) {
                            int compare = i.getDate().compareTo(date);
                            if (compare == 0) {
                                i.addImage(id);
                                where = -1;
                                changed = true;

                            } else if (compare < 0) {
                                where = albums.indexOf(i);
                                changed = true;

                            } else if (albums.indexOf(i) == albums.size()-1) {
                                where = albums.indexOf(i) + 1;
                            }
                            if (changed) {
                                break;
                            }
                        }
                        if (where > -1) {
                            ArrayList<Long> imgArray = new ArrayList<>();
                            imgArray.add(id);
                            Albums a = new Albums(imgArray, date);
                            albums.add(where, a);
                        }
                    }
                }
            }
        }
        return albums;
    }

    /**
     * get the time stamp of an image
     * @param img image we want to get the date from
     * @return date of image with 00:00:00 as time
     */
    public Date getDate(ImageMessage img) {
        Date date = img.getDate();
        Log.d(TAG, "getDate: before removing Time" + date);
        date = removeTime(date);
        Log.d(TAG, "getDate: after removing time" + date);
        return date;
    }

    /**
     * Remove time stamp (HH:MM:SS) from Date
     *
     * @param date date we want to remove the time from
     * @return date with 00:00:00 as time
     */
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            if (msg.what == 99) {
                adapter.notifyDataSetChanged();
                gridView.setAdapter(adapter);
            }
            super.handleMessage(msg);
        }
    };
}
