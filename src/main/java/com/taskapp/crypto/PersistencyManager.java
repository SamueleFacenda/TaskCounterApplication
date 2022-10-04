package com.taskapp.crypto;

import com.taskapp.dataClasses.Activity;
import com.taskapp.dataClasses.Lbl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * classe che si occupa del salvataggio dei dati sul dispositivo, in un file json, tutto ciò che deve rimanere
 * è gestito da questa classe
 */
public class PersistencyManager {
    private static File f;
    private static JSONObject obj;

    /**
     * va chiamato all'avvio dell'applicazione, se il file non esiste lo crea, altrimenti lo legge
     */
    public static void initialize(){
        f = new File("persistency.json");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //legge, crea una string a e converte il json in oggetto dal file
        try(Scanner in = new Scanner(f)) {
            StringBuilder sb = new StringBuilder();
            while(in.hasNextLine()){
                sb.append(in.nextLine());
            }
            if(sb.length() == 0)
                obj = new JSONObject();
            else
                obj = new JSONObject(sb.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * scrive i cambiamenti sul file
     */
    private static void update(){
        try(FileWriter fw = new FileWriter(f)){
            fw.write(obj.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    *   i seguenti metodi sono tutti uguali, semplicemente scrivono e leggono un dato dal file
     */

    public static void updateRsaKey(String key){
        obj.put("rsaKey",key);
        obj.put("lastRSAKeyUpdate",new Timestamp(System.currentTimeMillis()).toString());
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
            System.out.println("no rsa key found, persistency manager");
            throw new RuntimeException(e);
        }
    }
    public static String getUser(){
        try{
            return obj.getString("user");
        }catch(JSONException e){
            System.out.println("no user found, persistency manager");
            e.printStackTrace();
            return "";
        }
    }
    public static void updateUser(String user){
        obj.put("user",user);
        update();
    }
    public static String getAuthToken(){
        try{
            return obj.getString("authToken");
        }catch (JSONException e){
            System.out.println("no auth token found, persistency manager");
            return "";
        }
    }
    public static void updateAuthToken(String authToken){
        obj.put("authToken",authToken);
        update();
    }

    /**
     * controlla se è il primo avvio dell'applicazione
     * @return true se è il primo avvio, false altrimenti
     */
    public static boolean isFirstTime(){
        return obj.length() == 0;
    }
    public static Timestamp getLastRSAKeyUpdate(){
        try{
            return Timestamp.valueOf(obj.getString("lastRSAKeyUpdate"));
        }catch (JSONException e){
            System.err.println(e.getMessage());
            return new Timestamp(0);
        }
    }

    /**
     * se il server non è raggiungibile allora salvo le attività in locale
     * @param label label dell'attività
     * @param comment commento dell'attività
     */
    public static void addActivity(String label, String comment){
        JSONArray jo;
        try{
            jo = obj.getJSONArray("activity");
        }catch(JSONException e){
            jo = new JSONArray();
        }
        JSONObject niu = new JSONObject();
        niu.put("user", "");
        niu.put("label", label);
        niu.put("comment", comment);
        niu.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
        jo.put(niu);
        obj.put("activity", jo);
        update();
    }

    public static Activity[] getActivity(){
        JSONArray jo;
        try{
            jo = obj.getJSONArray("activity");
        }catch(JSONException e){
            jo = new JSONArray();
        }
        Activity[] ret = new Activity[jo.length()];
        for(int i = 0; i < jo.length(); i++){
            JSONObject o = jo.getJSONObject(i);
            ret[i] = new Activity(o.getString("user"), o.getString("label"), new Timestamp(o.getLong("timestamp")), null, o.getString("comment"));
        }
        return ret;
    }

    public static boolean hasActivity(){
        return obj.get("activity") != null && obj.getJSONArray("activity").length() > 0;
    }

    /**
     * controlla se è mai stato fatto un login con successo
     */
    public static boolean isFirstLogin(){
        return !obj.has("user");
    }

    public static void clearActivity() {
        obj.put("activity", new JSONArray());
        update();
    }

    public static void addLabel(Lbl label) {
        JSONObject jo;
        try{
            jo = obj.getJSONObject("labels");
        }catch (JSONException e){
            obj.put("labels", new JSONObject());
            jo = obj.getJSONObject("labels");
        }
        jo.put(label.getLabel(), label.getCount());
        obj.put("labels", jo);
        update();
    }
    public static Lbl[] getLabel(){
        try{
            JSONObject jo = obj.getJSONObject("labels");
            Lbl[] ret = new Lbl[jo.length()];
            int i = 0;
            for(String key : jo.keySet()){
                ret[i] = new Lbl(key, jo.getInt(key));
                i++;
            }
            return ret;
        }catch (JSONException e){
            obj.put("labels", new JSONObject());
            return new Lbl[0];
        }

    }

}
