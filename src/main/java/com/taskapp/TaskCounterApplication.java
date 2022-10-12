package com.taskapp;

import com.taskapp.crypto.PersistencyManager;
import com.taskapp.interfaccia.LabelManager;
import com.taskapp.interfaccia.Starter;
import com.taskapp.utils.Semaforo;
import com.taskapp.views.*;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;

public class TaskCounterApplication extends Application {

    public static final String PRIMARY_VIEW = "Primary View";
    public static final String LOADING_VIEW = HOME_VIEW;//start always with the loading view
    public static final String SECONDARY_VIEW = "Secondary View";
    public static final String WELCOME_VIEW = "Welcome View";
    public static final String REGISTER_VIEW = "Register View";
    public static final String LOGIN_VIEW = "Login View";
    public static Boolean FIRST_TIME = null, SERVER_REACHABLE = null, LOGGED_IN = null;
    public static Semaforo netCheck = new Semaforo(1);

    private final AppManager appManager = AppManager.initialize(this::postInit);

    @Override
    public void init() {
        appManager.addViewFactory(LOGIN_VIEW, () -> new LoginView().getView());
        appManager.addViewFactory(PRIMARY_VIEW, () -> new PrimaryView().getView());
        appManager.addViewFactory(SECONDARY_VIEW, () -> new SecondaryView().getView());
        appManager.addViewFactory(WELCOME_VIEW, () -> new WelcomeView().getView());
        appManager.addViewFactory(REGISTER_VIEW, () -> new RegisterView().getView());
        appManager.addViewFactory(LOADING_VIEW, () -> new LoadingView().getView());

        DrawerManager.buildDrawer(appManager);
        PersistencyManager.initialize();
        LabelManager.initialize();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //if it's the first time the app is launched, show the welcome view and exit this method
        boolean firstTime = PersistencyManager.isFirstTime();
        System.out.println("first time: " + firstTime);
        netCheck.P();
        FIRST_TIME = firstTime;
        netCheck.V();

        appManager.start(primaryStage);
        new Starter().start();//start checking server status
    }

    private void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);
        scene.getStylesheets().add(Objects.requireNonNull(TaskCounterApplication.class.getResource("style.css")).toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(Objects.requireNonNull(TaskCounterApplication.class.getResourceAsStream("/dalleIcon.png"))));

    }

    public static void main(String[] args) {
        launch(args);
    }
}
