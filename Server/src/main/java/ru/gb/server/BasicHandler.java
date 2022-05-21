package ru.gb.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.gb.dto.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class BasicHandler extends ChannelInboundHandlerAdapter {

    DataBaseService dataBaseService = new DataBaseService();
    private  String login;
    private  String password;
    private  String auth;
    private final String SER_DIR = ".";
    File file;
    private long sizefile;
    private int byteRead;
    private int start = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {



        if (msg instanceof FileRequest) {
            FileRequest request = (FileRequest) msg;
            if (dataBaseService.hasAuthRegister(request.getAuth(), request.getLogin())) {
                System.out.println(request.getPath());
                Path path = Paths.get(request.getPath());
                if (!Files.exists(path)) {
                    ctx.writeAndFlush(new BasicResponse("creat_ok"));
                    Files.createDirectory(path);
                }
            }
        }else if (msg instanceof RegRequest) {
                RegRequest request = (RegRequest) msg;
                if (!dataBaseService.hasRegistration(request.getLogin())) {
                    dataBaseService.creatRegistration(request.getLogin(), request.getPassword(),
                            request.getName(), request.getSurname());
                    ctx.writeAndFlush(new BasicResponse("reg_ok"));
                } else {
                    ctx.writeAndFlush(new BasicResponse("reg_no"));
                }
            }  if (msg instanceof AuthRequest) {
                AuthRequest request = (AuthRequest) msg;
                login = request.getLogin();
                password = request.getPassword();
                auth = dataBaseService.creatAuth(request.getLogin(), request.getPassword());
                if (!dataBaseService.isAuthRegister(auth, login)) {
                    dataBaseService.authRegister(auth, login);
                }
                ctx.writeAndFlush(new BasicResponse("auth " + auth));
                System.out.println(auth);
                if (dataBaseService.hasAuth(login, password)) {
                    String filename = SER_DIR + "\\" + login + "root";
                    Path path = Paths.get(filename);
                    if (!Files.exists(path)) {
                        Files.createDirectory(path);
                        System.out.println("New Directory created !   " + filename);
                    } else {
                        System.out.println("Directory already exists");
                    }
                    ctx.writeAndFlush(new BasicResponse("server_dir " + filename));
                    ctx.writeAndFlush(new BasicResponse("auth_ok"));
                } else {
                    ctx.writeAndFlush(new BasicResponse("auth_no"));
                }
            }else if (msg instanceof UploadRequest) {
                UploadRequest uploadRequest = (UploadRequest) msg;
                this.file = new File(uploadRequest.getRemPath() + "\\" + uploadRequest.getFilename());
                 System.out.println(uploadRequest.getSize()+" размер файла");
                 sizefile = uploadRequest.getSize();
                 byteRead = uploadRequest.getByteRead();

                  try (RandomAccessFile fos = new RandomAccessFile(file, "rw")) {

                         fos.seek(start);
                         fos.write(uploadRequest.getData());
                         start = start+byteRead;
                         System.out.println(start);
                         if(sizefile-start==0){
                             start = 0;
                         }



//                     System.out.println(Files.size(file.toPath())+"записалось");
//                     System.out.println(sizefile - start + " осталось записать");
//                     System.out.println(uploadRequest.getData()[0]);
//                     System.out.println(uploadRequest.getData()[1]);
//                     System.out.println(uploadRequest.getData()[2]);
//                     System.out.println(uploadRequest.getData()[3]);
//                     System.out.println(uploadRequest.getData()[uploadRequest.getData().length/1_000_000]);
//                     System.out.println(uploadRequest.getData()[uploadRequest.getData().length/2_000_000]);
//                     System.out.println(uploadRequest.getData()[uploadRequest.getData().length/3_000_000]);


                 } catch (IOException e) {
                      throw new RuntimeException(e);
                }

          }//else if(msg instanceof byte[]){
//            System.out.println("что-то прищло");
//            byte[] b = (byte[]) msg;
//
//            try (RandomAccessFile fos = new RandomAccessFile("C:\\Users\\gleb1\\Desktop\\FileManager\\asdroot\\2\\lesson5.mp4", "rw")) {
//                if(Files.size(Paths.get("C:\\Users\\gleb1\\Desktop\\FileManager\\asdroot\\2\\lesson5.mp4"))<1_000_000){
//                    fos.setLength(sizefile);
//                    fos.write(b);
//                }else{
//                    fos.write(b,1_000_000,1_000_000);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
