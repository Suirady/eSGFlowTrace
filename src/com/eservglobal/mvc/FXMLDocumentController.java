package com.eservglobal.mvc;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXMLDocumentController implements Initializable {

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private BorderPane root;

    public static BorderPane rootP;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (!NaivgationDrawer.isSplashLoaded) {
            loadSplashScreen();
        }

        rootP = root;

        try {
            VBox box = FXMLLoader.load(getClass().getResource("SidePanelContent.fxml"));
            drawer.setSidePane(box);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
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
    }

    private void loadSplashScreen() {
        try {
            NaivgationDrawer.isSplashLoaded = true;
            
            StackPane pane = FXMLLoader.load(getClass().getResource(("SplashFXML.fxml")));
            root.getChildren().setAll(pane);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), pane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), pane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            fadeIn.play();

            fadeIn.setOnFinished((e) -> fadeOut.play());

            fadeOut.setOnFinished((e) -> {
                try {
                    BorderPane parentContent = FXMLLoader.load(getClass().getResource(("FXMLDocument.fxml")));
                    root.getChildren().setAll(parentContent);

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private void loadSplashScreen() {
//        try {
//            NaivgationDrawer.isSplashLoaded = true;
//            
//            System.out.println("Loading Splash");
//            StackPane pane = FXMLLoader.load(getClass().getResource("SplashFXML.fxml"));
//            root.getChildren().setAll(pane);
//
//            FadeTransition transition = new FadeTransition(Duration.seconds(3), pane);
//            transition.setFromValue(0);
//            transition.setToValue(1);
//            transition.setCycleCount(1);
//
//            FadeTransition transition1 = new FadeTransition(Duration.seconds(3), pane);
//            transition1.setFromValue(1);
//            transition1.setToValue(0);
//            transition1.setCycleCount(1);
//
//            transition.setOnFinished((ActionEvent event) -> {
//                transition1.play();
//                System.out.println("Transition 1 complete");
//            });
//
//            transition1.setOnFinished((ActionEvent event) -> {
//                System.out.println("Transition 2 complete");
//                try {
//                    FXMLLoader loader = new FXMLLoader();
//                    AnchorPane pane1 = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
//                    FXMLDocumentController controller = loader.getController();
//                    root.getChildren().setAll(pane1);
//                    return;
//                } catch (IOException ex) {
//                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            });
//
//            transition.play();
//
//        } catch (IOException ex) {
//            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
