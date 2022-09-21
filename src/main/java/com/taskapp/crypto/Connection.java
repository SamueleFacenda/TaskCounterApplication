package com.taskapp.crypto;

/**
 * @author Samuele Facenda
 * tcp socket server class, the socket is passed in the constructor
 */

import com.taskapp.dataClasses.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

public class Connection{
    private final Socket socket;
    private CommunicatioUtils cu;
    private static final String SERVER = "ServerSamu--", CLIENT = "ClientSamu--", ACK = "Recived";
    private SecretKey sessionKey;
    private IvParameterSpec sessionIV;


    public Connection(Socket socket) {
        this.socket = socket;
        PersistencyManager.initialize();
        try {
            cu = new CommunicatioUtils(
                    new BufferedReader(new InputStreamReader(socket.getInputStream())),
                    new PrintWriter(socket.getOutputStream(), true)
            );
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("Error in Connection constructor");
        }
    }

    private boolean isLocal(){
        return !System.getProperty("os.name").startsWith("Windows");
    }


    private String readFromServer() throws IOException {
        String line = cu.readLine();
        if(line.startsWith(Connection.SERVER)){
            return line.substring(Connection.SERVER.length());
        }else
            throw new IOException("Invalid input from " + Connection.SERVER);
    }

    private boolean readAck() {
        try{
            String in = readFromServer();
            return in.equals(ACK);
        }catch(IOException e){
            System.out.println("Error in Connection readAck method");
            return false;
        }
    }


    private void writeInt(int i) {
        cu.writeLine(CLIENT + i);
    }

    private void askConnection(){
        cu.writeLine(CLIENT + "AskingForConnection");
    }

    private <T> void writeMessage(T message) {
        cu.writeLine(CLIENT + JsonUtils.toJson(message));
    }


    private void sendEncrypted(String message, String prefix, String suffix) {
        try {
            String encrypted = AESUtils.encrypt(message, sessionKey, sessionIV);
            cu.writeLine(prefix + encrypted + suffix);
        } catch (Exception e) {
            System.err.println("Error in Connection sendEncrypted method");
        }
    }

    private void reciveLastKey(){
        try{
            String in = readFromServer();
            if(in.startsWith("RSAKEY:") && in.endsWith("--ENDKEY")){
                in = in.substring(7, in.length() - 8);
                PersistencyManager.updateRsaKey(in);
            }
        }catch(IOException e){
            System.err.println("Error in Connection reciveLastKey method");
        }catch(Exception e){
            System.err.println("Error in Connection reciveLastKey method, invalid key");
        }
    }
    private void sendEncryptedKey(){
        try{
            sessionIV = AESUtils.generateIv();
            sessionKey = AESUtils.generateKey(1024);
            String key = AESUtils.toBase64(sessionKey);
            String iv = AESUtils.byteArrayToString(sessionIV.getIV());
            String json = JsonUtils.toJson(new AesKey(key, iv));
            json = RSAUtils.toBase64(RSAUtils.encrypt(json, PersistencyManager.getRsaKey()));
            cu.writeLine(SERVER + json);
        }catch(Exception e){
            System.err.println("Error in Connection sendEncryptedKey method");
            e.printStackTrace();
        }
    }

    private boolean readBye(){
        try{
            String in = readFromServer();
            return in.equals("Bye");
        }catch(IOException e){
            System.out.println("Error in Connection readBye method");
            return false;
        }
    }

    private void sendBye(){
        cu.writeLine(CLIENT + "Bye");
    }

}
