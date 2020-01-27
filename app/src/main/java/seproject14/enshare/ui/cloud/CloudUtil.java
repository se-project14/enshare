package seproject14.enshare.ui.cloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;

import seproject14.enshare.R;
import seproject14.enshare.database.ImageMessage;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CloudUtil {

    /**
     * if /enshare/cloud folder already exists: saving image in /enshare/cloud folder
     * if /enshare/cloud doesn't exist: create a new /enshare/cloud folder with the image
     * @param finalBitmap bitmap with the image we want to save
     */
    public static void saveImage(Bitmap finalBitmap, Context context) {

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/enshare/cloud");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    // needed for getting the mocked pictures
    public static Pair<ImageMessage, Bitmap> getBitmapById(long id, Context context){
        if (id == 44444) {
            ImageMessage message = new ImageMessage();
            message.setId(44444);
            Date date = new Date();
            date.setDate(15);
            date.setMonth(1);
            date.setYear(120);
            date.setHours(18);
            date.setMinutes(12);
            date.setSeconds(2);
            message.setDate(date);
            message.setLatitude(49.258523);
            message.setLongitude(7.048906);
            message.setUsername("Bob");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.example1262609836775746440);
            return new Pair<>(message, rotateBitmap(bitmap, 90));
        } else if (id == 55555) {
            ImageMessage message = new ImageMessage();
            message.setId(55555);
            Date date = new Date();
            date.setDate(22);
            date.setMonth(4);
            date.setYear(117);
            date.setHours(13);
            date.setMinutes(0);
            date.setSeconds(13);
            message.setDate(date);
            message.setLatitude(51.929033);
            message.setLongitude(-8.570932);
            message.setUsername("Alice");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ex1);
            return new Pair<>(message, rotateBitmap(bitmap, 90));
        } else if (id == 66666) {
            ImageMessage message = new ImageMessage();
            message.setId(66666);
            Date date = new Date();
            date.setDate(16);
            date.setMonth(4);
            date.setYear(117);
            date.setHours(17);
            date.setMinutes(7);
            date.setSeconds(52);
            message.setDate(date);
            message.setLatitude(53.012409);
            message.setLongitude(-6.320837);
            message.setUsername("Alice");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ex2);
            return new Pair<>(message, rotateBitmap(bitmap, 90));
        } else if (id == 77777) {
            ImageMessage message = new ImageMessage();
            message.setId(77777);
            Date date = new Date();
            date.setDate(18);
            date.setMonth(4);
            date.setYear(117);
            date.setHours(18);
            date.setMinutes(39);
            date.setSeconds(12);
            message.setDate(date);
            message.setLatitude(53.268365);
            message.setLongitude(-9.055228);
            message.setUsername("Alice");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ex3);
            return new Pair<>(message, rotateBitmap(bitmap, 90));
        } else if (id == 88888) {
            ImageMessage message = new ImageMessage();
            message.setId(88888);
            Date date = new Date();
            date.setDate(20);
            date.setMonth(4);
            date.setYear(117);
            date.setHours(14);
            date.setMinutes(0);
            date.setSeconds(17);
            message.setDate(date);
            message.setLatitude(53.548569);
            message.setLongitude(-9.915423);
            message.setUsername("Alice");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ex4);
            return new Pair<>(message, rotateBitmap(bitmap, 90));
        } else if (id == 99999) {
            ImageMessage message = new ImageMessage();
            message.setId(99999);
            Date date = new Date();
            date.setDate(20);
            date.setMonth(4);
            date.setYear(117);
            date.setHours(16);
            date.setMinutes(4);
            date.setSeconds(26);
            message.setDate(date);
            message.setLatitude(53.548569);
            message.setLongitude(-9.915423);
            message.setUsername("Alice");
            return new Pair<>(message, BitmapFactory.decodeResource(context.getResources(), R.drawable.ex5));
        } else if (id == 00000) {
            ImageMessage message = new ImageMessage();
            message.setId(00000);
            Date date = new Date();
            date.setDate(20);
            date.setMonth(4);
            date.setYear(117);
            date.setHours(16);
            date.setMinutes(4);
            date.setSeconds(26);
            message.setDate(date);
            message.setLatitude(52.136402);
            message.setLongitude(-9.013264);
            message.setUsername("Alice");
            return new Pair<>(message, BitmapFactory.decodeResource(context.getResources(), R.drawable.ex6));
        } else {
            Log.d(TAG, "getBitmapById: wrong id");
            ImageMessage message = new ImageMessage();
            message.setId(id);
            message.setDate(new Date(0));
            message.setLatitude(45.12675246);
            message.setLongitude(12.12675246);
            return new Pair<>(message, BitmapFactory.decodeResource(context.getResources(), R.drawable.wrong_id_image));
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
