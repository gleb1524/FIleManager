package ru.gb.client.net;

import ru.gb.client.ClientController;
import ru.gb.client.WorkController;
import ru.gb.client.RegController;
import ru.gb.dto.FileInfo;

import java.nio.file.Path;
import java.util.List;

public class ClientService {

   private static RegController regController;
   private static WorkController workController;
   private static ClientController clientController;
   private static String auth;
   private static String serverPath;
   private static String login;
   private static List<FileInfo> fileInfoList;
   private static Path updatePath;

    public static Path getUpdatePath() {
        return updatePath;
    }

    public static void setUpdatePath(Path updatePath) {
        ClientService.updatePath = updatePath;
    }

    public static List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

    public static void setFileInfoList(List<FileInfo> fileInfoList) {
        ClientService.fileInfoList = fileInfoList;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        ClientService.login = login;
    }

    public static String getServerPath() {
        return serverPath;
    }

    public static void setServerPath(String serverPath) {
        ClientService.serverPath = serverPath;
    }

    public static String getAuth() {
        return auth;
    }

    public static void setAuth(String auth) {
        ClientService.auth = auth;
    }

    public static ClientController getClientController() {
        return clientController;
    }

    public static void setClientController(ClientController clientController) {
        ClientService.clientController = clientController;
    }

    public static WorkController getWorkController() {
        return workController;
    }

    public static void setWorkController(WorkController authorizationController) {
        ClientService.workController = authorizationController;
    }

    public static void setRegController(RegController regController) {
        ClientService.regController = regController;
    }

    public static RegController getRegController() {
        return regController;
    }
}
