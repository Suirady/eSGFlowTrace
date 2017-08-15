package com.eservglobal.mvc;

import com.eservglobal.soa.ComponentData;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Paint;
import java.io.IOException;
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
    static JFXButton b1P;
    static JFXButton b2P;
    static JFXButton b3P;
    static JFXButton b4P;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        b1P = b1;
        b2P = b2;
        b3P = b3;
        b4P = b4;
    }

    @FXML
    private void chooseAction(ActionEvent event) throws IOException {
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
            case "Audit trail for instance ID":
                isVisible = CenterPanelController.gridPane2P.isVisible();
                if (isVisible) {
                    CenterPanelController.gridPane2P.setVisible(false);
                    CenterPanelController.areaTextP.setVisible(false);
                    CenterPanelController.saveFileBtnP.setVisible(false);
                } else {
                    CenterPanelController.gridPane2P.setVisible(true);
                    CenterPanelController.searchBtnP.setVisible(true);
                    CenterPanelController.searchBtn1P.setVisible(false);
                }
                break;
            case "Faults for instance ID":
                isVisible = CenterPanelController.gridPane2P.isVisible();
                if (isVisible) {
                    CenterPanelController.gridPane2P.setVisible(false);
                    CenterPanelController.areaTextP.setVisible(false);
                    CenterPanelController.saveFileBtnP.setVisible(false);
                } else {
                    CenterPanelController.gridPane2P.setVisible(true);
                    CenterPanelController.searchBtnP.setVisible(false);
                    CenterPanelController.searchBtn1P.setVisible(true);
                }
                break;
            case "Disconnect":
                BackPanelController.circleP.setFill(Paint.valueOf("#ff3140"));
                CenterPanelController.gridPane2P.setVisible(false);
                CenterPanelController.areaTextP.setVisible(false);
                CenterPanelController.saveFileBtnP.setVisible(false);
                CenterPanelController.addressP.clear();
                CenterPanelController.usernameP.clear();
                CenterPanelController.passwordP.clear();
                b1.setDisable(false);
                SidePanelContentController.b1P.setOpacity(1);
                b2.setDisable(true);
                SidePanelContentController.b2P.setOpacity(0.5);
                b3.setDisable(true);
                SidePanelContentController.b3P.setOpacity(0.5);
                b4.setDisable(true);
                SidePanelContentController.b4P.setOpacity(0.5);
                ComponentData.getInstance().closeLoc();
                break;
        }
    }
}
