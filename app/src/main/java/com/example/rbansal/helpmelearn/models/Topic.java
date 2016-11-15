package com.example.rbansal.helpmelearn.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rbansal on 4/7/16.
 */
public class Topic implements Parcelable {
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
    //constructor to use when re-constructing objects from parcel
    public Topic(Parcel in) {
        readFromParcel(in);
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

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest,int flags) {
        dest.writeString(category);
        dest.writeString(topicName);
        dest.writeString(description);
    }
    private void readFromParcel(Parcel in) {
        category = in.readString();
        topicName = in.readString();
        description = in.readString();
    }
    //creator field
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Topic createFromParcel(Parcel in) {
                    return new Topic(in);
                }
                public Topic[] newArray(int size) {
                    return new Topic[size];
                }
            };
}

