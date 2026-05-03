package com.example.myapplication;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonUtils {
    public static <T> List<T> loadListFromRaw(Context context, int rawId, Class<T> clazz) {
        try {
            InputStream is = context.getResources().openRawResource(rawId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            
            Gson gson = new Gson();
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
