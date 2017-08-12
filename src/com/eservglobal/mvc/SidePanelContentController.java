package com.eservglobal.mvc;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.Glow;

import java.net.URL;
import java.util.ResourceBundle;


public class SidePanelContentController implements Initializable {

    @FXML
    private JFXButton b1;
    @FXML
    private JFXButton b2;
    @FXML
    private JFXButton b3;
    @FXML
    private JFXButton b4;

    static JFXButton b2P;
    static JFXButton b3P;
    static JFXButton b4P;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        b2P = b2;
        b3P = b3;
        b4P = b4;
    }

    @FXML
    private void chooseAction(ActionEvent event) {
        JFXButton btn = (JFXButton) event.getSource();
        System.out.println(btn.getText());
        switch (btn.getText()) {
            case "Connection details":
                boolean isVisible = CenterPanelController.gridPaneP.isVisible();
                if (isVisible) {
                    CenterPanelController.gridPaneP.setVisible(false);
                    CenterPanelController.connectBtnP.setVisible(false);
                } else {
                    CenterPanelController.gridPaneP.setVisible(true);
                    CenterPanelController.connectBtnP.setVisible(true);
                }
                break;
            case "Color 2":
                //  CenterPanelController.anchorPaneP.setStyle("-fx-background-color:#0000FF");
                break;
            case "Color 3":
                //     CenterPanelController.anchorPaneP.setStyle("-fx-background-color:#FF0000");
                break;
        }
    }

    @FXML
    private void exit(ActionEvent event) {
        System.exit(0);
    }

}
