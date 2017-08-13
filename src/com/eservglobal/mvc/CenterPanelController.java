package com.eservglobal.mvc;

import com.eservglobal.soa.GetComponentsBasedOnCompositeId;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CenterPanelController implements Initializable {

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXTextField address;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gridPane;

    @FXML
    public JFXButton connectBtn;

    static GridPane gridPaneP;
    static JFXButton connectBtnP;
    private static int sessionID = 0;

    private GetComponentsBasedOnCompositeId getComponentsBasedOnCompositeId ;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gridPaneP = gridPane;
        connectBtnP = connectBtn;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("SidePanelContent.fxml"));
            drawer.setSidePane(box);
        } catch (IOException ex) {
            Logger.getLogger(CenterPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (drawer.isShown()) {
                drawer.close();
            } else {
                drawer.open();
            }
        });

        connectBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty() && !address.getText().isEmpty()) {
              String sessionID = authorize(address.getText(), username.getText(), password.getText());
                if (sessionID != null) {
                    SidePanelContentController.b1P.setOpacity(0.5);
                    SidePanelContentController.b1P.setDisable(true);
                    SidePanelContentController.b2P.setOpacity(1);
                    SidePanelContentController.b2P.setDisable(false);
                    SidePanelContentController.b3P.setOpacity(1);
                    SidePanelContentController.b3P.setDisable(false);
                    SidePanelContentController.b4P.setOpacity(1);
                    SidePanelContentController.b4P.setDisable(false);
                    BackPanelController.circleP.setFill(Paint.valueOf("#00ff33"));
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please complete all the fields!", ButtonType.OK);
                alert.setTitle(null);
                alert.show();
            }
        });
    }

    private String authorize(String address, String username, String password) {
        getComponentsBasedOnCompositeId = new GetComponentsBasedOnCompositeId(address, username, password);
        try {
            getComponentsBasedOnCompositeId.connect();
            return generateSessionID();
        } catch (CommunicationException e) {
            setAlertBox(e.getCause().toString());
        } catch (NamingException e) {
            setAlertBox("Invalid IP format!");
            return "xxx";
        } catch (Exception e) {
            setAlertBox(e.getMessage());
        }
        return null;
    }

    private void setAlertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle(null);
        alert.showAndWait();
    }

    private String generateSessionID() {
        sessionID++;
        return username.getText() + " - session " + sessionID;
    }

}