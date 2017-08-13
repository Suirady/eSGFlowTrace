package com.eservglobal.mvc;

import com.eservglobal.soa.ComponentData;
import com.eservglobal.soa.EnableConnection;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    private GridPane gridPane2;

    @FXML
    private JFXTextField instanceId;

    @FXML
    private JFXButton searchBtn;

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
    static GridPane gridPane2P;
    static JFXButton connectBtnP;
    private static int sessionID = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gridPaneP = gridPane;
        gridPane2P = gridPane2;
        connectBtnP = connectBtn;
        searchBtn.setDisable(true);

        try {
            VBox box = FXMLLoader.load(getClass().getResource("SidePanelContent.fxml"));
            drawer.setSidePane(box);
        } catch (IOException ex) {
            Logger.getLogger(CenterPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }

        address.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d+\\.?)+")) {
                address.setText(newValue.replaceAll("[^\\d+(.\\d+)*$]", ""));
            }
        });

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

        connectBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty() && !address.getText().isEmpty()) {
                String sessionID = authorize(address.getText(), username.getText(), password.getText());
                // to be changed != null
                if (sessionID != null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Connection to t3://" + address.getText() + ":7001 was successful!", ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.setTitle(null);
                    alert.showAndWait();
                    SidePanelContentController.b1P.setOpacity(0.5);
                    SidePanelContentController.b1P.setDisable(true);
                    SidePanelContentController.b2P.setOpacity(1);
                    SidePanelContentController.b2P.setDisable(false);
                  //  SidePanelContentController.b3P.setOpacity(1);
                    SidePanelContentController.b3P.setDisable(false);
                   // SidePanelContentController.b4P.setOpacity(1);
                    SidePanelContentController.b4P.setDisable(false);
                    BackPanelController.circleP.setFill(Paint.valueOf("#00ff33"));
                    connectBtn.setVisible(false);
                    gridPane.setVisible(false);
                }
            } else {
                setAlertBox("Please complete all the fields!");
            }
        });

        instanceId.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            String text = instanceId.getText();
            boolean disableButton = text.isEmpty() || text.trim().isEmpty();
            searchBtn.setDisable(disableButton);
        });

        instanceId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                instanceId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        searchBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            ComponentData.getInstance().setInstanceID(instanceId.getText());
            try {
                ComponentData.getInstance().storeItems();
            } catch (Exception ex) {
                Logger.getLogger(CenterPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private String authorize(String address, String username, String password) {
        EnableConnection enableConnection = new EnableConnection(address, username, password);
        try {
            enableConnection.connect();
            return generateSessionID();
        } catch (CommunicationException e) {
            setAlertBox(e.getCause().toString());
        } catch (NamingException e) {
            setAlertBox("Invalid credentials!");
        } catch (Exception e) {
            setAlertBox(e.getMessage());
        }
        return null;
    }

    private void setAlertBox(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(null);
        alert.showAndWait();
    }

    private String generateSessionID() {
        sessionID++;
        return username.getText() + " - session " + sessionID;
    }

}