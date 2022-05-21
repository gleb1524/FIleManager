package ru.gb.dto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class UploadRequest implements BasicRequest{

   private File file;
   private String filename;
   byte [] data;
   byte [] lustDataPac;
   private int packageCount;

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public byte[] getLustDataPac() {
        return lustDataPac;
    }

    public void setLustDataPac(byte[] lustDataPac) {
        this.lustDataPac = lustDataPac;
    }

    private String remPath;
   private long size;
   private int byteRead;
   private int lustPac;

    public int getLustPac() {
        return lustPac;
    }

    public void setLustPac(int lustPac) {
        this.lustPac = lustPac;
    }

    public int getByteRead() {
        return byteRead;
    }

    public void setByteRead(int byteRead) {
        this.byteRead = byteRead;
    }

    public long getSize() {
        return size;
    }

    private static final int MB_8 = 4_000_000;
    public UploadRequest(String path, String remPath) {
        this.remPath = remPath;
        this.file=new File(path);
        this.filename = file.getName();
        try {
            this.size = Files.size(Paths.get(path));
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        try(FileInputStream fileInputStream = new FileInputStream(file)){
//            data = fileInputStream.readAllBytes();
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void setData(byte[] data){
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }
    public byte[] getData()  {
        return data;
    }

    public File getFile() {
        return file;
    }


    public String getRemPath() {
        return remPath;
    }
    @Override
    public String getType() {
        return null;
    }

//    byte[] filePart = new byte[MB_8];
//    public void saw(String path, Consumer<byte[]> filePartConsumer) {
//
//        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(path).toFile())) {
//            while (fileInputStream.read(filePart) != -1) {
//                filePartConsumer.accept(filePart);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
