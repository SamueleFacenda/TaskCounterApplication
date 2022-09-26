package com.taskapp.views;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class LoginView {
    public View getView() {
        try {
            View view = FXMLLoader.load(LoginView.class.getResource("login.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
