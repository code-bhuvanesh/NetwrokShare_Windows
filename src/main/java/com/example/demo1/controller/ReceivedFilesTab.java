package com.example.demo1.controller;

import javafx.fxml.Initializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ReceivedFilesTab implements Initializable {

    int filePort = 6745;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(filePort);
                    Socket client = server.accept();
                    dataOutputStream = new DataOutputStream(client.getOutputStream());
                    dataInputStream = new DataInputStream(client.getInputStream());
                    int totalFiles =  dataInputStream.readInt();
                    for(int i = 0; i < totalFiles; i++){
                        System.out.println("file transfer started for " + i);
                        receiveFile(dataInputStream,dataOutputStream);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void receiveFile(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        System.out.println("file transfer started");
        Long fileSize = dataInputStream.readLong();
        int filenameSize = dataInputStream.readInt();
        byte[] filenameByteArray = new byte[filenameSize];
        dataInputStream.readFully(filenameByteArray,0,filenameSize);
        String fileName = new String(filenameByteArray);

        FileOutputStream out = new FileOutputStream("D:\\New folder\\files\\" + fileName);
        byte[] buffer = new byte[1024 * 1000];

        int count;
        int c = 0;
        while ((count = dataInputStream.read(buffer))> 0){
            System.out.println("getting file " + c++);
            out.write(buffer,0,count);
        }
        out.close();
        System.out.println("file transfer completed");
    }
}
