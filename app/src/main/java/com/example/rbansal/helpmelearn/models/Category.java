package com.example.rbansal.helpmelearn.models;

import java.util.List;

/**
 * Created by rbansal on 8/7/16.
 */
public class Category {
    String name;
    List<test> topics;

    public Category() {
    }

    public Category(String name, List<test> topics) {
        this.name = name;
        this.topics = topics;
    }

    public String getName() {
        return name;
    }

    public List<test> getTopics() {
        return topics;
    }
}
