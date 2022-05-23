package ru.gb.dto;

public class FileListRequest implements BasicRequest{
    private String path;
    private String auth;

    public FileListRequest(String path, String auth, String login) {
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
