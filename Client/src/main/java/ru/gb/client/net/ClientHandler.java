package ru.gb.client.net;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.gb.client.Client;
import ru.gb.client.ClientController;
import ru.gb.dto.BasicResponse;


public class ClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicResponse response = (BasicResponse) msg;
        String responseText = response.getResponse();
        System.out.println(responseText);

        if("creat_ok".equals(responseText)){
            ClientService.getWorkController().updateServerTable();
        }

        if (responseText.startsWith("server_dir")) {
            String [] token =  responseText.split(" ", 2);
            ClientService.setServerPath(token[1]);
        }
        if("reg_no".equals(responseText)){
            ClientService.getRegController().loginBusy.setVisible(true);
            ClientService.getRegController().name.clear();
            ClientService.getRegController().password.clear();
            ClientService.getRegController().surname.clear();
            ClientService.getRegController().login.clear();
        }
        if("reg_ok".equals(responseText)){
            ClientService.getRegController().registrationComplete.setVisible(true);
            ClientService.getRegController().name.clear();
            ClientService.getRegController().password.clear();
            ClientService.getRegController().surname.clear();
            ClientService.getRegController().login.clear();
        }
        if(responseText.startsWith("auth")){
            if("auth_ok".equals(responseText)){
                Client.setRoot("auth");
            }else if("auth_no".equals(responseText)){
                ClientService.getClientController().textArea.appendText("Invalid login or password");
            }else {
                String [] token =  responseText.split(" ", 2);
                ClientService.setAuth(token[1]);
            }
        }
    }

}
