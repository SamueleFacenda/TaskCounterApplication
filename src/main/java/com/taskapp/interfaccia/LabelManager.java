package com.taskapp.interfaccia;


import com.taskapp.crypto.PersistencyManager;
import com.taskapp.dataClasses.Activity;
import com.taskapp.dataClasses.Lbl;

import java.util.*;
import java.util.stream.IntStream;

import static com.taskapp.TaskCounterApplication.*;
import static java.util.stream.Collectors.toMap;

public class LabelManager {

    private static Map<String, Integer> labels;
    static abstract class Waiter extends Thread{
        public Waiter(){
            super();
        }
        @Override
        public void run() {
            while(true){
                netCheck.P();
                if(SERVER_UP != null) {
                    netCheck.V();
                    break;
                }
                netCheck.V();
            }
            while(true){
                netCheck.P();
                if(LOGGED_IN != null) {
                    netCheck.V();
                    break;
                }
                netCheck.V();
            }
            doSomething();
        }

        protected abstract void doSomething();
    }
    public static void initialize() {
        new Waiter(){
            @Override
            protected void doSomething(){
                if(!SERVER_UP)
                    return;
                if(!LOGGED_IN)
                    return;
                if(!PersistencyManager.hasActivity())
                    return;
                Activity[] a = PersistencyManager.getActivity();
                PersistencyManager.clearActivity();
                Backend.load(a);//load the activities, if some error accours automaticaly read the activities to the persistency manager
            }
        }.start();
        Lbl[] l = PersistencyManager.getLabel();
        labels = new HashMap<>(
                IntStream.range(0, l.length).boxed().collect(toMap(i -> l[i].getLabel(), i -> l[i].getCount())));
        if(labels.size() == 0){
            //try to retrive labels from the server
            //TODO
        }
    }

    public static void save(String label, String comment){
        if(!labels.containsKey(label)){
            labels.put(label, 1);
            PersistencyManager.addLabel(new Lbl(label, 1));
        }else {
            labels.computeIfPresent(label, (k, v) -> v + 1);
            PersistencyManager.addLabel(new Lbl(label, labels.get(label)));
        }
        if(SERVER_UP && LOGGED_IN)
            Backend.load(label, comment);
        else
            PersistencyManager.addActivity(label, comment);
    }

    public static String[] getLabels(String in){
        return labels
                .keySet()
                .stream()
                .filter(s -> in.equals("") || s.regionMatches(true, 0, in, 0, in.length()) )
                .sorted(Comparator.comparingInt(a -> -labels.get(a)))//reverse count sort
                .limit(5)
                .toArray(String[]::new);
    }
}
