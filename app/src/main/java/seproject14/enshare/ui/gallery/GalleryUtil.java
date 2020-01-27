package seproject14.enshare.ui.gallery;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.exifinterface.media.ExifInterface;
import seproject14.enshare.service.EBCConnector;

import static android.media.ExifInterface.TAG_DATETIME_ORIGINAL;
import static android.media.ExifInterface.TAG_GPS_LATITUDE;
import static android.media.ExifInterface.TAG_GPS_LONGITUDE;


/**
 *
 * Utility class to handle common gallery functionalities.
 */
public class GalleryUtil {

    static HashMap<ImageModel, String> mapFilesSortedByLocation = new HashMap<>();

    static HashMap<ImageModel, String> mapFilesSortedByDate = new HashMap<>();

    /**
     * Method that deletes the specified file.
     * @param file file to be deleted.
     */
    public static void deleteImage(File file) {
        boolean delete = file.delete();
    }

    /**
     * Method that converts a given string of gps coordinates to degrees
     * @param gps_coords GPS coordinates in the form of string.
     * @return a double value which could be the latitude /longitude of the input coordinates.
     */
    public static Double convertDegreeToDecimal(String gps_coords) {
        Double result = null;
        String[] DMS = gps_coords.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double degree0 = new Double(stringD[0]);
        Double degree1 = new Double(stringD[1]);
        Double FloatD = degree0 / degree1;

        String[] stringM = DMS[1].split("/", 2);
        Double minute0 = new Double(stringM[0]);
        Double minute1 = new Double(stringM[1]);
        Double FloatM = minute0 / minute1;

        String[] stringS = DMS[2].split("/", 2);
        Double second0 = new Double(stringS[0]);
        Double second1 = new Double(stringS[1]);
        Double FloatS = second0 / second1;

        result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    }

    /**
     * Gets the location address from the given Longitude and Latitude
     * @param gps_latitude
     * @param gps_longitude
     * @return address of the location
     */
    public static String getLocationFromCoordinates(double gps_latitude, double gps_longitude, Context context) {
        List<Address> address;
        String location="";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            address = geocoder.getFromLocation(gps_latitude, gps_longitude, 1);
            location = address.get(0).getAddressLine(0);
        }
        catch (IndexOutOfBoundsException e){
            location = "Unknown location";
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Method to upload the image from the app to the cloud.
     * @param imageModel Name of the model that has to be shared.
     * @param ebcConnector
     * @param context
     */
    public static void shareImage(ImageModel imageModel, EBCConnector ebcConnector, Context context){
       Double latitude = imageModel.getImageLatitude();
       Double longitude = imageModel.getImageLongitude();
            if(latitude != null && longitude != null){

                ebcConnector.sendImage(imageModel.getImagePath(), imageModel.getImageDate(), latitude, longitude);
            } else{
                Toast.makeText(context, "Image cannot be shared as it does not contain Location Information", Toast.LENGTH_LONG).show();
            }
    }

    /**
     * Method to get image files from the Enshare specific folder.
     * @param root root folder from which the image files are to be displayed.
     * @return Arraylist of files in the "EBC" folder
     */
    public static ArrayList<File> getImageFiles(File root) {
        ArrayList<File> fileList;
        fileList = new ArrayList<File>();
        if(!root.exists()){
            root.mkdir();
        }
        File[] files = root.listFiles();
        if(files != null){
            for (File file : files) {
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png") || file.getName().endsWith(".gif")) {
                    fileList.add(file);
                }
            }
        }

        return fileList;
    }

    /**
     * Method to sort the images based on date of the images captured.
     * @param modelList list of image models to be sorted.
     * @return sorted list of files based on Date.
     * @throws IOException
     */
    public static ArrayList<ImageModel> sortByDate(ArrayList<ImageModel> modelList) throws IOException {
        GalleryFragment.flag =false;

        ArrayList<ImageModel> sortedByDate = modelList;
        if (sortedByDate != null && sortedByDate.size() >= 1 && mapFilesSortedByDate.size() != modelList.size()) {
            mapFilesSortedByDate.clear();
            for (int i = 0; i < sortedByDate.size(); i++) {
                ImageModel model = sortedByDate.get(i);
                mapFilesSortedByDate.put(model, model.getDateString());
            }
        }

        Map<ImageModel, String> hm2 = sortByValue(mapFilesSortedByDate);
        assert sortedByDate != null;
        sortedByDate.removeAll(modelList);

        sortedByDate = new ArrayList<>(hm2.keySet());
        Collections.reverse(sortedByDate);
        return sortedByDate;
    }

