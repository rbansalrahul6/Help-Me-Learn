package com.example.rbansal.helpmelearn.models;

/**
 * Created by rbansal on 4/7/16.
 */
public class Topic {
    String category;
    String topicName;   //must be unique
    String description;

    public Topic() {
    }

    public Topic(String category, String topicName, String description) {
        this.category = category;
        this.topicName = topicName;
        this.description = description;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }
}

