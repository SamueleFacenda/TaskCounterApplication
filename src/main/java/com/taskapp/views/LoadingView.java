package com.taskapp.views;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class LoadingView {
    public View getView() {
        try {
            return FXMLLoader.load(LoginView.class.getResource("loading.fxml"));
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
