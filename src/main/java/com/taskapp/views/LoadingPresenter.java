package com.taskapp.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

import static com.taskapp.TaskCounterApplication.*;

public class LoadingPresenter {
    @FXML
    private Label label;
    @FXML
    private ProgressIndicator circle;

    public void initialize() {
        /*
        AtomicInteger loading = new AtomicInteger(0);
        Timeline t = new Timeline();
        t.getKeyFrames().add(
                new KeyFrame(Duration.millis(600),
                e -> label.setText("Loading" + ".".repeat(loading.getAndIncrement() % 4))
                ));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
         */
        new AnimationTimer() {
            private long startTime = -1;
            @Override
            public void handle(long now) {
                if(startTime == -1)
                    startTime = now;

                circle.setProgress(Math.abs(Math.cos((now - startTime) / 1_000_000_000.0 * Math.PI /2) + 1) % 1);
            }
        }.start();

        //check net and login to change the view
        Timeline t2 = new Timeline();
        t2.getKeyFrames().add(
                new KeyFrame(Duration.millis(100), e -> {
                    netCheck.P();
                    if(LOGGED_IN != null){
                        System.out.println("logged in cambiato");
                        String swap = (!LOGGED_IN && SERVER_UP) ? LOGIN_VIEW : PRIMARY_VIEW;
                        netCheck.V();
                        AppManager.getInstance().switchView(swap);
                        t2.stop();
                    }else
                        netCheck.V();
        }));
        t2.setCycleCount(Timeline.INDEFINITE);
        t2.play();
    }
}
