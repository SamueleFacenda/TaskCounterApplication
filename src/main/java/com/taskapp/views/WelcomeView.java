package com.taskapp.views;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class WelcomeView {
    public View getView() {
        try {
            return FXMLLoader.load(WelcomeView.class.getResource("welcome.fxml"));
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
