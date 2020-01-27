package seproject14.enshare.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import org.mpisws.embeddedsocial.ESClient;
import org.mpisws.encounters.EncounterBasedCommunication;
import org.mpisws.messaging.PrivateMessage;
import org.mpisws.messaging.ReceivedMessageWrapper;
import org.mpisws.messaging.constraints.EncounterQueryConstraint;
import org.mpisws.messaging.constraints.SpaceTimeIntersectConstraint;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import seproject14.enshare.R;
import seproject14.enshare.database.EnshareDbHelper;
import seproject14.enshare.database.ImageMessage;
import seproject14.enshare.ui.MainActivity;
import seproject14.enshare.ui.settings.SettingsFragment;

/**
 * The EBC Connector works as a bridge between the actual
 * EBC library and our app. It also adapts the API more
 * towards the use case we need in our app.
 */
public class EBCConnector {
    private static final String TAG = "EnShareApp|EBCConnector";
    private static final long TIME_PRECISION = 60 * 60 * 1000; // 60 minutes
    private static final long ENCOUNTER_DURATION = 1 * 1000; // 5 minutes
    private static final double ENCOUNTER_ANGLE = 0.45d; // 100km square in degrees
    private static final int FILE_BUFFER_SIZE = 8192;
    private static final String MESSAGE_DELIM = "$$§§$$";

    private EnshareDbHelper messageDb;
    private boolean initialized = false;

    /**
     * Returns whether the underlying EBC instance has been
     * initialized.
     *
     * Note, that the `initialized' state does only change when
     * called through {@link #initialize(Context)}.
     *
     * @return true if the EBC instance has been initialized
     */
    public synchronized boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Initializes the underlying EBC instance.
     *
     * @param context The Android Activity that should be used
     *                as context.
     */
    public synchronized void initialize(Context context) {
        Log.d(TAG, "initialize");
        assert !this.isInitialized();

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        ebc.initialize(context, R.raw.ebc_config);

        this.messageDb = new EnshareDbHelper(context);
        this.initialized = true;
    }

    /**
     * this method returns whether is currently logged in.
     *
     * @return true if the user is logged in
     */
    public synchronized boolean isLoggedIn() {
        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        return ebc.isSignedIn();
    }

    /**
     * Logs the user in using a provided Google Token.
     * This should only be called if there is currently
     * no user logged in.
     *
     * @param googleToken the Google OAuth token
     * @exception IllegalArgumentException if the provided token is empty
     * @exception IllegalStateException if the user is already logged in
     */
    public synchronized void login(String googleToken) {
        Log.d(TAG, "login " + googleToken);
        if (googleToken == null) {
            throw new IllegalArgumentException("Arguments should not be null");
        }

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.IUserAccountClient uac = ebc.getUserAccountClient();
        uac.loginAccountWithGoogle(googleToken);
    }

    /**
     * Logs the user out.
     * This should only be called if there is actually a user
     * logged in.
     *
     * @exception IllegalStateException if the user is not logged in
     */
    public synchronized void logout() {
        Log.d(TAG, "logout");
        if (!this.isLoggedIn()) {
            throw new IllegalStateException("Not logged in");
        }

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.IUserAccountClient uac = ebc.getUserAccountClient();
        uac.signOut();
    }

    /**
     * Launches the background EBC service, if not already running.
     * This should only be called if the user is logged in.
     *
     * @exception IllegalStateException if the user is not logged in
     */
    public void startService() {
        Log.d(TAG, "startService");
        if (!this.isLoggedIn()) {
            throw new IllegalStateException("Should be logged in");
        }

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.ISDDRClient sddrClient = ebc.getSDDRClient();
        sddrClient.startFormingEncounters();
    }

    /**
     * Stops the background EBC service.
     * This should only be called if the user is logged in.
     *
     * @exception IllegalStateException if the user is not logged in
     */
    public void stopService() {
        Log.d(TAG, "stopService");

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.ISDDRClient sddrClient = ebc.getSDDRClient();
        sddrClient.stopFormingEncounters();
    }

    /**
     * Confirm future encounters with other devices. Photos can
     * only be shared with confirmed encounters.
     * This should only be called if the user is logged in.
     *
     * @exception IllegalStateException if the user is not logged in
     */
    public void startEncounterConfirmations() {
        Log.d(TAG, "startEncounterConfirmations");

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.ISDDRClient sddrClient = ebc.getSDDRClient();
        sddrClient.confirmAndCommunicateUsingNetwork();
    }

