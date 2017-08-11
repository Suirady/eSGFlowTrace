package com.eservglobal.mvc;

import insidefx.undecorator.Undecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NaivgationDrawer extends Application {
    
    public static Boolean isSplashLoaded = false;


    @Override
    public void start(final Stage stage) throws Exception {
        Region root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        // undecorator extends StackPane
        Undecorator undecorator = new Undecorator(stage, root);
        undecorator.getStylesheets().add("skin/undecorator.css");
        // undecorator.getStylesheets().add("@resources/mySettings.css");

        Scene scene = new Scene(undecorator, 600, 400);
        stage.setScene(scene);

        // Fade transition on window closing request
        stage.setOnCloseRequest(event -> {
            event.consume(); // Do not hide
            undecorator.setFadeOutTransition();
        });

        // hidden some button
        Node node = scene.lookup("#StageMenu");
        node.setVisible(false);

        // Transparent scene and stage
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        // Set minimum size
        stage.setMinWidth(300);
        stage.setMinHeight(200);
        stage.setTitle("No title bar");

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
