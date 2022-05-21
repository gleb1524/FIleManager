package ru.gb.dto;

import java.io.Serializable;
import java.nio.file.Path;


public class BasicResponse implements Serializable {
    private String response;
    private String path;

    public String getPath() {
        return path;
    }



    public BasicResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
