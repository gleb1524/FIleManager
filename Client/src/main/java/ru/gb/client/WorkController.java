package ru.gb.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.gb.client.net.ClientService;
import ru.gb.client.net.NettyClient;
import ru.gb.dto.*;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class WorkController implements Initializable {
   private GetFileInfo clientFileInfo;
   private GetFileInfo serverFileInfo;

    private final String CLIENT_DIR = ".";

    private final int MB_16 = 16_000_000;

    @FXML
    public TextField serverDir;
    @FXML
    public TableView<FileInfo> clientTable;
    @FXML
    public TableView<FileInfo> serverTable;
    @FXML
    public TextField clientDir;
    @FXML
    public ComboBox disksBoxClient;
    @FXML
    public ComboBox disksBoxServer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        disksBoxClient.getItems().clear();
        for(Path p : FileSystems.getDefault().getRootDirectories()){
            disksBoxClient.getItems().add(p.toString());
        }
        disksBoxClient.getSelectionModel().select(0);

        disksBoxServer.getItems().clear();

        for(Path p : Paths.get(ClientService.getServerPath())){
            disksBoxServer.getItems().add(p.toString());
        }
        disksBoxServer.getItems().remove(0);
        disksBoxServer.getSelectionModel().select(0);


        ClientService.setWorkController(this);
       clientFileInfo = new GetFileInfo(clientTable, Paths.get(CLIENT_DIR), clientDir);
       serverFileInfo = new GetFileInfo(serverTable, Paths.get(ClientService.getServerPath()), serverDir);

    }

    @FXML
    public void copyBtnAction(ActionEvent actionEvent) throws IOException {
        String name = clientDir.getText();
        Path path = Paths.get(name);



        saw(path, b -> {
            UploadRequest uploadRequest = new UploadRequest(name, serverDir.getText());
            uploadRequest.setData(b);
            uploadRequest.setByteRead(b.length);
            try {
                NettyClient.getChannel().writeAndFlush(uploadRequest).sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(b.length +"отправилось");});
        updateServerTable();
    }

    public void saw(Path path, Consumer<byte[]> filePartConsumer) {
        byte[] filePart = new byte[MB_16];
        int count;


        try (FileInputStream fileInputStream = new FileInputStream((path).toFile())) {
            while ((count = fileInputStream.read(filePart)) != -1) {
                byte[] test;
                if (count >= MB_16) {
                    filePartConsumer.accept(filePart);
                } else {
                    test = Arrays.copyOf(filePart, count);
                    filePartConsumer.accept(test);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void deleteBtnAction(ActionEvent actionEvent) {
    }
    @FXML
    public void creatDirAction(ActionEvent actionEvent) throws InterruptedException {
        Alert creatDirAlert  = new Alert(Alert.AlertType.CONFIRMATION,
                "Хотите создать новую папку?", ButtonType.NEXT, ButtonType.CANCEL);
        creatDirAlert.showAndWait();
        TextInputDialog dirName = new TextInputDialog("Введите имя");
        dirName.showAndWait();
        String dir = dirName.getEditor().getText();
        String creatDir = serverDir.getText()+"\\"+dir;
        System.out.println(creatDir);
        FileRequest request = new FileRequest(creatDir, ClientService.getAuth(), ClientService.getLogin());
        NettyClient.getChannel().writeAndFlush(request);

    }

    public void updateServerTable(){
        serverFileInfo.updateList(Paths.get(ClientService.getServerPath()), serverTable, serverDir);
    }
    @FXML
    public void clickToClose(ActionEvent actionEvent) {
    }
    @FXML
    public void selectDiskActionServer(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        Path serverPath = Paths.get(element.getSelectionModel().getSelectedItem());
        serverFileInfo.updateList(serverPath, serverTable, serverDir);

    }
    @FXML
    public void selectDiskActionClient(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        Path clientPath = Paths.get(element.getSelectionModel().getSelectedItem());
        clientFileInfo.updateList(clientPath, clientTable, clientDir);

    }
    @FXML
    public void btnPathUpActionClient(ActionEvent actionEvent) {
        Path clientPath = Paths.get(clientDir.getText()).getParent();
        if(clientPath != null){
           clientFileInfo.updateList(clientPath, clientTable, clientDir);
        }

    }
    @FXML
    public void btnPathUpActionServer(ActionEvent actionEvent) {
        Path serverPath = Paths.get(serverDir.getText()).getParent();
        Path serverPathControl = Paths.get(ClientService.getServerPath()).normalize().toAbsolutePath().getParent();
        System.out.println(serverPath.toString());
        System.out.println(serverPathControl.toString());
        if(!serverPath.equals(serverPathControl)){
            if(serverPath != null){
                clientFileInfo.updateList(serverPath, serverTable, serverDir);
            }
        }
    }


}
