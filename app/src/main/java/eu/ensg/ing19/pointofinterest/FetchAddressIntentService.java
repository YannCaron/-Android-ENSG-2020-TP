package eu.ensg.ing19.pointofinterest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchAddressIntentService extends IntentService {

    public static final String TAG = "ENSG";

    private static final String ACTION_GEOCODE = "eu.ensg.ing19.mapproject.action.GEOCODE";
    // Parameters name
    private static final String EXTRA_PARAM_LOCATION = "fr.ign.positionname.extra.LOCATION";
    private static final String EXTRA_PARAM_RECEIVER = "fr.ign.positionname.extra.RECEIVER";

    // Result name/code
    protected static final int EXTRA_RESULT_ERROR = 0;
    protected static final int EXTRA_RESULT_SUCCESS = 1;
    protected static final String EXTRA_RESULT_ADDRESS = "fr.ign.positionname.extra.ADDRESS";
    protected static final String EXTRA_RESULT_MESSAGE = "fr.ign.positionname.extra.MESSAGE";

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionGeocode(Context context, Location location, ResultReceiver receiver) {
        Intent intent = new Intent(context, eu.ensg.ing19.pointofinterest.FetchAddressIntentService.class);
        intent.setAction(ACTION_GEOCODE);
        intent.putExtra(EXTRA_PARAM_LOCATION, location);
        intent.putExtra(EXTRA_PARAM_RECEIVER, receiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GEOCODE.equals(action)) {
                final Location location = intent.getParcelableExtra(EXTRA_PARAM_LOCATION);
                final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_PARAM_RECEIVER);
                handleActionGeocode(location, receiver);
            }
        }
    }

    private void handleActionGeocode(Location location, ResultReceiver receiver) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // Store results
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            String finalMessage = String.format(Locale.getDefault(), "%s. Latitude = %f, Longitude = %f", errorMessage, location.getLatitude(), location.getLongitude());
            Log.e(TAG, finalMessage, illegalArgumentException);
        }

        // result bundle (to send back data to receiver)
        Bundle resultBundle = new Bundle();
        int resultCode;

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            resultBundle.putString(EXTRA_RESULT_MESSAGE, errorMessage);
            resultCode = EXTRA_RESULT_ERROR;
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            resultCode = EXTRA_RESULT_SUCCESS;
            resultBundle.putString(EXTRA_RESULT_ADDRESS, TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
        receiver.send(resultCode, resultBundle);
    }

}