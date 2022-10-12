package com.taskapp.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

import static com.taskapp.TaskCounterApplication.*;


public class WelcomePresenter {
    @FXML
    private View welcome;
    @FXML
    private Label loading;

    public void initialize() {
        welcome.setShowTransitionFactory(BounceInRightTransition::new);

        welcome.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText("Welcome");
            }
        });

        /*
        AtomicInteger i = new AtomicInteger();
        Timeline t = new Timeline();
        t.getKeyFrames().add(
                new KeyFrame(Duration.millis(500), e -> {
                    boolean isset;
                    netCheck.P();
                    isset = SERVER_REACHABLE != null;
                    netCheck.V();
                    if(isset){
                        boolean isup;
                        netCheck.P();
                        isup = SERVER_REACHABLE;
                        netCheck.V();
                        if(isup)
                            AppManager.getInstance().switchView(LOGIN_VIEW);
                        else
                            AppManager.getInstance().switchView(PRIMARY_VIEW);
                        t.stop();
                    }else
                        if(loading != null)
                            loading.setText("Loading" + ".".repeat(i.getAndIncrement() % 4));
                }));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();*/
    }

    @FXML
    private void hyperLogin(){
        AppManager.getInstance().switchView(LOGIN_VIEW);
    }
    @FXML
    private void hyperRegister(){
        AppManager.getInstance().switchView(REGISTER_VIEW);
    }
    @FXML
    private void hyperTutorial(){
        System.out.println("Tutorial");
    }
}
