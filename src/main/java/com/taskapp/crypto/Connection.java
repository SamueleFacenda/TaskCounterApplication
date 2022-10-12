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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.sql.Timestamp;
import java.util.function.Function;

public class Connection{
    private final Socket socket;
    private CommunicatioUtils cu;
    private static final String SERVER = "ServerSamu--", CLIENT = "ClientSamu--", ACK = "Recived";
    private SecretKey sessionKey;
    private IvParameterSpec sessionIV;
    public static final int TIMEOUT = 2000;// 10000; //1500

    public static Socket getSocket() throws IOException {
        Socket out = new Socket();
        //connect to samuele.ddns.net at port 9999 with timeout
        out.connect(new InetSocketAddress("samuele.ddns.net",9999),TIMEOUT);
        out.setSoTimeout(TIMEOUT);
        return out;
    }


    public Connection(Socket socket) {
        this.socket = socket;
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
        askConnection();
        if(connectionNotAccepted())
            throw new IOException("Connection not accepted");
        sendMetadata();
        if(readFromServer().equals("true"))
            reciveLastKey();
        readAck();
        sendEncryptedKey();
        readAck();
    }

    private boolean connectionNotAccepted() throws IOException {
        return !readFromServer().equals("ConnectionAccepted");
    }

    private void sendMetadata(){
        writeMessage(new SessionMetadata(
                PersistencyManager.isFirstTime(),
                PersistencyManager.getLastRSAKeyUpdate()
        ));
    }


    private String readFromServer() throws IOException {
        String line = cu.readLine();
        if(line.startsWith(Connection.SERVER)){
            return line.substring(Connection.SERVER.length());
        }else
            throw new IOException("Invalid input from " + Connection.SERVER + " : " + line);
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


    private void sendEncrypted(String message, String prefix) throws GeneralSecurityException {
        String encrypted = AESUtils.encrypt(message, sessionKey, sessionIV);
        cu.writeLine(prefix + encrypted);
    }

    private void reciveLastKey(){
        try{
            String in = readFromServer();
            if(in.startsWith("RSAKEY:") && in.endsWith("--ENDKEY")){
                in = in.substring(7, in.length() - 8);
                PersistencyManager.updateRsaKey(in);
            }
            System.out.println("Recived RSA key: "+ in);
        }catch(IOException e){
            System.err.println("Error in Connection reciveLastKey method");
        }catch(Exception e){
            System.err.println("Error in Connection reciveLastKey method, invalid key");
        }
    }
    private void sendEncryptedKey(){
        try{
            sessionIV = AESUtils.generateIv();
            sessionKey = AESUtils.generateKey(256);
            String key = AESUtils.toBase64(sessionKey);
            String iv = AESUtils.byteArrayToString(sessionIV.getIV());
            String json = JsonUtils.toJson(new AesKey(key, iv));
            PublicKey publicKey = PersistencyManager.getRsaKey();
            json = RSAUtils.toBase64(RSAUtils.encrypt(json, publicKey));
            cu.writeLine(CLIENT + json);
        }catch(Exception e){
            System.err.println("Error in Connection sendEncryptedKey method");
            e.printStackTrace();
        }
    }

    private String hash(String s){
        try {
            byte[] ha = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * ha.length);
            for (byte b : ha) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
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

    private boolean loginTemplate(Auth auth, Function<String, Boolean> f){
        String json = JsonUtils.toJson(auth);
        try{
            sendEncrypted(json, CLIENT);
        }catch(GeneralSecurityException e){
            e.printStackTrace();
            return false;
        }
        try {
            String in = readFromServer();
            return f.apply(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error in Connection login method  " + e.getMessage());
            return false;
        }
    }

    public boolean login(String user, String password){
        password = hash(password);
        return loginTemplate(new Auth(user, password), in -> {
            if(in.equals("NO")){
                System.out.println("Invalid username or password");
                return false;
            }
            if(in.startsWith("OK--")){
                try{
                    in = in.substring(4);
                    String token = AESUtils.decrypt(in, sessionKey, sessionIV);
                    PersistencyManager.updateUser(user);
                    PersistencyManager.updateAuthToken(token);
                    System.out.println("Logged in as " + user);
                }catch(GeneralSecurityException e){
                    e.printStackTrace();
                    System.err.println("Error in Connection login method, invalid token or something crypto");
                }
                return true;//comunque sono autenticato, ma non ho il token
            }
            return in.startsWith("OK");//può essere che non mi venga mandato il token
        });
    }
    public boolean login(){
        return loginTemplate(new Auth(PersistencyManager.getUser(), PersistencyManager.getAuthToken()) ,s -> {
            switch (s) {
                case "OK":
                    System.out.println("Logged in as " + PersistencyManager.getUser());
                    return true;
                case "NO":
                    System.out.println("Invalid username or token");
                    return false;
                case "EXPIRED":
                    System.out.println("Token expired");
                    return false;
                default:
                    System.out.println("Invalid input from " + Connection.SERVER);
                    return false;
            }
        });
    }

    public boolean register(String user, String password){
        password = hash(password);
        return loginTemplate(new Auth(user, password), in -> {
            if(in.equals("alreadyExists"))
                System.out.println("User already exist");
            return in.startsWith("OK");
        });
    }
    public void sendInt(int i){
        writeInt(i);
    }


    private void sendBye(){
        cu.writeLine(CLIENT + "Bye");
    }

    public boolean bye()   {
        try{
            sendBye();
            readBye();
            socket.close();
            return true;
        }catch (IOException e){
            System.err.println("Error in Connection bye method");
            return false;
        }
    }

    public void sendLabel(String label, String comment, Timestamp ts) throws GeneralSecurityException {
        String json = JsonUtils.toJson(new Activity(PersistencyManager.getUser(), label, ts,null, comment));
        sendEncrypted(json, CLIENT);
    }
}
