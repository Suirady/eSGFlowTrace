package com.eservglobal.mvc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackPanelController implements Initializable {

    static Circle circleP;

    @FXML
    private Circle circle;

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        circleP = circle;
        try {
            borderPane.setCenter(FXMLLoader.load(getClass().getResource("CenterPanel.fxml")));
        } catch (IOException ex) {
            Logger.getLogger(CenterPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
