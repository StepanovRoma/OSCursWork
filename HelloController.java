package com.example.visualclient;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.visualclient.ClientConnect;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class HelloController {
    private boolean flag = true;
    private boolean flagflag = false;
    private static String ipAddr = "localhost";
    private static final int port = 8080;
    private static ClientConnect clientConnect;


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label clientName;

    @FXML
    private TextField nameField;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea welcomeText;

    @FXML
    void initialize() {
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle("Server ip");
        tid.setHeaderText("Enter server ip:");
        tid.setContentText("ip: ");
        tid.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);
        Optional<String> ip = tid.showAndWait();
        if (ip.isPresent()) {
            this.flagflag = true;
            ipAddr = ip.get();
        }
        Platform.runLater(() -> {
            nameField.requestFocus();
        });
        sendButton.setOnAction(event -> {
            if (flag) {
                if (!nameField.getText().equals("")) {
                    clientName.setText(nameField.getText());
                    nameField.clear();
                    welcomeText.clear();
                    nameField.setPromptText("Enter your message");
                    flag = false;
                    clientConnect = new ClientConnect(ipAddr, port, clientName.getText(), this);
                }
            } else {
                if (!nameField.getText().equals(""))
                    clientConnect.send(nameField.getText());
                nameField.clear();
            }
        });
    }
    /*@FXML
    public void handleEnterPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER && clientConnect != null && !nameField.getText().equals("") && this.flagflag) {
        sendButton.fire();
    }
}*/
    public void appendMsg(String msg) {
        Platform.runLater( () -> {welcomeText.appendText(msg + "\n");} );
    }
    public void exitApplication() {
        if (clientConnect != null && clientConnect.flag) {
            clientConnect.downService();
        }
    }
}
