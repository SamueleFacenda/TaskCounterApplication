package com.taskapp.interfaccia;

import com.taskapp.crypto.Connection;
import com.taskapp.crypto.PersistencyManager;

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
                return connection.readAck();
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
                return connection.readAck();
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
        }
        return false;
    }

    public static void load(String label, String comment){
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            connection.sendInt(2);
            connection.sendLabel(label, comment);
            connection.readAck();
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
            PersistencyManager.addActivity(label, comment);
        }
    }
}
