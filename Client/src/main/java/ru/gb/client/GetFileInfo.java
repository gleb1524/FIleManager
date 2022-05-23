package ru.gb.client;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ru.gb.client.net.ClientService;
import ru.gb.client.net.NettyClient;
import ru.gb.dto.FileInfo;
import ru.gb.dto.FileListRequest;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GetFileInfo {
   private TableView<FileInfo> filesTable;
   private TextField address;
   private Path path;
   private List<FileInfo> fileInfos;
    public void updateList(Path updatePath, TableView<FileInfo> tableView, TextField currentAddress, List<FileInfo> fileInfos) {
        new UpdateList(updatePath, tableView, currentAddress, fileInfos);
    }

    public void updateList(Path updatePath, TableView<FileInfo> tableView, TextField currentAddress){
        new UpdateList(updatePath,tableView, currentAddress);
    }

    public GetFileInfo(TableView<FileInfo> filesTable, Path path, TextField address) {
        this.filesTable = filesTable;
        this.address = address;
        this.path = path;

        updateList(path, filesTable, address);

        FileTableBuilder fileTableBuilder = new FileTableBuilder();

        TableColumn<FileInfo, String> fileTypeColumn = fileTableBuilder.getFileTypeColumn();
        TableColumn<FileInfo, String> fileNameColumn = fileTableBuilder.getFileNameColumn();
        TableColumn<FileInfo, String> lastModifiedColumn = fileTableBuilder.getLastModifiedColumn();
        TableColumn<FileInfo, Long> fileSizeColumn = fileTableBuilder.getFileSizeColumn();

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    Path path = Paths.get(address.getText())
                            .resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                    if(Files.isDirectory(path)){
                       new UpdateList(path, filesTable, address);
                    }
                }else if(mouseEvent.getClickCount() == 1){
                    Path path = Paths.get(address.getText())
                            .resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                    if(!Files.isDirectory(path)&&Files.isDirectory(path.getParent())){
                       new UpdateList(path, filesTable, address);
                        address.setText(path.normalize().toString());
                    }else if(!Files.isDirectory(path.getParent())){
                        path = (path.getParent()).getParent().resolve(filesTable
                                .getSelectionModel().getSelectedItem().getFileName());
                        new UpdateList(path, filesTable, address);
                        address.setText(path.normalize().toString());
                    }
                }
            }
        });


        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn,fileSizeColumn, lastModifiedColumn);
    }

    public GetFileInfo(TableView<FileInfo> filesTable, Path path, TextField address, List<FileInfo> fileInfos) {
        this.filesTable = filesTable;
        this.address = address;
        this.path = path;
        this.fileInfos = fileInfos;

        updateList(path, filesTable, address, fileInfos);

        FileTableBuilder fileTableBuilder = new FileTableBuilder();

        TableColumn<FileInfo, String> fileTypeColumn = fileTableBuilder.getFileTypeColumn();
        TableColumn<FileInfo, String> fileNameColumn = fileTableBuilder.getFileNameColumn();
        TableColumn<FileInfo, String> lastModifiedColumn = fileTableBuilder.getLastModifiedColumn();
        TableColumn<FileInfo, Long> fileSizeColumn = fileTableBuilder.getFileSizeColumn();

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    List<FileInfo> fileInfos1 = ClientService.getFileInfoList();
                    Path path = Paths.get(address.getText())
                            .resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                    serverUpdateList(path.toString());
                    ClientService.setUpdatePath(path);
                    System.out.println(path);
//                    if(Files.isDirectory(path)){
//                       new UpdateList(path, filesTable, address, fileInfos1);
//                    }
                }else if(mouseEvent.getClickCount() == 1){
                    Path path = Paths.get(address.getText())
                            .resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                    if(!Files.isDirectory(path)&&Files.isDirectory(path.getParent())){
                       new UpdateList(path, filesTable, address, fileInfos);
                        address.setText(path.normalize().toString());
                    }else if(!Files.isDirectory(path.getParent())){
                        path = (path.getParent()).getParent().resolve(filesTable
                                .getSelectionModel().getSelectedItem().getFileName());
                       new UpdateList(path, filesTable, address);
                        address.setText(path.normalize().toString());
                    }
                }
            }
        });

        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn,fileSizeColumn, lastModifiedColumn);
    }

    public void serverUpdateList(String path){
        FileListRequest fileListRequest = new FileListRequest(path, ClientService.getAuth(), ClientService.getLogin());
        try {
            NettyClient.getChannel().writeAndFlush(fileListRequest).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