    /**
     * Method to sort the images based on the location of the images captured.
     * @param modelList list of image models to be sorted.
     * @return sorted list of files based on Location.
     */
    public static ArrayList<ImageModel> sortByLocation(ArrayList<ImageModel> modelList){
        GalleryFragment.flag = true;
        ArrayList<ImageModel> sortedByLocation = modelList;
        mapFilesSortedByLocation.clear();
        for (ImageModel model : sortedByLocation) {
            mapFilesSortedByLocation.put(model, model.getShortLocation());
        }
        Map<ImageModel, String> hm2 = sortByValue(mapFilesSortedByLocation);
        sortedByLocation.removeAll(sortedByLocation);

        sortedByLocation = new ArrayList<>(hm2.keySet());
        return sortedByLocation;
    }

    /**
     * Method to help sort the values in an ascending order.
     * @param hm Hashmap containing the items to be sorted.
     * @return hashmap sorted in an ascending fashion.
     */
    public static HashMap<ImageModel, String> sortByValue(HashMap<ImageModel, String> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<ImageModel, String> > list =
                new LinkedList<Map.Entry<ImageModel, String> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<ImageModel, String> >() {
            public int compare(Map.Entry<ImageModel, String> o1,
                               Map.Entry<ImageModel, String> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<ImageModel, String> temp = new LinkedHashMap<ImageModel, String>();
        for (Map.Entry<ImageModel, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    /**
     * Method to get the EXIF information of the desired attribute.
     * @param exif ExifInterface of the image.
     * @param tag attribute that needs to be extracted from the image.
     * @return String containing the extracted value for the mentioned attribute.
     */
    private static String getExifTag(ExifInterface exif, String tag)
    {
        String attribute = exif.getAttribute(tag);
        return (null != attribute ? attribute : "");
    }

    /**
     * Method to initialise the gallery fragment with Images and related metadata.
     */
    public static ArrayList<ImageModel> initializeGallery(Context context) {
        ArrayList<File> fileListImages = getImageFiles(new File(GalleryFragment.IMG_FOLDER_PATH));
        ArrayList<ImageModel> imageModelList = new ArrayList<>();
        for(File imgFile : fileListImages){
            imageModelList.add(getImageModel(imgFile, context));
        }
        return imageModelList;
    }

    /**
     * Method to get the imageModel of any File.
     * @param imgFile path of the image file.
     * @return Image model of the file.
     */
    public static ImageModel getImageModel(File imgFile, Context context){
        ImageModel imageModel = null;
        try {
            ExifInterface exifInterface = new ExifInterface(imgFile);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH);
            String imgDate = getExifTag(exifInterface, TAG_DATETIME_ORIGINAL);
            Date date;
            Double latitude;
            Double longitude;
            String shortLocation;
            if(imgDate != ""){
                date = sdf.parse(imgDate);
            } else{
                date = new Date(0);
                imgDate = date.toString();
            }
            String latitudeString = getExifTag(exifInterface, TAG_GPS_LATITUDE);
            String longitudeString = getExifTag(exifInterface, TAG_GPS_LONGITUDE);
            String location ;
            if(!latitudeString.equals("") && !longitudeString.equals("")){
                latitude = convertDegreeToDecimal(latitudeString);
                longitude = convertDegreeToDecimal(longitudeString);

                location = getLocationFromCoordinates(latitude, longitude, context);
                shortLocation = location.split(",")[0];
            } else{
                latitude = null;
                longitude = null;

                location = "Unknown Location";
                shortLocation = "Unknown";
            }
            imageModel = new ImageModel(imgFile.getPath(), date, location,latitude, longitude, shortLocation, imgDate);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return imageModel;
    }
}
