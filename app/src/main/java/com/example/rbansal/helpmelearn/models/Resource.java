package com.example.rbansal.helpmelearn.models;

/**
 * Created by rbansal on 4/7/16.
 */
public class Resource {
    String resId;   //must be unique  (primary key)
    String topic;      //must refer to a valid topic (foreign key)
    String type;
    String reference;
    String description;
    String addedBy;      //must refer to a valid user (foreign key)
    String dateAdded;
    int votes;
}
