package com.eservglobal.mvc;

import com.eservglobal.soa.ComponentData;
import com.eservglobal.soa.EnableConnection;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CenterPanelController implements Initializable {

    @FXML
    private JFXButton saveFileBtn;

    @FXML
    private JFXTextArea areaText;

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
    private GridPane gridPane;

    @FXML
    public JFXButton connectBtn;

    public static TextArea areaTextP;
    public static JFXButton saveFileBtnP;
    static GridPane gridPaneP;
    static GridPane gridPane2P;
    static JFXButton connectBtnP;
    static JFXTextField addressP;
    static JFXTextField usernameP;
    static JFXPasswordField passwordP;
    private static int sessionID = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        areaText.setEditable(false);
        searchBtn.setDisable(true);
        gridPaneP = gridPane;
        gridPane2P = gridPane2;
        connectBtnP = connectBtn;
        saveFileBtnP = saveFileBtn;
        areaTextP = areaText;
        addressP = address;
        usernameP = username;
        passwordP = password;

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
                    setAlertBox(Alert.AlertType.INFORMATION, "Connection to t3://" + address.getText() + ":7001 successful!","Information");
                    SidePanelContentController.b1P.setDisable(true);
                    SidePanelContentController.b2P.setOpacity(0.5);
                    SidePanelContentController.b2P.setDisable(false);
                    SidePanelContentController.b2P.setOpacity(1);
                    SidePanelContentController.b3P.setDisable(false);
                    SidePanelContentController.b3P.setOpacity(1);
                    SidePanelContentController.b4P.setDisable(false);
                    SidePanelContentController.b4P.setOpacity(1);
                    BackPanelController.circleP.setFill(Paint.valueOf("#00ff33"));
                    connectBtn.setVisible(false);
                    gridPane.setVisible(false);
                }
            } else {
                setAlertBox(Alert.AlertType.WARNING, "Please complete all the fields!","Warning");
            }
        });

        instanceId.addEventHandler(KeyEvent.KEY_RELEASED, (e) -> {
            String text = instanceId.getText();
            boolean disableButton = text.isEmpty() || text.trim().isEmpty();
            searchBtn.setDisable(disableButton);
        });

        instanceId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                instanceId.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        searchBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            ComponentData.getInstance().setInstanceID(instanceId.getText());
            try {
                ComponentData.getInstance().storeAuditTrail();
                setAlertBox(Alert.AlertType.INFORMATION, "Search for composite ID " + instanceId.getText() + " complete!","Information");
                ComponentData.getInstance().displaySummary();
                searchBtn.setDisable(true);
                areaText.setVisible(true);
            } catch (Exception ex) {
                Logger.getLogger(CenterPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        saveFileBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            FileChooser fileChooser = new FileChooser();

            // set the extension filter to .txt
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extensionFilter);

            // show the save file dialog box
            Scene scene = saveFileBtn.getScene();
            if (scene != null) {
                Window window = scene.getWindow();
                File file = fileChooser.showSaveDialog(window);
                if (file != null) {
                    SaveFile(ComponentData.getFilename(), file);
                }
            }
        });
    }

    private void SaveFile(String fileToCopy, File file) {
        Path path = Paths.get(fileToCopy);
        // try-with-resources
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            setAlertBox(Alert.AlertType.INFORMATION,"File saved to disk successflly!","Information");
        } catch (IOException ex) {
            Logger.getLogger(CenterPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String authorize(String address, String username, String password) {
        EnableConnection enableConnection = new EnableConnection(address, username, password);
        try {
            enableConnection.connect();
            return generateSessionID();
        } catch (CommunicationException e) {
            setAlertBox(Alert.AlertType.ERROR, e.getCause().toString(),"Error");
        } catch (NamingException e) {
            setAlertBox(Alert.AlertType.WARNING, "Invalid credentials!","Warning");
        } catch (Exception e) {
            setAlertBox(Alert.AlertType.ERROR, e.getMessage(),"Error");
        }
        return null;
    }

    private void setAlertBox(Alert.AlertType type, String message,String title) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private String generateSessionID() {
        sessionID++;
        return username.getText() + " - session " + sessionID;
    }
}