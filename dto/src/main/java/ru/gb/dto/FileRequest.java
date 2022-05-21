package ru.gb.dto;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRequest implements BasicRequest{
    private String path;
    private String auth;

    public FileRequest(String path,String auth, String login) {
        this.path = path;
        this.auth = auth;
        this.login = login;
    }

    public String getPath() {
        return path;
    }

    public String getAuth() {
        return auth;
    }

    public String getLogin() {
        return login;
    }

    private String login;
    @Override
    public String getType() {
        return "file";
    }
}
