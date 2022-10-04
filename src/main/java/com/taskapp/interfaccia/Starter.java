package com.taskapp.interfaccia;

import com.gluonhq.charm.glisten.application.AppManager;
import com.taskapp.TaskCounterApplication;
import com.taskapp.crypto.PersistencyManager;

import static com.taskapp.TaskCounterApplication.*;

public class Starter extends Thread{
    @Override
    public void run() {

        boolean serverUp;
        serverUp = Backend.checkServerUp();
        netCheck.P();
        SERVER_UP = serverUp;
        netCheck.V();
        if(!SERVER_UP) {
            netCheck.P();
            LOGGED_IN = false;
            netCheck.V();
            return;
        }
        boolean loggedin = Backend.checkTokenValidility();
        netCheck.P();
        LOGGED_IN = loggedin;
        netCheck.V();
    }
}
