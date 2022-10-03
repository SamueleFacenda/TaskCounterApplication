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
            connection.readAck();
            System.out.println("Sending username and password");
            boolean out = connection.login(username, password, true);
            quit(connection);
            return out;
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
            connection.readAck();
            boolean out = connection.register(username, password);
            quit(connection);
            return out;
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
            connection.readAck();
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
        System.out.println("Login");
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            if(PersistencyManager.isFirstLogin()){
                quit(connection);
                return false;
            }
            connection.sendInt(1);
            connection.readAck();
            boolean out = connection.login(PersistencyManager.getUser(), PersistencyManager.getAuthToken(), false);
            connection.readAck();
            connection.bye();
            System.out.println("Login: " + out);
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
                connection.readAck();
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

    public static boolean checkServerUp(){
        System.out.println("Check server up");
        try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            connection.sendInt(3);
            connection.readAck();
            if(connection.bye())
                System.out.println("Bye from server correct");
            else
                System.out.println("Bye from server incorrect");
            System.out.println("Check server up: true");
            return true;
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
            System.out.println("Check server up: false");
            System.out.println(e.getMessage());
            return false;
        }
    }
    public static boolean checkTokenValidility(){
        if(PersistencyManager.isFirstLogin()) {
            System.out.println("Check token validility: false");
            return false;
        }try{
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            connection.sendInt(1);
            connection.readAck();
            boolean out = connection.login(PersistencyManager.getUser(), PersistencyManager.getAuthToken(), false);
            quit(connection);
            System.out.println("Check token validility: " + out);
            return out;
        }catch(IOException e){
            return false;
        }
    }

    private static void quit(Connection c){
        c.sendInt(3);
        c.readAck();
        c.bye();
    }
}
