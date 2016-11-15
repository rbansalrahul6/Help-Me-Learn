package com.example.rbansal.helpmelearn.utils;

import android.content.Context;
import android.widget.Toast;

import java.text.DecimalFormat;

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
    public static String miniNumber(double number) {
        if (number > 10000000) {
            return new DecimalFormat("0m").format(number / 1000000);
        } else if (number >= 1000000) {
            return new DecimalFormat("0.#m").format(number / 1000000);
        } else if (number > 10000) {
            return new DecimalFormat("0k").format(number / 1000);
        } else if (number >= 1000) {
            return new DecimalFormat("0.#k").format(number / 1000);
        } else {
            return new DecimalFormat("#,###").format(number);
        }
    }
}
