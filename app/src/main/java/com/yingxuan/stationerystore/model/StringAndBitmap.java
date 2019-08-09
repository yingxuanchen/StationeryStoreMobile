package com.yingxuan.stationerystore.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class StringAndBitmap {

    public static Bitmap stringToBitmap(String string){
        if(string!=null){
            byte[] bytes= Base64.decode(string,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bitmap;
        }
        else {
            return null;
        }
    }

    public static String bitmapToString(Bitmap bitmap){
        if(bitmap!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            String string=Base64.encodeToString(bytes,Base64.DEFAULT);
            return string;
        }
        else{
            return "";
        }
    }

}
