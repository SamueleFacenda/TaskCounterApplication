package com.taskapp.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.taskapp.TaskCounterApplication;
import com.taskapp.interfaccia.Backend;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class LoginPresenter {

    @FXML
    private View login;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    private Label out;

    private boolean clickOffLine = false;

    public void initialize() {
        login.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText("Login");
            }
        });
    }

    @FXML
    private void buttonClick() {
        //login
        String user = username.getText().trim(), pass = password.getText().trim();
        if(!clickOffLine && !Backend.checkServerUp()){//skip if is second click offline
            System.out.println("Server is down");
            out.setText("Server is down, your data will be saved locally...\n Press again this button the login button to proceed");
            clickOffLine = true;
            return;
        }
        System.out.println("Login with: " + user + " " + pass);
        if(!clickOffLine && !Backend.login(user, pass)){
            System.out.println("Login failed");
            out.setText("Login failed");
            return;
        }
        AppManager.getInstance().switchView(TaskCounterApplication.PRIMARY_VIEW);
    }
}
