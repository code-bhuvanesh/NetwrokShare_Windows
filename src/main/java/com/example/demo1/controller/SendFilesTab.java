package com.example.demo1.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SendFilesTab {
    public StackPane fileViewer;
    public Button clearAllBtn;
    public Button sendFilesBtn;
    public Button selectFilesBtn;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void setData(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }


    public void selectFiles(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg", "*.jpg*"));
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(new Stage());
        if(selectedFile != null){
            selectedFile.forEach(file -> {
                try {
                    byte[] nameByteArray = file.getName().getBytes();
                    int nameArraySize = nameByteArray.length;
                    byte[] fileByteArray = Files.readAllBytes(file.toPath());
                    int fileSize = (int) file.length();
                    dataOutputStream.writeInt(nameArraySize);
                    dataOutputStream.write(nameByteArray,0,nameArraySize);
                    dataOutputStream.writeInt(fileSize);
                    dataOutputStream.write(fileByteArray,0,fileSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
