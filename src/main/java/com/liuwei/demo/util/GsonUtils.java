package com.liuwei.demo.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtils {
    //    private static Gson gson = new Gson();
    private static Gson gson = new GsonBuilder().
            registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == src.intValue())
                        return new JsonPrimitive(src.intValue());
                    return new JsonPrimitive(src);
                }
            }).create();

    public static Map<String, Object> GsonToMap(String data) {
        Map<String, Object> map = gson.fromJson(data, new TypeToken<Map<String, Object>>() {
        }.getType());
        return map;
    }

    public static String toJson(Object obj){
        return gson.toJson(obj);
    }

    public static Integer Double2Integer(Double number){
        return new Integer(number.intValue());
    }
}
