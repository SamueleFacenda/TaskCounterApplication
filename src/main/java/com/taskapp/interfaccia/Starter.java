package com.taskapp.interfaccia;

import static com.taskapp.TaskCounterApplication.*;

public class Starter extends Thread{

    public static final int WAIT_TIME = 60000;
    @Override
    public void run() {
        //parallel check for the server status and login status
        while(true){
            boolean serverUp;
            serverUp = Backend.checkServerReachable();
            netCheck.P();
            SERVER_REACHABLE = serverUp;
            netCheck.V();
            if (!SERVER_REACHABLE) {
                netCheck.P();
                LOGGED_IN = false;
                netCheck.V();
                return;
            }
            boolean loggedin = Backend.checkTokenValidility();
            netCheck.P();
            LOGGED_IN = loggedin;
            netCheck.V();

            break;//remove for check every tot time
            /*
            try{
                sleep(WAIT_TIME);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            //*/
        }
    }
}
