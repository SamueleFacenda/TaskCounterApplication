package com.taskapp.crypto;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class PersistencyManager {
    private static File f;
    private static JSONObject obj;
    public static void initialize(){
        f = new File("persistency.json");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try(Scanner in = new Scanner(f)) {
            StringBuilder sb = new StringBuilder();
            while(in.hasNextLine()){
                sb.append(in.nextLine());
            }
            obj = new JSONObject(sb.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static void update(){
        try(FileWriter fw = new FileWriter(f)){
            fw.write(obj.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void put(String key, String value){
        obj.put(key,value);
        update();
    }
    public static void updateRsaKey(String key){
        obj.put("rsaKey",key);
        update();
    }
    public static void updateRsaKey(PublicKey key){
        obj.put("rsaKey",RSAUtils.toBase64(key));
        update();
    }
    public static PublicKey getRsaKey(){
        try {
            return RSAUtils.fromBase64Public(obj.getString("rsaKey"));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getUser(){
        return obj.getString("user");
    }
    public static void updateUser(String user){
        obj.put("user",user);
        update();
    }
    public static String getAuthToken(){
        return obj.getString("authToken");
    }
    public static void updateAuthToken(String authToken){
        obj.put("authToken",authToken);
        update();
    }

}