    /**
     * Stop confirming future encounters with other devices.
     * This should only be called if the user is logged in.
     *
     * @exception IllegalStateException if the user is not logged in
     */
    public void stopEncounterConfirmations() {
        Log.d(TAG, "stopEncounterConfirmations");

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.ISDDRClient sddrClient = ebc.getSDDRClient();
        sddrClient.disableConfirmation();
    }

    /**
     * Sends a string to all encounters that happened within
     * given time and location.
     * This should only be called if the user is logged in.
     *
     * @param message the string to should be sent to other
     *                devices
     * @param date the time, during which the devices should
     *             have been linked
     * @param latitude the location latitude, where the
     *                 pairings should have happened
     * @param longitude the location longitude, where the
     *                  pairings should have happened
     * @exception IllegalArgumentException if one of the arguments is invalid
     * @exception IllegalStateException if the user is not logged in
     */
    public void sendString(String message, Date date, double latitude, double longitude) {
        Log.d(TAG, "sendString " + message + " " + date + " " + latitude + " " + longitude);
        if (message == null || date == null /*|| latitude == null || longitude == null*/) {
            throw new IllegalArgumentException("Arguments should not be null");
        }

        // check whether we should send as anonymous or with username
        SharedPreferences sharedPreferences = MainActivity._staticInstance.getPreferences(Context.MODE_PRIVATE);
        String username = MainActivity._staticInstance.getAccountName();
        if (SettingsFragment.anonymousState) {
            username = "Anonymous";
        }

        // Construct the message to send
        String constructedMessage = message
                + MESSAGE_DELIM + username
                + MESSAGE_DELIM + latitude
                + MESSAGE_DELIM + longitude;

        // Form the encounter constraints
        long timeStart = 0;
        long timeEnd = System.currentTimeMillis();

        EncounterQueryConstraint constraint = new SpaceTimeIntersectConstraint(
                timeStart,
                timeEnd,
                ENCOUNTER_DURATION,
                new EncounterQueryConstraint.SpaceRegion(latitude, longitude, ENCOUNTER_ANGLE),
                false
        );
        //constraint = new AllEncountersConstraint();

        Log.d(TAG, String.format("constraint[%s]", constraint.toQueryString()));

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.ISDDRClient sddrClient = ebc.getSDDRClient();
        sddrClient.updateDatabaseOnAgent();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EncounterBasedCommunication.ICommunicationClient commClient = ebc.getCommunicationClient();
        commClient.sendDirectMessageToEncounters(constructedMessage, constraint, true /* false */);

        // store the message entry into the database
        this.messageDb.insert(ImageMessage.TYPE_SENT, date, latitude, longitude, message, username);
    }

