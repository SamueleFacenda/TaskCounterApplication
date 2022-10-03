package com.taskapp;

import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.attach.util.Services;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.control.NavigationDrawer.Item;
import com.gluonhq.charm.glisten.control.NavigationDrawer.ViewItem;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.image.Image;

import java.util.Objects;

import static com.taskapp.TaskCounterApplication.*;

public class DrawerManager {

    public static void buildDrawer(AppManager app) {
        NavigationDrawer drawer = app.getDrawer();
        
        NavigationDrawer.Header header = new NavigationDrawer.Header("TickStats",
                "Manage and track your days",
                new Avatar(21, new Image(Objects.requireNonNull(DrawerManager.class.getResourceAsStream("/dalleIcon.png")))));
        drawer.setHeader(header);

        final Item primaryItem = new ViewItem("Primary", MaterialDesignIcon.HOME.graphic(), PRIMARY_VIEW, ViewStackPolicy.SKIP);
        final Item secondaryItem = new ViewItem("Secondary", MaterialDesignIcon.DASHBOARD.graphic(), SECONDARY_VIEW);
        drawer.getItems().addAll(primaryItem, secondaryItem);
        
        if (Platform.isDesktop()) {
            final Item quitItem = new Item("Quit", MaterialDesignIcon.EXIT_TO_APP.graphic());
            quitItem.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
                }
            });
            drawer.getItems().add(quitItem);
        }
    }
}