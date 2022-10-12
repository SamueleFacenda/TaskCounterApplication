package com.taskapp.interfaccia;

import com.taskapp.crypto.Connection;
import com.taskapp.crypto.PersistencyManager;
import com.taskapp.dataClasses.Activity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * classe principale che fa da interfaccia di alto livello con il server
 */
public class Backend {
    private static String last_log;//ultimo log ricevuto dal server, non lo uso

    /**
     * connection template to be used with lambda
     * @param c funzione che ricevuta una connessione ritorna un boolean
     * @return il risultato della connessione contando anche il risultato della funzione parametro
     */
    private static boolean templateWithoutLogin(Function<Connection, Boolean> c){
        try{
            //crea una connessione e la inizializza
            Connection connection = new Connection(Connection.getSocket());
            connection.init();
            //esegue la funzione parametro
            boolean out = c.apply(connection);
            //quit procedure
            connection.sendInt(3);
            connection.readAck();
            connection.bye();
            return out;
        }catch(IOException e){
            last_log = "Connection error, maybe offline";
            e.printStackTrace();
            return false;
        }
    }

    /**
     * estensione del template di connessione precedente, autentica la connessione con i dati locali
     * @param fun operazione da eseguire dopo l'autenticazione
     * @return il risultato della connessione
     */
    private static boolean templateWithLogin(Consumer<Connection> fun){
        return templateWithoutLogin(c ->{
            //mando il codice di login
            c.sendInt(1);
            c.readAck();
            //mando l'username e l'auth token, senza hasharlo
            boolean out = c.login();
            if(!out)
                return false;
            //se l'autenticazione è andata a buon fine eseguo la funzione parametro
            fun.accept(c);
            return true;
        });
    }

    /**
     * eseguo il login dell'utente, comprende anche il salvataggio del token di accesso
     * @param username username dell'utente
     * @param password password dell'utente
     * @return true se sono stato autenticato
     */
    public static boolean login(String username, String password) {
        return templateWithoutLogin(c ->{
            c.sendInt(1);
            c.readAck();
            System.out.println("Sending username and password");
            //invio la password hashata
            return c.login(username, password);
        });
    }

    /**
     * registro un nuovo utente
     * @param username username dell'utente
     * @param password password dell'utente
     * @return true se sono stato registrato, false significa probabilmente che l'username è già stato preso
     */
    public static boolean register(String username, String password) {
        return templateWithoutLogin(c -> {
            //mando il codice per la registrazione
            c.sendInt(0);
            c.readAck();
            return c.register(username, password);
        });
    }

    /**
     * aggiunge un'attività sul server, se fallisce la aggiunge nella persistenza
     * @param label
     * @param comment
     */
    public static void load(String label, String comment, Timestamp ts){
        if(!templateWithLogin(c ->{
            c.sendInt(2);
            c.readAck();
            try{
                c.sendLabel(label, comment, ts);
            }catch(GeneralSecurityException e){
                e.printStackTrace();
                PersistencyManager.addActivity(label, comment, ts);
            }
        }))
            //inizio blocco dell'if, se la connessione fallisce aggiungo l'attività alla persistenza
            PersistencyManager.addActivity(label, comment, ts);
    }

    /**
     * aggiungo un array di attività sul server, con una sola connessione
     * controllo un invio alla volta che non ci siano errori e in caso stoppo tutto e riaggiungo tutto alla persistenza
     * @param activity array da aggiungere
     */
    public static void load(Activity[] activity)  {
        templateWithLogin(c ->{
            c.sendInt(2);
            c.readAck();
            boolean failed = false;
            for (Activity a : activity) {
                //una volta che un invio è fallito riaggungo tutto alla persistenza
                if(failed){
                    PersistencyManager.addActivity(a.label(), a.comment(), a.ts());
                    continue;
                }
                try{
                    c.sendLabel(a.label(), a.comment(), a.ts());
                }catch(GeneralSecurityException e){
                    failed = true;
                    PersistencyManager.addActivity(a.label(), a.comment(), a.ts());
                }
            }
        });
    }

    /**
     * @return true se il server è raggiungibile
     */
    public static boolean checkServerReachable(){
        return templateWithoutLogin(c -> true);
    }

    /**
     * @return true se ho un token di accesso valido
     */
    public static boolean checkTokenValidility(){
        if(PersistencyManager.isFirstLogin()) {
            //sicuramente non ho un token di accesso
            System.out.println("Check token validility: false");
            return false;
        }
        return templateWithLogin(c -> {});
    }
}