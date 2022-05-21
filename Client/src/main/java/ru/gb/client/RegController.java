package ru.gb.client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.gb.client.net.ClientService;
import ru.gb.client.net.NettyClient;
import ru.gb.dto.RegRequest;

import java.io.IOException;

public class RegController{


    @FXML
    public Button back;
    @FXML
    public TextField login;
    @FXML
    public PasswordField password;
    @FXML
    public TextField name;
    @FXML
    public TextField surname;
    @FXML
    public Label loginBusy;
    @FXML
    public Label registrationComplete;


    @FXML
    public void clickToClose(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            Stage stage = (Stage) back.getScene().getWindow();
            stage.close();
            NettyClient.getEventLoopGroup().shutdownGracefully();
        });
    }

    @FXML
    public void registration(ActionEvent actionEvent) throws InterruptedException {
        ClientService.setRegController(this);
        loginBusy.setVisible(false);
        registrationComplete.setVisible(false);

            if(login.getText().isEmpty()||password.getText().isEmpty()||name.getText().isEmpty()||surname.getText().isEmpty()){
                return;
            }else{
                RegRequest request = new RegRequest(login.getText(), password.getText(), name.getText(), surname.getText());
                NettyClient.getChannel().writeAndFlush(request);


        }
    }

    @FXML
    public void back(ActionEvent actionEvent) throws IOException {
        Client.setRoot("client");
    }



}
