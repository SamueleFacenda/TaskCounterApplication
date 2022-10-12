package com.taskapp.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.AutoCompleteTextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.taskapp.interfaccia.LabelManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.List;

import static com.taskapp.TaskCounterApplication.SERVER_REACHABLE;
import static com.taskapp.TaskCounterApplication.netCheck;

public class PrimaryPresenter {

    @FXML
    private View primary;
    @FXML
    private Label status;

    @FXML
    private TextArea comment;
    @FXML
    private AutoCompleteTextField<String> lbl;

    private long lastTouch = 0;

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
        status.setText("Server is " + (SERVER_REACHABLE != null && SERVER_REACHABLE ? "up" : "down"));
        netCheck.V();

        lbl.setCompleter(t -> List.of(LabelManager.getLabels(t)));
        lbl.valueProperty().addListener((observable, oldValue, newValue) -> {
            lbl.setText(newValue);
        });
        //prossimo step: mettere un bottone dentro la questo textfield

        //test che non funzionano per avere l'autocompletamento appena clicco il campo, con click, touch e focus
        lbl.setOnMouseClicked(e -> lbl.search());
        lbl.setOnTouchPressed(e -> {
            lastTouch = System.currentTimeMillis();
        });
        lbl.setOnTouchReleased(e ->{
            if(System.currentTimeMillis() - lastTouch < 500)
                lbl.search();
        });

        lbl.focusedProperty().addListener((observableValue, oldProperty, newProperty) -> {
            if(newProperty){
                //focus acquired
                System.out.println("click");
                lbl.search();
            }
        });
    }

    private void submit(String label, String commento){
        if(label == null || label.isEmpty())
            return;

        lbl.setText("");
        comment.setText("");

        LabelManager.save(label, commento);
    }
    
    @FXML
    private void submit(){
        submit(lbl.getText(), comment.getText());
    }
    
}
