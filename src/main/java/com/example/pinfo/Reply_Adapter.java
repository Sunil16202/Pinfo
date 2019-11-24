package com.example.pinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Reply_Adapter extends RecyclerView.Adapter<Reply_Adapter.Holder> {
    Context applicationContext;
    ArrayList<Replies> reply_list;


    public Reply_Adapter(Context applicationContext, ArrayList<Replies> reply_list) {
        this.applicationContext = applicationContext;
        this.reply_list = reply_list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.reply_list_layout,parent,false);
        Reply_Adapter.Holder holder=new Reply_Adapter.Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Replies dataclass = reply_list.get(position);
        holder.holder_reply_text.setText(dataclass.getReply_text());
        holder.holder_reply_name.setText(dataclass.getNick());
    }

    @Override
    public int getItemCount() {
        return reply_list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        public TextView holder_reply_text;
        public TextView holder_reply_name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            holder_reply_text=(TextView)itemView.findViewById(R.id.tv_reply_text);  //id of recycler_ui.xml file
            holder_reply_name=(TextView)itemView.findViewById(R.id.tv_reply_name);
        }
        //this is post text

    }
}
