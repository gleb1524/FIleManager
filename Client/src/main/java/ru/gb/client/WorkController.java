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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
    private List<FileInfo> fileInfoList;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        IsAuthRequest isAuthRequest = new IsAuthRequest();
//        if(isAuthRequest.isAuthenticated(ClientService.getAuth())){
            fileInfoList = ClientService.getFileInfoList();
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
            serverFileInfo = new GetFileInfo(serverTable, Paths.get(ClientService.getServerPath()), serverDir, fileInfoList);

//        }

    }

    @FXML
    public void copyBtnAction(ActionEvent actionEvent) throws InterruptedException {
        String name = clientDir.getText();
        Path path = Paths.get(name);
        if(!Files.isDirectory(path)){
        saw(path, b -> {
            upload(name, serverDir.getText(), b);  //необходимо добавить получение path с сервера
            System.out.println(b.length +" отправилось");});
        FileListRequest fileListRequest = new FileListRequest(serverDir.getText(),ClientService.getAuth(),ClientService.getLogin());
        ClientService.setUpdatePath(Paths.get(serverDir.getText()));
        NettyClient.getChannel().writeAndFlush(fileListRequest).sync();
        updateServerTable();
        }
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
    public void upload(String name, String path, byte[] b){
            UploadRequest uploadRequest = new UploadRequest(name, path);
            uploadRequest.setData(b);
            uploadRequest.setByteRead(b.length);
            try {
                NettyClient.getChannel().writeAndFlush(uploadRequest).sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
    }
    @FXML
    public void deleteBtnAction(ActionEvent actionEvent) {
    }
    @FXML
    public void creatDirAction(ActionEvent actionEvent) throws InterruptedException {
        IsAuthRequest isAuthRequest = new IsAuthRequest();
//        if(isAuthRequest.isAuthenticated(ClientService.getAuth())){
            Alert creatDirAlert  = new Alert(Alert.AlertType.CONFIRMATION,
                    "Хотите создать новую папку?", ButtonType.NEXT, ButtonType.CANCEL);
            creatDirAlert.showAndWait();
            TextInputDialog dirName = new TextInputDialog("Введите имя");
            dirName.showAndWait();
            String dir = dirName.getEditor().getText();
            String creatDir = serverDir.getText()+"\\"+dir;
            System.out.println(creatDir);
            System.out.println(ClientService.getAuth());
            System.out.println(ClientService.getLogin());
            CreateDirRequest request = new CreateDirRequest(creatDir, ClientService.getAuth(), ClientService.getLogin());
            NettyClient.getChannel().writeAndFlush(request).sync();
//        }
    }

    public void updateServerTable(){
        serverFileInfo.updateList(ClientService.getUpdatePath(), serverTable, serverDir, ClientService.getFileInfoList());
       // new UpdateList(ClientService.getUpdatePath(), serverTable,serverDir, ClientService.getFileInfoList());
    }
    @FXML
    public void clickToClose(ActionEvent actionEvent) {
    }
    @FXML
    public void selectDiskActionServer(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        Path serverPath = Paths.get(element.getSelectionModel().getSelectedItem());
        serverFileInfo.updateList(serverPath, serverTable, serverDir, ClientService.getFileInfoList());

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
    public void btnPathUpActionServer(ActionEvent actionEvent) throws InterruptedException {
        Path serverPath = Paths.get(serverDir.getText()).getParent();
        Path serverPathControl = Paths.get(ClientService.getServerPath()).normalize().toAbsolutePath().getParent();
        System.out.println(serverPath.toString());
        System.out.println(serverPathControl.toString());
        if(!serverPath.equals(serverPathControl)){
                FileListRequest fileListRequest = new FileListRequest(serverPath
                        .toString(),ClientService.getAuth(),ClientService.getLogin());
                ClientService.setUpdatePath(serverPath);
                NettyClient.getChannel().writeAndFlush(fileListRequest).sync();
                //serverFileInfo.updateList(serverPath, serverTable, serverDir, ClientService.getFileInfoList());

        }
    }


}
