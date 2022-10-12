package com.taskapp.crypto;

import com.taskapp.dataClasses.*;
import com.taskapp.dataClasses.Auth;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonUtils {
    public static Auth getAuth(String in){
        JSONObject obj = new JSONObject(in);
        return new Auth(obj.getString("user"),obj.getString("psw"));
    }
    public static String getAuth(Auth a){
        Map<String,String> map = new LinkedHashMap<>();
        map.put("user",a.user());
        map.put("psw",a.psw());
        return new JSONObject(map).toString();
    }
    public static Activity getActivity(String in){
        JSONObject obj = new JSONObject(in);
        if(obj.has("ts")){
            return new Activity(
                    obj.getString("user"),
                    obj.getString("label"),
                    Timestamp.valueOf(obj.getString("ts")),
                    obj.getString("file"),
                    obj.getString("comment")
            );
        }else{
            return new Activity(
                    obj.getString("user"),
                    obj.getString("label"),
                    null,
                    null,
                    obj.getString("comment")
            );
        }
    }
    public static String getActivity(Activity a){
        Map<String,String> map = new LinkedHashMap<>();
        map.put("user",a.user());
        map.put("label",a.label());
        map.put("ts",a.ts().toString());
        map.put("file","");
        map.put("comment",a.comment());
        return new JSONObject(map).toString();
    }

    public static String getSessioneMetadata(SessionMetadata in){
        Map<String,String> map = new LinkedHashMap<>();
        map.put("isNew",String.valueOf(in.isNew()));
        map.put("rsaTimestamp",in.rsaTimestamp().toString());
        return new JSONObject(map).toString();
    }

    public static SessionMetadata getSessionMetadata(String in){
        JSONObject obj = new JSONObject(in);
        return new SessionMetadata(
                Boolean.parseBoolean(obj.getString("isNew")),
                Timestamp.valueOf(obj.getString("rsaTimestamp"))
        );
    }

    public static AesKey getAesKey(String in){
        JSONObject obj = new JSONObject(in);
        return new AesKey(obj.getString("key"),obj.getString("iv"));
    }

    public static String getAesKey(AesKey a){
        Map<String,String> map = new LinkedHashMap<>();
        map.put("key",a.key());
        map.put("iv",a.iv());
        return new JSONObject(map).toString();
    }

    public static <T> String toJson(T in){
        /*
        return switch(in){
            case Auth a -> getAuth(a);
            case Activity a -> getActivity(a);
            case AesKey a -> getAesKey(a);
            case SessionMetadata a -> getSessioneMetadata(a);
            default -> null;
        };

         */
        if(in instanceof Auth){
            return getAuth((Auth) in);
        }else if(in instanceof Activity){
            return getActivity((Activity) in);
        }else if(in instanceof AesKey){
            return getAesKey((AesKey) in);
        }else if(in instanceof SessionMetadata){
            return getSessioneMetadata((SessionMetadata) in);
        }else{
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String in, Class<T> clazz){
        /*
        return (T) switch(clazz.getSimpleName()){
            case "Auth" -> getAuth(in);
            case "Activity" -> getActivity(in);
            case "AesKey" -> getAesKey(in);
            case "SessionMetadata" -> getSessionMetadata(in);
            default -> null;
        };
         */
        if(clazz.getSimpleName().equals("Auth")){
            return (T) getAuth(in);
        }else if(clazz.getSimpleName().equals("Activity")){
            return (T) getActivity(in);
        }else if(clazz.getSimpleName().equals("AesKey")){
            return (T) getAesKey(in);
        }else if(clazz.getSimpleName().equals("SessionMetadata")){
            return (T) getSessionMetadata(in);
        }else{
            return null;
        }
    }

}
