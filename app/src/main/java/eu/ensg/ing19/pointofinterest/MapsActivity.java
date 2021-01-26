package eu.ensg.ing19.pointofinterest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import eu.ensg.ing19.pointofinterest.dataaccess.DataBaseHelper;
import eu.ensg.ing19.pointofinterest.dataaccess.PointOfInterestDAO;
import eu.ensg.ing19.pointofinterest.dataaccess.UserDAO;
import eu.ensg.ing19.pointofinterest.dataobject.PointOfInterest;
import eu.ensg.ing19.pointofinterest.dataobject.User;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Constants {

    // private attributes
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Marker lastLocationMarker = null;

    private FloatingActionButton fb_addPoint;

    private SQLiteDatabase db;
    private User user;
    private PointOfInterestDAO pointOfInterestDAO;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                MapsActivity.this.setLastLocationMarkerPosition(location);
            }

        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
    };

    // events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra(EXTRA_USER);
        }

        // get instances
        fb_addPoint = findViewById(R.id.fb_addPoint);

        fb_addPoint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                addPointOfInterest();
                return true;
            }
        });

        // Database
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        db = dataBaseHelper.getWritableDatabase();
        pointOfInterestDAO = new PointOfInterestDAO(db);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng maison = new LatLng(45.9604682, 5.8287048);
        mMap.addMarker(new MarkerOptions().position(maison).title("Home sweet home !"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(maison));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        getLastPosition();

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("ENSG", "Permission");
    }

    // private functions
    private void startLocationUpdates() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null && locationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void setLastLocationMarkerPosition(Location location) {
        if (mMap == null) return;

        geocode(location);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (lastLocationMarker == null) {
            lastLocationMarker = mMap.addMarker( new MarkerOptions().position(latLng).title("Current position"));
        } else {
            lastLocationMarker.setPosition(latLng);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 2000, null);
    }

    private void getLastPosition() {

        // Tests if permission on location is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    // Success
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Try to add a marker on the map
                                MapsActivity.this.setLastLocationMarkerPosition(location);
                            }
                        }
                    })
                    // Failure
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        } else {
            // Is not...
            Toast.makeText(this, "Erreur: impossible d'accéder à la position", Toast.LENGTH_LONG).show();
        }
    }

    private void geocode(Location location) {
        FetchAddressIntentService.startActionGeocode(this, location, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                // Is success?
                if (resultCode == FetchAddressIntentService.EXTRA_RESULT_SUCCESS) {
                    // Yes (result is address)
                    String result = resultData.getString(FetchAddressIntentService.EXTRA_RESULT_ADDRESS);
                    MapsActivity.this.setCurrentLocationMarkerTitle(result);
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                } else {
                    // No (result is error message)
                    String message = resultData.getString(FetchAddressIntentService.EXTRA_RESULT_MESSAGE);
                    MapsActivity.this.setCurrentLocationMarkerTitle("");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setCurrentLocationMarkerTitle(String title) {
        lastLocationMarker.setTitle(title);
    }

    private void addPointOfInterest() {

        Log.i(LOG_TAG, "Create POI !!!!");

        PointOfInterest poi = new PointOfInterest(lastLocationMarker.getTitle(), "",
                lastLocationMarker.getPosition().latitude,
                lastLocationMarker.getPosition().longitude,
                user.getId());

        poi = pointOfInterestDAO.create(poi);

    }
}