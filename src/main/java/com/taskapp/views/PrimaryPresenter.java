package com.taskapp.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.taskapp.TaskCounterApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static com.taskapp.TaskCounterApplication.SERVER_UP;
import static com.taskapp.TaskCounterApplication.netCheck;

public class PrimaryPresenter {

    @FXML
    private View primary;

    @FXML
    private Label label, status;

    public void initialize() {
        primary.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText("Primary");
                appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> 
                        System.out.println("Search")));
            }
        });

        netCheck.P();
        status.setText("Server is " + (SERVER_UP != null && SERVER_UP ? "up" : "down"));
        netCheck.V();
    }
    
    @FXML
    void buttonClick() {
        label.setText("Hello JavaFX Universe!");
    }
    
}
