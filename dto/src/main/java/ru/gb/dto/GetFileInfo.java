package ru.gb.dto;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class GetFileInfo {
   private TableView<FileInfo> filesTable;
   private TextField address;
   private Path path;
    public void updateList(Path updatePath, TableView<FileInfo> tableView, TextField currentAddress) {
        try {
            Path currentPath = updatePath.normalize().toAbsolutePath();
            currentAddress.setText(currentPath.toString());
            tableView.getItems().clear();
            tableView.getItems().addAll(Files.list(updatePath).map(FileInfo::new).collect(Collectors.toList()));
            tableView.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то неведомой причине не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public GetFileInfo(TableView<FileInfo> filesTable, Path path, TextField address) {
        this.filesTable = filesTable;
        this.address = address;
        this.path = path;

        updateList(path, filesTable, address);

        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn("Type");
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().toString()));
        fileTypeColumn.setPrefWidth(50);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn("Filename");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(100);

        TableColumn<FileInfo, String> lastModifiedColumn = new TableColumn("Last Modified");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        lastModifiedColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()
                .getLastModified().format(dtf)));
        lastModifiedColumn.setPrefWidth(120);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn("Size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> new TableCell<FileInfo, Long>(){
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if(item==null|| empty){
                    setText(null);
                    setStyle("");
                }else{
                    String text = String.format("%,d bytes", item);
                    if(item == -1L){
                        text = "[DIR]";
                    }else if(item >=10_000 && item <= 1_000_000){
                        text = String.format("%d KB", item/1024);
                    }else if(item >=1_000_000){
                        text = String.format("%d MB", item/(1024*1024));
                    }
                    setText(text);
                }
            }
        });
        fileSizeColumn.setPrefWidth(50);

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    Path path = Paths.get(address.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                    if(Files.isDirectory(path)){
                        updateList(path, filesTable, address);
                    }else {
                        address.setText(path.normalize().toString());
                    }
                }
            }
        });

        filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn,fileSizeColumn, lastModifiedColumn);
    }


}
