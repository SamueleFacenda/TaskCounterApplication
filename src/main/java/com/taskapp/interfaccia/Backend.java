package com.taskapp.interfaccia;

import com.taskapp.crypto.Connection;
import com.taskapp.crypto.PersistencyManager;
import com.taskapp.dataClasses.Activity;

import java.io.IOError;
import java.io.IOException;
import java.net.Socket;

public class Backend {
    private static String last_log;

    public static boolean login(String username, String password) {
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            connection.sendInt(1);
            if (connection.login(username, password))
                return connection.readAck() && connection.bye();
            connection.bye();
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
        }
        return false;
    }
    public static boolean register(String username, String password) {
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            connection.sendInt(0);
            if (connection.register(username, password))
                return connection.readAck() && connection.bye();
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
        }
        return false;
    }

    public static boolean load(String label, String comment){
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            if(!login())
                return false;
            connection.sendInt(2);
            connection.sendLabel(label, comment);
            connection.readAck();
            connection.bye();
            return true;
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
            PersistencyManager.addActivity(label, comment);
            return true;
        }
    }

    private static boolean login(){
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            connection.sendInt(1);
            boolean out = connection.login(PersistencyManager.getUser(), PersistencyManager.getAuthToken());
            connection.readAck();
            connection.bye();
            return out;
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
            return false;
        }
    }

    public static boolean tryUpdateActivity(){
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            if(!login())
                return false;
            for(Activity a: PersistencyManager.getActivity()){
                connection.sendInt(2);
                connection.sendLabel(a.label(), a.comment(), a.ts());
                connection.readAck();
            }
            connection.bye();
            PersistencyManager.clearActivity();
            return true;
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
            return true;
        }
    }
}
