package ru.gb.dto;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo implements Serializable {
    public enum FileType{
        FILE("F"), DIRECTORY("D");

        private String name;

        public String getName() {
            return name;
        }

        FileType(String name) {
            this.name = name;
        }
    }

    private String filename;
    private FileType fileType;
    private long size;
    private LocalDateTime lastModified;

    public String getFileName() {
        return filename;
    }

    public void setName(String name) {
        this.filename = name;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setType(FileType type) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public FileInfo(Path path){
        try {
            this.filename = path.getFileName().toString();
            this.size = Files.size(path);
            this.fileType = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if(this.fileType == FileType.DIRECTORY){
                this.size = -1L;
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
        } catch (IOException e) {
           throw new RuntimeException("File not found or invalid");
        }
    }
}
