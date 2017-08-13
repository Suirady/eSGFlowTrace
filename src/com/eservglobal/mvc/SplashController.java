package com.eservglobal.mvc;

import insidefx.undecorator.Undecorator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.fxml.FXMLLoader.load;

public class SplashController implements Initializable {

    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new SplashScreen().start();
    }
    class SplashScreen extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep((long) (Math.floor(Math.random() * 5) + 1) * 1000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        BorderPane root = null;
                        try {
                            root = load(getClass().getResource("BackPanel.fxml"));
                        } catch (IOException ex) {
                            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        Stage stage = new Stage();
                        // undecorator extends StackPane
                        Undecorator undecorator = new Undecorator(stage, root);
                        undecorator.getStylesheets().add("skin/undecorator.css");

                        Scene scene = new Scene(undecorator, 750, 500);
                        stage.setScene(scene);

                        // Fade transition on window closing request
                        stage.setOnCloseRequest(event -> {
                            event.consume(); // Do not hide
                            undecorator.setFadeOutTransition();
                        });

                        // hide some buttons
                        Node node = scene.lookup("#StageMenu");
                        node.setVisible(false);
                        node = scene.lookup("#fullscreen");
                        node.setVisible(false);
                        node = scene.lookup("#maximize");
                        node.setVisible(false);
                        node = scene.lookup("#minimize");
                        AnchorPane.setRightAnchor(node, AnchorPane.getRightAnchor(node) - 34.0);

                        // Transparent scene and stage
                        scene.setFill(Color.TRANSPARENT);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        stage.setTitle("No title bar");
                        stage.setResizable(false);
                        stage.show();
                        stackPane.getScene().getWindow().hide();
                    }
                });

            } catch (InterruptedException ex) {
                Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

