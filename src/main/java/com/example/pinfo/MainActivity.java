package com.example.pinfo;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String Tag ="app";

    private ArrayList<Message> allmsg=new ArrayList<Message>();

    private static int SPLASH_SCREEN_TIME_OUT=15000;
    private FirebaseFirestore fb_db=FirebaseFirestore.getInstance();
    private CollectionReference col_ref;
    Geocoder geocoder;
    List<Address> addresses;

//    Location Variables

    private String state;
    private static double latitude,longitude;
    Location myLocation;
    private LocationRequest myLocationRequest;
    GoogleApiClient myGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 90;
    public static final int LOCATION_PERMISSIONS_REQUEST = 99;
    public static final int LOCATION_ENABLE_REQUEST = 97;
    private long UPDATE_INTERVAL = 10000;
    private long FASTEST_INTERVAL = 10000;

    Intent i1=null;

    //	#After completion of 2000 ms, the next activity will get started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.flash_icon_page);
        //this will bind your MainActivity.class file with activity_main.

        geocoder = new Geocoder(this, Locale.getDefault());
        //Location Code Start
//        if (ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            SPLASH_SCREEN_TIME_OUT=4000;
//        }
        createLocationRequest();
        AskPermission();

        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

//          Location Code ends ^^



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Intent is used to switch from one activity to another.

                i1=new Intent(MainActivity.this, Login.class);
                i1.putExtra("alldata", allmsg);
                if(myLocation!=null) {

                    startActivity(i1);
                }
                //invoke the SecondActivity.
                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }


    //Location Functions
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag,"I am here................in onstart");
        if (myGoogleApiClient != null) {
            myGoogleApiClient.connect();
            Log.d(Tag,"I am here...............google client not null");
        }
    }

    @Override
    protected void onStop() {
//        if (myGoogleApiClient.isConnected()) {
//            Log.d(Tag,"I am here......................in stop stopping connection");
//            LocationServices.FusedLocationApi.removeLocationUpdates(myGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
//            myGoogleApiClient.disconnect();
//        }

        super.onStop();
    }

    private void AskPermission() {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSIONS_REQUEST );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQUEST : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED  ) {
                    startUpdates();
                    Log.d(Tag,"onReqPerResult");

                } else {
                    this.finish();
                }
                return;
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case LOCATION_ENABLE_REQUEST:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        Log.d(Tag,"here4......................start update called after permission granted in OnActivityResult");
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        this.finish();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    protected void createLocationRequest() {
        myLocationRequest = LocationRequest.create();
        myLocationRequest.setInterval(UPDATE_INTERVAL);
        myLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        buildLocationSettingsRequest();
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(myLocationRequest);
        // LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                Toast.makeText(getApplication(), "permission given", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                LOCATION_ENABLE_REQUEST);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);

        startUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void startUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(Tag,"here4......................permission granted");
            LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    myLocation=location;
//                    Latitude = (long)location.getLatitude();
//                    Longitude = (long)location.getLongitude();


                    if(myLocation!=null){

                        latitude = (double)myLocation.getLatitude();
                        longitude = (double)myLocation.getLongitude();
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            state = addresses.get(0).getAdminArea();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
//        else{
//            super.onDestroy();
//        }

//                    Toast.makeText(MainActivity.this,"State : "+state,Toast.LENGTH_LONG).show();

                    col_ref =fb_db.collection("AllPosts").document(state).collection(state+"_collection");

                    col_ref.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                Message msg= new Message();
                                Calculate_Distance objCD = new Calculate_Distance();
                                @Override
                                public void onSuccess(QuerySnapshot docSnap) {
                                    allmsg.clear();
                                    for(QueryDocumentSnapshot ds : docSnap){
//                                        String id=ds.getId();
                                        msg= ds.toObject(Message.class);
//                                        msg.setmsg_id(id);
//                                        Log.w("MSGID IS:",msgId);
                                        //add messages only which are within the range using double latitude and longitude variables
                                        allmsg.add(msg);
                                        Log.w("Pinfo","before"+allmsg.size());
                                    }
                                    allmsg=objCD.get_allPost(allmsg,MainActivity.this.latitude,MainActivity.this.longitude);
                                    Log.w("Pinfo","after"+allmsg.size());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "ERROR" + e.toString(),Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, e.toString());
                                }

                            });
//                    i1=new Intent(MainActivity.this, Login.class);
//            //Intent is used to switch from one activity to another.
//            i1.putExtra("alldata",allmsg);
//            startActivity(i1);
                    Log.d(Tag,"here4 MainAct......................location updated in start update "+Double.toString(latitude)+" "+Double.toString(longitude));
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }
    }

}

