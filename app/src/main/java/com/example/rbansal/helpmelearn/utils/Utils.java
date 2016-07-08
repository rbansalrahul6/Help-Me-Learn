package com.example.rbansal.helpmelearn.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by rbansal on 5/7/16.
 */
public class Utils {
    public static void showToast(Context context,String message) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    public static String formatData(String input) {
        input = input.trim();
        input = input.toLowerCase();
        return input;
    }
}