    /**
     * Sends a image to all encounters that happened within
     * given time and location.
     * This works by encoding the image as Base64 string, and then
     * forwarding the call to {@link #sendString(String, Date, double, double)}.
     * This should only be called if the user is logged in.
     *
     * @param filename the filename of the image to be sent
     * @param date the time, during which the devices should
     *             have been linked
     * @param latitude the location latitude, where the
     *                 pairings should have happened
     * @param longitude the location longitude, where the
     *                  pairings should have happened
     * @exception IllegalArgumentException if one of the arguments is invalid
     * @exception IllegalStateException if the user is not logged in
     */
    public void sendImage(String filename, Date date, double latitude, double longitude) {
        Log.d(TAG, "sendImage " + filename + " " + date + " " + latitude + " " + longitude);
        if (filename == null || date == null /*|| latitude == null || longitude == null*/) {
            throw new IllegalArgumentException("Arguments should not be null");
        }

        try {
            // see https://stackoverflow.com/a/17874349
            int bytesRead;
            byte[] buffer = new byte[FILE_BUFFER_SIZE];
            InputStream inputStream = new FileInputStream(filename);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            Base64.Encoder encoder = Base64.getEncoder();
            String message = encoder.encodeToString(output.toByteArray());

            this.sendString(message, date, latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches for new images.
     * This operations runs asynchronously. When it is completed,
     * the provided {@linkplain ReceiveImagesCallback} will be called.
     * This should only be called if the user is logged in.
     *
     * Note: this does not return the received images. It will save
     * them into the local database instead. The user can use the
     * provided callback parameter to then call
     * {@linkplain #getImages(boolean, int, int)}.
     *
     * @param callback the callback implementation to be called
     *                 when the operation is completed.
     * @exception IllegalStateException if the user is not logged in
     */
    public void receiveImages(ReceiveImagesCallback callback) {
        Log.d(TAG, "receiveImages");

        ESClient.GetReceivedMessagesCallback internalCallback = new ESClient.GetReceivedMessagesCallback() {
            @Override
            public void processReceivedMessages(List<ReceivedMessageWrapper> msgs) {
                Log.d(TAG, msgs.toString() + ", " + msgs.size());

                for (ReceivedMessageWrapper messageWrapper : msgs) {
                    Log.d(TAG, String.format("RECEIVED [%s:%s]",
                            messageWrapper.getMsgType(),
                            messageWrapper.getEncounterForwardingMessage().getMsgText()));

                    // only handle messages of type PRIVATE_MESSAGE
                    ReceivedMessageWrapper.MsgTyp messageType = messageWrapper.getMsgType();
                    if (messageType == ReceivedMessageWrapper.MsgTyp.PRIVATE_MESSAGE) {
                        PrivateMessage message = messageWrapper.getPrivateMessage();
                        String constructedMessage = message.getMsgText();
                        Date date = new Date(messageWrapper.getTimestamp());

                        // extract the parts from the message
                        String[] parts = constructedMessage.split(MESSAGE_DELIM);
                        if (parts.length != 4) {
                            Log.e(TAG, "incorrect number of things in message");
                            continue;
                        }
                        String messageText = parts[0];
                        String username = parts[1];
                        Double latitude = Double.valueOf(parts[2]);
                        Double longitude = Double.valueOf(parts[3]);

                        // store the message entry into the database
                        // TODO: use actual values
                        messageDb.insert(ImageMessage.TYPE_RECEIVED, date, latitude, longitude, messageText, username);
                    }
                }

                // execute the callback
                callback.executeCallback();
            }
        };

        EncounterBasedCommunication ebc = EncounterBasedCommunication.getInstance();
        EncounterBasedCommunication.ICommunicationClient commClient = ebc.getCommunicationClient();
        commClient.getUnreadMsgs(internalCallback);
    }

    public interface ReceiveImagesCallback {
        void executeCallback();
    }

    /**
     * Returns the images stored in the local database.
     * This fetches the local database and returns all images
     * matching the provided query parameters.
     *
     * @param sortByLocation true, if results should be sorted by location,
     *                       false, if results should be sorted by time.
     * @param start the index of the first result that should be returned.
     *              This is useful for pagination or a infinite scroll
     *              implementation.
     * @param count The maximum amount of results that should be returned.
     *              If 0, everything will be returned.
     * @return a list of {@link Pair<ImageMessage, Bitmap>} matching the query params.
     */
    public List<Pair<ImageMessage, Bitmap>> getImages(boolean sortByLocation, int start, int count) {
        // retrieve the matching messages from the database
        int sort = sortByLocation
                ? EnshareDbHelper.DB_SORT_LOCATION_ASC
                : EnshareDbHelper.DB_SORT_DATE_DESC;
        List<ImageMessage> messages = this.messageDb.getReceived(sort, start, count);
        List<Pair<ImageMessage, Bitmap>> returnList = new ArrayList<>(messages.size());

        // decode the image in each message
        Base64.Decoder decoder = Base64.getDecoder();
        for (ImageMessage message : messages) {
            byte[] imageData = decoder.decode(message.getMessage());
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            returnList.add(new Pair<>(message, imageBitmap));
        }

        return returnList;
    }

    /**
     * Returns a single image by its Id.
     * This fetches the local database and returns all images
     * matching the provided query parameters.
     *
     * @param id The internal id of the image.
     * @return a {@link Pair<ImageMessage, Bitmap>} for the image id.
     */
    public Pair<ImageMessage, Bitmap> getImage(long id) {
        // retrieve the matching message from the database
        ImageMessage message = this.messageDb.getById(id);

        // decode the image in each message
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] imageData = decoder.decode(message.getMessage());
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        return new Pair<>(message, imageBitmap);
    }
}
