package com.example.demo1.controller;

import com.example.demo1.model.ThumbModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

import static com.example.demo1.controller.MainController.thumbImages;

public class PhotosTabController implements Initializable {

    public GridPane gridPane;

    int columns = 0;
    int row = 1;
    private DataInputStream dataInputStream2;
    private DataOutputStream dataOutputStream2;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ScrollPane scrollPane;
    String tmpdir = System.getProperty("java.io.tmpdir")+"tempImages\\";
    private List<String> tempImagesName = new ArrayList<>();

    public void setData(DataOutputStream dataOutputStream,DataInputStream dataInputStream){
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
        getImagesFromDevice(dataInputStream);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File tempDir = new File(tmpdir);
        if(!tempDir.exists()){
            tempDir.mkdir();
        }
        String[] fileNames = new File(tmpdir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg");
            }
        });
        if (fileNames != null) {
            tempImagesName = Arrays.asList(fileNames);
        }
    }


    public void connectToPort2(){
        try {
            ServerSocket serverSocket = new ServerSocket(5647);
            Socket client = serverSocket.accept();
            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();
            dataInputStream2 = new DataInputStream(inputStream);
            dataOutputStream2 = new DataOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getImagesFromDevice(DataInputStream dataInputStream) {
        connectToPort2();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        final double SPEED = 0.0001;
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });
        Thread getImageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int totalImages = dataInputStream.readInt();
                    System.out.println("total images: "+ totalImages);
                    System.out.println(getClass().getResource("ThumbPhoto.fxml"));
                    for(int i = 0; i<totalImages; i++){
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ThumbPhoto.fxml"));
                        VBox vBox = fxmlLoader.load();
                        ThumbController thumbController = fxmlLoader.getController();
//                        System.out.println("current image: "+ i);
                        thumbController.setData(Objects.requireNonNull(getImage(dataInputStream)),dataOutputStream2,dataInputStream2,totalImages);
                        if(columns == 3){
                            columns = 0;
                            row++;
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                gridPane.add(vBox,columns++,row);
//                                scrollPane.setVvalue(1);
                                GridPane.setMargin(vBox,new Insets(25));
                            }
                        });

                    }
                } catch (UnknownError | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        getImageThread.setDaemon(true);
        getImageThread.start();

    }

    int imageId = 0;
    private ThumbModel getImage(DataInputStream dataInputStream){
        try {

            int imageNameSize = dataInputStream.readInt();
            byte[] nameByteArray = new byte[imageNameSize];
            dataInputStream.readFully(nameByteArray,0,imageNameSize);
            String imageName = new String(nameByteArray);
//            System.out.println("image name : " + imageName);
            imageId = dataInputStream.readInt();
            if(tempImagesName.contains(imageName)){
                File imgFile = new File(tmpdir+imageName);
                dataOutputStream.writeInt(0);
                ThumbModel thumbModel = new ThumbModel(imageName,imgFile,imageId);
                thumbImages.add(thumbModel);
                return thumbModel;
            }else{
                dataOutputStream.writeInt(1);
                int imageSize = dataInputStream.readInt();
//                System.out.println("image size: " + imageSize);
                byte[] imageByteArray = new byte[imageSize];
                dataInputStream.readFully(imageByteArray,0,imageSize);
                File imageFile = new File(tmpdir+imageName);
                FileOutputStream fos = new FileOutputStream(imageFile);
                fos.write(imageByteArray);
                fos.close();
                ThumbModel thumbModel = new ThumbModel(imageName,imageFile,imageId);
                thumbImages.add(thumbModel);
                return thumbModel;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
