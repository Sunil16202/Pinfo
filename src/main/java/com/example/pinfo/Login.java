package com.example.pinfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN=123;
    private static final String postList2="postList2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInButton= findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //Step 3: create gso(googlesignin option object)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //Step 4: set on click listener and call signin()(userdefined) function
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { signInWithGoogle(); }
        });


    }

    // Step 4.1: Step 4 calls signInWithGoogle() and it calls startActivityForResult()
    private void signInWithGoogle(){
        Intent signInGoogleIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInGoogleIntent,RC_SIGN_IN);
    }

    // Step 4.2: Step 4.1 calls  startActivityForResult() and result is captured and processed by onActivityResult() after startActivityForResult() is completed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            //So if RC(requestCode) received at onActivityResult is "123" which RC for sign in
            //then we now know  signIn attempt has been made and we should create a task which is gonna gather all details of the user
            Task<GoogleSignInAccount> gatherUserData= GoogleSignIn.getSignedInAccountFromIntent(data);
            //call checkSignInStatus to check if signIn was successful!
            checkSignInStatus(gatherUserData);
        }
    }

    // Step 4.3: Step 4.2 calls checkSignInStatus to check if signIn was successful
    private void checkSignInStatus(Task<GoogleSignInAccount> gatherED_UserData){
        try{
            //Try to collect user data, if failed exception will be caught
            GoogleSignInAccount acc_details=gatherED_UserData.getResult(ApiException.class);
            //Make a toast for successful login and proceed to next activity!(KUDOS!)
            Toast.makeText(this,"Success",Toast.LENGTH_LONG).show();

            ArrayList<Message> allmsg= new ArrayList<Message>();
            allmsg= (ArrayList<Message>) getIntent().getSerializableExtra("alldata");

            Intent i1= new Intent(this,PostFetchScreen.class);
            i1.putExtra("alldata",allmsg);
            startActivity(i1);
//            startActivity(new Intent(this,PostFetchScreen.class));
        }
        //Catch the exception as failed to fetch user data as SignIn was not successful :(
        catch(ApiException e){
            Log.w("SignIn", "signInWithCredential Status:Failure FailCode:"+ e.getStatusCode());
            Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show();
        }
    }
    //Step 5: Inside onStart() check if previously signed in with google account, if true proceed to next activity
    @Override
    protected void onStart() {
        GoogleSignInAccount acc =GoogleSignIn.getLastSignedInAccount(this);
        if(acc!=null){
            ArrayList<Message> allmsg= new ArrayList<Message>();
            allmsg= (ArrayList<Message>) getIntent().getSerializableExtra("alldata");

            Intent i1= new Intent(this,PostFetchScreen.class);
            i1.putExtra("alldata",allmsg);
            startActivity(i1);
        }
        super.onStart();
    }



}
