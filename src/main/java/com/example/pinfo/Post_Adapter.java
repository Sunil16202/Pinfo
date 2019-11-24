package com.example.pinfo;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//import android.annotation.NonNull;
//import android.recyclerview.widget.RecyclerView;
//import com.example.myapplication.R;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.Holder>{
    Context applicationContext;
    ArrayList<Message> post_list;

    public Post_Adapter(Application applicationContext, ArrayList<Message> post_list) {
        this.applicationContext=applicationContext;
        this.post_list=post_list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.recycler_ui,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Message dataclass = post_list.get(position);
        holder.holder_post_text.setText(dataclass.getPost_text());
        holder.holder_post_title.setText(dataclass.getPost_title());

//        //onclick Method
//        holder.holder_post_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(applicationContext,PostReplyList.class);
//                intent.putExtra("post",dataclass);
//                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                applicationContext.startActivity(intent);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        public TextView holder_post_text;
        public TextView holder_post_title;
        //this is post text
        public Holder(@NonNull View itemView) {
            super(itemView);
            holder_post_text=(TextView)itemView.findViewById(R.id.posttext);  //id of recycler_ui.xml file
            holder_post_title=(TextView)itemView.findViewById(R.id.posttitle);
            //clickable
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // item clicked
                    int i=getAdapterPosition();
                    Intent intent=new Intent(applicationContext,PostReplyList.class);
                    //    intent.addFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.putExtra("post",post_list.get(i));
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    applicationContext.startActivity(intent);

                    // Toast.makeText(applicationContext,"Working",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

