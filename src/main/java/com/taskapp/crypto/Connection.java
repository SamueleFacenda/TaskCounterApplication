package com.taskapp.crypto;

/**
 * @author Samuele Facenda
 * tcp socket server class, the socket is passed in the constructor
 */

import com.taskapp.dataClasses.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.Timestamp;

public class Connection{
    private final Socket socket;
    private CommunicatioUtils cu;
    private static final String SERVER = "ServerSamu--", CLIENT = "ClientSamu--", ACK = "Recived";
    private SecretKey sessionKey;
    private IvParameterSpec sessionIV;

    public static Socket getSocket() throws IOException {
        //connect to samuele.ddns.net at port 9999
        return new Socket("samuele.ddns.net", 9999);
    }


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

    public void init() throws IOException {
        PersistencyManager.initialize();
        askConnection();
        if(readFromServer().equals("true"))
            reciveLastKey();
        readAck();
        sendEncryptedKey();
        readAck();
    }

    private void sendMetadata(){
        writeMessage(new SessionMetadata(
                PersistencyManager.isFirtTime(),
                PersistencyManager.getLastRSAKeyUpdate()
        ));
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

    public boolean readAck() {
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

    public boolean login(String user, String password){
        String json = JsonUtils.toJson(new Auth(user, password));
        sendEncrypted(json, CLIENT, "");
        try {
            String in = readFromServer();
            if(in.equals("NO")){
                System.out.println("Invalid username or password");
                return false;
            }
            if(in.startsWith("OK--")){
                in = in.substring(4);
                String token = AESUtils.decrypt(in, sessionKey, sessionIV);
                PersistencyManager.updateUser(user);
                PersistencyManager.updateAuthToken(token);
                System.out.println("Logged in as " + user);
                return true;
            }
        } catch (IOException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            System.err.println("Error in Connection login method  " + e.getMessage());
        }
        return false;
    }

    public boolean register(String user, String password){
        String json = JsonUtils.toJson(new Auth(user, password));
        sendEncrypted(json, CLIENT, "");
        try {
            String in = readFromServer();
            if(in.equals("alreadyExists"))
                System.out.println("User already exist");
            if(in.startsWith("OK"))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error in Connection login method  " + e.getMessage());
        }
        return false;
    }
    public void sendInt(int i){
        writeInt(i);
        readAck();
    }
    public boolean login(String user){
        String json = JsonUtils.toJson(new Auth(user, PersistencyManager.getAuthToken()));
        sendEncrypted(json, SERVER, "");
        try {
            String in = readFromServer();
            if(in.equals("NO")){
                System.out.println("Invalid username or token");
                return false;
            }
            if(in.equals("OK")){
                PersistencyManager.updateUser(user);
                System.out.println("Logged in as " + user);
                return true;
            }
            if(in.equals("EXPIRED")){
                System.out.println("Token expired");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error in Connection login method  " + e.getMessage());
        }
        return false;
    }

    private void sendBye(){
        cu.writeLine(CLIENT + "Bye");
    }

    public void bye() throws IOException {
        sendBye();
        readBye();
        socket.close();
    }

    public boolean sendLabel(String label, String comment){
        String json = JsonUtils.toJson(new Activity(PersistencyManager.getUser(), label, new Timestamp(System.currentTimeMillis()),null, comment));
        sendEncrypted(json, CLIENT, "");
        return readAck();
    }
}
