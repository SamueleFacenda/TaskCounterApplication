package com.taskapp.test;

import com.taskapp.interfaccia.Backend;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println(Backend.checkServerReachable());
    }
}
