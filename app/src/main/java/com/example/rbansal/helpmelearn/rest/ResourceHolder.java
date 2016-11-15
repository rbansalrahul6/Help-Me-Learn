package com.example.rbansal.helpmelearn.rest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.rbansal.helpmelearn.R;

/**
 * Created by rbansal on 14/11/16.
 */
public class ResourceHolder extends RecyclerView.ViewHolder {
    public View mView;
     public ResourceHolder(View itemView) {
         super(itemView);
         mView = itemView;
     }
    public void setName(String name) {
        TextView resName = (TextView)mView.findViewById(R.id.res_link);
        resName.setText(name);
    }
    public void setDesc(String desc) {
        TextView resDesc = (TextView) mView.findViewById(R.id.res_desc);
        resDesc.setText(desc);
    }
    public void setVoteCount(int count) {
        TextView voteCount = (TextView) mView.findViewById(R.id.vote_count);
        voteCount.setText(Integer.toString(count));
    }
    public void setButton(Boolean test, final Context mContext) {
        Button voteBtn = (Button) mView.findViewById(R.id.vote_btn);
        String status;
        if(test)
            status = "VOTED";
        else
            status = "VOTE";
        voteBtn.setText(status);
       /* voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(mContext,"Button tested");
            }
        }); */
    }
}

