package com.example.rbansal.helpmelearn.rest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rbansal.helpmelearn.R;
import com.example.rbansal.helpmelearn.models.Topic;

import java.util.List;

/**
 * Created by rbansal on 10/7/16.
 */
public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.MyViewHolder> {
    private List<Topic> topicsList;

    public TopicsAdapter(List<Topic> topicsList) {
        this.topicsList = topicsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView topicName,description;
        public MyViewHolder(View view) {
            super(view);
            topicName = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.desc);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_list_row,parent,false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder,int position) {
        Topic topic = topicsList.get(position);
        holder.topicName.setText(topic.getTopicName());
        holder.description.setText(topic.getDescription());
    }
    @Override
    public int getItemCount() {
        return topicsList.size();
    }
}
