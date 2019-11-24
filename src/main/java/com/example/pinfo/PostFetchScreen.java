package com.example.pinfo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.pinfo.MainActivity.Tag;

public class PostFetchScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private ArrayList<Message> allmsg= new ArrayList<Message>();
    private static final String TAG = "FetchPosts";
    private TextView textView;
    private Button btn_post;
    //recycler variables
    RecyclerView recyclerView;
    EditText et_post_title,et_post_text;
    Button btn_AddPost;

    //Location Variables
    private double latitude,longitude;
    Location myLocation;
    private LocationRequest myLocationRequest;
    GoogleApiClient myGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 90;
    public static final int LOCATION_PERMISSIONS_REQUEST = 99;
    public static final int LOCATION_ENABLE_REQUEST = 97;
    private long UPDATE_INTERVAL = 60000;
    private long FASTEST_INTERVAL = 60000;

    //Geocoder to get city and state
    Geocoder geocoder;
    List<Address> addresses;
    private static final String[] Key={"latitude","longitude","post_title","post_text","firebase_autogenID","owner_id","state","city","postalCode","address","msg_id","nick"};
    private static final String[] user_Key={"Name","Nick_Name","F_Name","Email","Owner_ID","Interacted_Posts_Ref","Photo"};

    //User Details Variable
    GoogleSignInClient mGoogleSignInClient;
    private String state="",timestamp="";
    private String owner_id="";
    private String nick;

//    User_Details user;
    Map<String,Object> user_submitBundle = new HashMap<>();
    // private EditText ed_title,ed_text;
    private FirebaseFirestore fb_db=FirebaseFirestore.getInstance();
    private CollectionReference col_ref;
    private CollectionReference user_col_ref;
    private CollectionReference user_mypost_col_ref;
    private static int PROFILE_IMAGE=200;
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_list);

        allmsg= (ArrayList<Message>) getIntent().getSerializableExtra("alldata");

        imgButton = (ImageButton) findViewById(R.id.btn_smallprofile);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"on click pressed ");
                Intent intent_for_profile_page = new Intent(PostFetchScreen.this, Profile_Edit.class);
                startActivityForResult(intent_for_profile_page, PROFILE_IMAGE);
            }
        });

        //implement RecyclerView
        recyclerView=findViewById(R.id.recycler);
        et_post_text =(EditText) findViewById(R.id.et_post_text);
        et_post_title= (EditText) findViewById(R.id.et_post_title);
//        Toast.makeText(getApplicationContext(), Integer.toString(allmsg.size()), Toast.LENGTH_SHORT).show();
        final Post_Adapter adapter = new Post_Adapter(getApplication(),allmsg);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


//        Inplement fetching login info
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            GoogleSignInAccount acc =GoogleSignIn.getLastSignedInAccount(PostFetchScreen.this);

            if(acc!=null){
                String name=acc.getDisplayName();
                nick=acc.getGivenName();
                String fname=acc.getFamilyName();
                String email=acc.getEmail();
                owner_id=acc.getId();
            String postCollection="myposts_"+owner_id;
            Uri photo2=acc.getPhotoUrl();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(photo2);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap photo = BitmapFactory.decodeStream(imageStream);
            user_col_ref =fb_db.collection("Users");
            user_mypost_col_ref =fb_db.collection("Users").document(owner_id).collection(postCollection);

            user_submitBundle.put(user_Key[0],name);
            user_submitBundle.put(user_Key[1],nick);
            user_submitBundle.put(user_Key[2],fname);
            user_submitBundle.put(user_Key[3],email);
            user_submitBundle.put(user_Key[4],owner_id);
            user_submitBundle.put(user_Key[5],postCollection);
            user_submitBundle.put(user_Key[6],photo);
            user_col_ref.document(owner_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("user--upload", "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("user--upload", "User Details Exists!");
                    } else {
                        Log.d("user--upload", "Uploading User Data");
                        user_col_ref.document(owner_id). set(PostFetchScreen.this.user_submitBundle)
                                    .addOnSuccessListener(new OnSuccessListener< Void >() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "User Details Saved!",Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "ERROR" + e.toString(),Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });
                    }
                }
            });

        }

//        geocoder code
        geocoder = new Geocoder(this, Locale.getDefault());

//        location Request
        createLocationRequest();
        AskPermission();
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

