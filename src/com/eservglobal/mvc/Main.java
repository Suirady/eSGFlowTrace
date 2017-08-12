package com.eservglobal.mvc;

import insidefx.undecorator.Undecorator;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable {

    static Boolean isSplashLoaded = false;

    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            borderPane.setCenter(FXMLLoader.load(getClass().getResource("CenterPanel.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(final Stage stage) throws Exception {
        Region root = FXMLLoader.load(getClass().getResource("Main.fxml"));

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
    }


    public static void main(String[] args) {
        launch(args);
    }
}
