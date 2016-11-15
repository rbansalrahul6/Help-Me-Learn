package com.example.rbansal.helpmelearn.models;

import java.util.HashMap;

/**
 * Created by rbansal on 4/7/16.
 */
public class Resource {
    String resId;   //must be unique  (primary key)
    String name;
    String topic;      //must refer to a valid topic (foreign key)
    String type;
    String reference;
    String description;
    String link;
    String addedBy;      //must refer to a valid user (foreign key)
    String dateAdded;
    int votes;
    HashMap<String,Boolean> voted;
    public Resource() {}

    public Resource(String name,String description,HashMap<String,Boolean> voted) {
        this.name = name;
        this.description = description;
        this.voted = voted;
    }
    public Resource(String name,String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<String, Boolean> getVoted() {
        return voted;
    }

    public String getResId() {
        return resId;
    }

    public int getVotes() {
        return votes;
    }
}
