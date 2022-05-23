package ru.gb.client.net;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.gb.client.Client;
import ru.gb.dto.BasicResponse;
import ru.gb.dto.FileListRequest;
import ru.gb.dto.FileListResponse;

import java.io.IOException;


public class ClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof BasicResponse) {
            BasicResponse response = (BasicResponse) msg;
            String responseText = response.getResponse();
            System.out.println(responseText);

            if ("creat_ok".equals(responseText)) {
                ClientService.getWorkController().updateServerTable();
            }

            if (responseText.startsWith("server_dir")) {
                String[] token = responseText.split(" ", 2);
                ClientService.setServerPath(token[1]);
            }
            if ("reg_no".equals(responseText)) {
                ClientService.getRegController().name.clear();
                ClientService.getRegController().password.clear();
                ClientService.getRegController().surname.clear();
                ClientService.getRegController().login.clear();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "registration failed, try again.", ButtonType.OK);
                    alert.showAndWait();
                });
            }
            if ("reg_ok".equals(responseText)) {
                ClientService.getRegController().name.clear();
                ClientService.getRegController().password.clear();
                ClientService.getRegController().surname.clear();
                ClientService.getRegController().login.clear();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration OK!.", ButtonType.OK);
                    alert.showAndWait();
                });
                Client.setRoot("client");
            }
//            if ("fileList".equals(responseText)) {
//                FileListRequest fileRequest = new FileListRequest(ClientService.getServerPath(), ClientService.getAuth(), ClientService.getLogin());
//                System.out.println(Arrays.toString(ClientService.getFileInfoList().toArray()));
//
//            }

            if (responseText.startsWith("auth")) {
                if (responseText.startsWith("auth_set")) {
                    String[] token = responseText.split(" ", 2);
                    ClientService.setAuth(token[1]);
                    System.out.println(ClientService.getAuth() + " установили auth");
                } else if ("auth_ok".equals(responseText)) {
                    System.out.println(1);
                    FileListRequest fileListRequest = new FileListRequest(ClientService.getServerPath(), ClientService.getAuth(), ClientService.getLogin());
                    try {
                        NettyClient.getChannel().writeAndFlush(fileListRequest).sync();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if ("auth_no".equals(responseText)) {
                    ClientService.getClientController().textArea.appendText("Invalid login or password");
                }
            }
        }else if(msg instanceof FileListResponse){
            FileListResponse response = (FileListResponse) msg;
            System.out.println(response.getFileInfoList().size());
            ClientService.setFileInfoList(response.getFileInfoList());
            System.out.println("Файл лист пришел");
            if(response.getMarker()==0) {
                Platform.runLater(() -> {
                    try {
                        Client.setRoot("auth");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }else{
            System.out.println(ClientService.getFileInfoList().size());
            ClientService.getWorkController().updateServerTable();
            }
        }
    }

}
