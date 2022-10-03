package com.taskapp.utils;

public class Semaforo {
    private int c;
    public Semaforo(int c){
        this.c = c;
    }
    public synchronized void P(){
        while(c == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        c--;
    }
    public synchronized void V(){
        c++;
        notify();
    }
}
