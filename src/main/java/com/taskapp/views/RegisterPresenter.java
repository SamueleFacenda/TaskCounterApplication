package com.taskapp.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.taskapp.interfaccia.Backend;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import static com.taskapp.TaskCounterApplication.PRIMARY_VIEW;

public class RegisterPresenter {

    @FXML
    private View register;
    @FXML
    private PasswordField password1, password2;
    @FXML
    private TextField username;
    @FXML
    private Label status;

    public void initialize() {
        register.setShowTransitionFactory(BounceInRightTransition::new);

        register.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText("Register");
            }
        });
    }
    @FXML
    private void submit(){
        String user = username.getText().trim(), pass1 = password1.getText().trim(), pass2 = password2.getText().trim();
        if(!pass1.equals(pass2)){
            status.setText("Passwords are not the same");
            return;
        }
        if(user.equals("") || pass1.equals("")){
            status.setText("Username or password are empty");
            return;
        }
        if(!Backend.register(user, pass1)){
            status.setText("Registration failed, username already exists");
            return;
        }
        status.setText("Registration successful");
        AppManager.getInstance().switchView(PRIMARY_VIEW);
    }
}
