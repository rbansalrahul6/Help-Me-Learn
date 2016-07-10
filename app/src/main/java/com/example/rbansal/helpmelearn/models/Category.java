package com.example.rbansal.helpmelearn.models;

import java.util.HashMap;

/**
 * Created by rbansal on 8/7/16.
 */
public class Category {
    String name;
    HashMap<String,String> topics;

    public Category() {
    }

    public Category(String name, HashMap<String, String> topics) {
        this.name = name;
        this.topics = topics;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getTopics() {
        return topics;
    }
}
