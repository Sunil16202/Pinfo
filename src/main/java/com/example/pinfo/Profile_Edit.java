package com.example.pinfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;


public class  Profile_Edit extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "ABC";
    Uri selectedImage;
    ImageView iv_profileImage;
    ImageButton btn_ProfileImage;
    Button btn_signout;
    GoogleSignInClient mGoogleSignInClient;
    Uri mUri;
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //
        setContentView(R.layout.profile_edit_page);
        Log.d(TAG,"on create");
        btn_ProfileImage = (ImageButton) findViewById(R.id.btn_gallery);
        iv_profileImage = (ImageView) findViewById(R.id.btn_profile);
        btn_signout=(Button) findViewById(R.id.btn_signout);
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.btn_signout:
                        signOut();
                        break;
                }
            }
        });
        btn_ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(Profile_Edit.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"permission not granted");

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Profile_Edit.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Log.d(TAG,"i m here");
                        ActivityCompat.requestPermissions(Profile_Edit.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(Profile_Edit.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        Log.d(TAG,"hiiiii");

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    Intent intent_for_gallary = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent_for_gallary, RESULT_LOAD_IMAGE);
                    // Permission has already been granted
                }
            }
        });
        SharedPreferences shared = getSharedPreferences("myprefs", MODE_PRIVATE);
        String photo = shared.getString("imagePreferances", "null");
        Log.d(TAG, photo);
        if (photo != "null") {
            final byte[] decodedByte = Base64.decode(photo.getBytes(), Base64.DEFAULT);
            iv_profileImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
        }

        if (savedInstanceState != null) {
            mUri = savedInstanceState.getParcelable("uri");
            iv_profileImage.setImageURI(mUri);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUri = selectedImage;
        Log.d(TAG, "onActivityResult: onActivity");
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            Log.d(TAG, selectedImage.toString());
            String[] filePathColumn = {Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.d(TAG, picturePath);
            cursor.close();
            ImageView iv_profileImage = (ImageView) findViewById(R.id.btn_profile);
            iv_profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //RG






            //
            Log.d(TAG, "onActivity");
            Button btn1 = findViewById(R.id.btn_save);
            btn1.setText("SAVE");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d(TAG, "savemyimage()");
                        saveMyImage();
                    } catch (Exception e) {
                        Log.d(TAG, "exception");
                    }
                }
            });
        } else {
            Toast.makeText(getBaseContext(), "failed to upload!\n try again", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: onActivity");
        }


    }

    void saveMyImage() throws IOException {

        try {
            //code image to string
            iv_profileImage.setDrawingCacheEnabled(true);
            Bitmap bitmap_profileImage = Bitmap.createBitmap(iv_profileImage.getDrawingCache());
            iv_profileImage.setDrawingCacheEnabled(false);

            //bitmap is encoded in base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap_profileImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
            final byte[] b = baos.toByteArray();
            final String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            Log.d(TAG, imageEncoded);
            Log.d(TAG, "hiii");
            // storing in shared preferences
            SharedPreferences preferences = getSharedPreferences("myprefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("imagePreferances", imageEncoded);
            editor.commit();


        } catch (Exception e) {
            Log.d(TAG, "on exception");
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroyed");
        super.onDestroy();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent_for_gallary = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent_for_gallary, RESULT_LOAD_IMAGE);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this,"permission not granted",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUri != null) {
            outState.putParcelable("uri", mUri);
        }
    }

    //Last edit RG
    private void signOut()
    {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent=new Intent(Profile_Edit.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

}

