package com.example.pinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostReplyList extends AppCompatActivity {

    private ArrayList<Replies> replyList= new ArrayList<Replies>();
    RecyclerView recyclerView;
    EditText et_reply_text;
    Button btn_Addreply;
    TextView tv_title_reply;
    TextView tv_text_reply;
    Message message;
    String replyText;
    String TAG="PinfoReply";

    GoogleSignInClient mGoogleSignInClient;

    final Reply_Adapter adapter = new Reply_Adapter(getApplication(),replyList);


    //Firebase FireStore Variables
    private FirebaseFirestore fb_db=FirebaseFirestore.getInstance();
    private CollectionReference col_ref;
    private CollectionReference user_col_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_reply_list);
        message= (Message) getIntent().getSerializableExtra("post");
        Toast.makeText(this, "nick name :"+message.getNick(), Toast.LENGTH_SHORT).show();
        btn_Addreply=findViewById(R.id.btn_Addreply);
        recyclerView=findViewById(R.id.recycler_reply);
        et_reply_text =(EditText) findViewById(R.id.et_reply_text);
        tv_text_reply=findViewById(R.id.tv_text_reply);
        tv_title_reply=findViewById(R.id.tv_title_reply);
        tv_text_reply.setText(message.getPost_text());
        tv_title_reply.setText(message.getPost_title());


        fetchMyThread();

        Log.w("Pinfo","afterReply in onCreate"+replyList.size());


        //recyclerView here i need replylist with repolies before it
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);
        btn_Addreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyText=et_reply_text.getText().toString();
                if(replyText.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Type Something to reply!", Toast.LENGTH_SHORT).show();
                }
                else{
                    postMsg();
                    adapter.notifyDataSetChanged();
                }
                //when data posted and reply list updated the the next line


            }
        });


    }
    public void postMsg(){

//        fetchUserSigninDetails();
//        CreateBundletoPost();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acc =GoogleSignIn.getLastSignedInAccount(PostReplyList.this);

        String nick,m_owner_id;
        String m_timestamp=new DateTime().getdatetime();


        if(acc!=null) {
            nick = acc.getGivenName();
            m_owner_id = acc.getId();
        }else{m_owner_id="";nick="";}
        final String owner_id=m_owner_id;
        final String timestamp=m_timestamp;

        String reply_id=timestamp+"_"+owner_id;

        Map<String,String> submitBundle = new HashMap<>();
        String[] Key={"parent_post_id","parent_owner_id","owner_id","state","parent_nick","nick","reply_text","reply_id"};
        submitBundle.put(Key[0],message.getmsg_id());
        submitBundle.put(Key[1],message.getOwner_id());
        submitBundle.put(Key[2],owner_id);
        submitBundle.put(Key[3],message.getState());
        submitBundle.put(Key[4],message.getNick());
        submitBundle.put(Key[5],nick);
        submitBundle.put(Key[6],replyText);
        submitBundle.put(Key[7],reply_id);

        Replies reply=new Replies(message.getmsg_id(),message.getOwner_id(),owner_id,message.getState(),message.getNick(),nick,replyText,reply_id);
        replyList.add(reply);
        user_col_ref =fb_db.collection("Users");
        col_ref =fb_db.collection("AllPosts").document(message.getState()).collection(message.getState()+"_collection").document(message.getmsg_id()).collection("Replies");
        col_ref.document(timestamp+"_"+owner_id). set(submitBundle)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Posted!",Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        et_reply_text.setText("");

                        //reference of post in user profile

                        Map<String,String> msg_ref_list= new HashMap<String,String>();
                        msg_ref_list.put("Message_Reference",message.getmsg_id());
                        user_col_ref.document(owner_id).collection("MyPosts_"+owner_id).document(message.getmsg_id()).set(msg_ref_list)
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
    public void fetchMyThread(){
        col_ref =fb_db.collection("AllPosts").document(message.getState()).collection(message.getState()+"_collection").document(message.getmsg_id()).collection("Replies");
        col_ref.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    Replies reply= new Replies();
                    @Override
                    public void onSuccess(QuerySnapshot docSnap) {
//                        replyList.clear();
                        for(QueryDocumentSnapshot ds : docSnap){
//                                        String id=ds.getId();
                            reply= ds.toObject(Replies.class);
                            replyList.add(reply);

                        }
                        Log.w("Pinfo","afterReply "+replyList.size());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostReplyList.this, "ERROR" + e.toString(),Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, e.toString());
                    }

                });

        return;
    }

}