//        Button for posting messages
//        Post message implementation
        btn_post=findViewById(R.id.btn_AddPost);
        btn_post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title=et_post_title.getText().toString();
                String description=et_post_text.getText().toString();

                String getText= et_post_text.getText().toString();
                String getTitle= et_post_title.getText().toString();
                if (myLocation != null && !getTitle.isEmpty() && !getText.isEmpty() ) {
                    //Double latitude=myLocation.getLatitude();
                    //Double longitude=myLocation.getLatitude();
//                    Toast.makeText(getApplicationContext(), "Latitude : " + myLocation.getLatitude() + " , Longitude : " + myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    String address="";
                    String city="";
                    state="";
                    String postalCode="";


                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        postalCode = addresses.get(0).getPostalCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    timestamp=new DateTime().getdatetime();
                    String msgId=state+"_"+timestamp+"_"+owner_id;
                    Message msg = new Message(latitude,longitude,title,description,"autogenId",owner_id,state,city,postalCode,address,msgId,nick);

                    allmsg.add(msg);
                    Map<String,Object> submitBundle = new HashMap<>();
                    submitBundle.put(Key[0],latitude);
                    submitBundle.put(Key[1],longitude);
                    submitBundle.put(Key[2],title);
                    submitBundle.put(Key[3],description);
                    submitBundle.put(Key[4],"autogenId");
                    submitBundle.put(Key[5],owner_id);
                    submitBundle.put(Key[6],state);
                    submitBundle.put(Key[7],city);
                    submitBundle.put(Key[8],postalCode);
                    submitBundle.put(Key[9],address);
                    submitBundle.put(Key[10],msgId);
                    submitBundle.put(Key[11],nick);



                    col_ref =fb_db.collection("AllPosts").document(state).collection(state+"_collection");
                    col_ref.document(state+"_"+timestamp+"_"+owner_id). set(submitBundle)
                            .addOnSuccessListener(new OnSuccessListener< Void >() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Successfully Posted!",Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                    et_post_text.setText("");
                                    et_post_title.setText("");

                                    //reference of post in user profile

                                    Map<String,String> msg_ref_list= new HashMap<String,String>();
                                    msg_ref_list.put("Message_Reference",PostFetchScreen.this.state+"_"+PostFetchScreen.this.timestamp+"_"+PostFetchScreen.this.owner_id);
                                    user_col_ref.document(owner_id).collection("MyPosts_"+PostFetchScreen.this.owner_id).document(PostFetchScreen.this.state+"_"+PostFetchScreen.this.timestamp+"_"+PostFetchScreen.this.owner_id).set(msg_ref_list)
                                            .addOnSuccessListener(new OnSuccessListener< Void >() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Reference Added!",Toast.LENGTH_SHORT).show();
//                                                    adapter.notifyDataSetChanged();
                                                }
                                             })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Reference Failed" + e.toString(),Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, e.toString());
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "ERROR" + e.toString(),Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                }
                else {
                    if (ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        AskPermission();
                    }
                    if(getTitle.isEmpty() || getText.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Title or Description should not be empty!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Enable location to post!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    //Implementation of location functions
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag,"I am here................in onstart");
        if (myGoogleApiClient != null) {
            myGoogleApiClient.connect();
            Log.d(Tag,"I am here...............google client not null");

            SharedPreferences shared =getSharedPreferences("myprefs", MODE_PRIVATE);
            String photo= shared.getString("imagePreferances","null");
            if(photo!="null")
            {
                final byte[] decodedByte= Base64.decode(photo.getBytes(),Base64.DEFAULT);
                imgButton.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0,decodedByte.length));
            }
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
                //if user denies location permissions
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
                Toast.makeText(getApplication(), "permission given", Toast.LENGTH_SHORT).show();
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
                        resolvable.startResolutionForResult(PostFetchScreen.this,
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
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Log.d(Tag,"here4......................location updated in start update "+Double.toString(latitude)+" "+Double.toString(longitude));
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }
    }

    protected void onResume() {
        super.onResume();
//        if (!checkGooglePlayServices()) {
//            Toast.makeText(getApplicationContext(), "could not get your current location ! Install google play services", Toast.LENGTH_SHORT).show();
//        }
        Log.d(Tag,"I am here................in onResume");
    }

    //Implement killing the app on pressing the back button

    @Override
    public void onBackPressed() {
        this.finishAffinity();
        super.onBackPressed();
    }

}

