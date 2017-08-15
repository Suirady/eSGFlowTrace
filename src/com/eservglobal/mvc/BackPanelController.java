package com.eservglobal.mvc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackPanelController implements Initializable {

    static Circle circleP;

    @FXML
    private Hyperlink hyperLink;

    @FXML
    private Circle circle;

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        circleP = circle;
        hyperLink.setBorder(Border.EMPTY);
        try {
            borderPane.setCenter(FXMLLoader.load(getClass().getResource("CenterPanel.fxml")));
        } catch (IOException ex) {
            Logger.getLogger(BackPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }

        hyperLink.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.eservglobal.com/"));
                hyperLink.setVisited(true);
            } catch (IOException | URISyntaxException e1) {
                Logger.getLogger(BackPanelController.class.getName()).log(Level.SEVERE, null, e1);
            }
        });
    }
}
