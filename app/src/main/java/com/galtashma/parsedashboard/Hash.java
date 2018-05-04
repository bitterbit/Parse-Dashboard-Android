package com.galtashma.parsedashboard;

import android.util.Log;

import java.security.MessageDigest;

public class Hash {
    private static String salt = "1m4bqk";
    public static String sha1(String value){
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update((value + salt).getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes)
            {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            String result = buffer.toString();
            Log.d(Const.TAG, "Hash result " + result);

            return result;
        }
        catch (Exception ignored)
        {
            ignored.printStackTrace();
            return null;
        }
    }
}
