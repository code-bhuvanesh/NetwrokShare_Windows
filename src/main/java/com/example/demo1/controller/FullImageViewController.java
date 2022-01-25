package com.example.demo1.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class FullImageViewController {

    public Label imageNameLLabel;
    public Button backButton;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Scene mainScene;

    public ImageView fillImageView;
    public Button closeBtn;
    public int currentImageId = 0;
    public Button l_arrow;
    public Button r_arrow;

    private ArrayList<String> fileNameList = new ArrayList();

    private int maxImg;

    public void setData(DataInputStream dataInputStream, DataOutputStream dataOutputStream, int imageId, int maxImg, Scene mainScene) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.mainScene = mainScene;
        currentImageId = imageId;
        getImage(currentImageId);


        this.maxImg = maxImg;
        System.out.println("max image " + maxImg);

    }

    public void getImage(int imageId) {
        currentImageId = imageId;
        System.out.println("image id" + imageId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fillImageView.setImage(MainController.thumbImages.get(imageId).getImage());
                    centerImage(fillImageView);
                    dataOutputStream.writeInt(currentImageId);
                    int imageSize = dataInputStream.readInt();
//                    int imageNameSize = dataInputStream.readInt();
//                    byte[] imageNameBytearray = new byte[imageNameSize];
//                    dataInputStream.readFully(imageNameBytearray,0,imageNameSize);
                    String imageName = MainController.thumbImages.get(imageId).getImageName();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            imageNameLLabel.setText(imageName);
                        }
                    });

//                    System.out.println("image size " + imageSize);
                    byte[] imageBytearray = new byte[imageSize];
                    dataInputStream.readFully(imageBytearray, 0, imageSize);
                    fileNameList.add(imageName);
                    File imageFile = new File("D:\\New folder\\" + imageName);
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    DataOutputStream dos = new DataOutputStream(bos);
                    dos.write(imageBytearray);
                    fos.close();
                    File imageFile1 = new File("D:\\New folder\\" + imageName);
                    Image image = new Image(imageFile1.toURI().toString());
                    fillImageView.setImage(image);
                    centerImage(fillImageView);


                    System.out.println("received image");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



    public void centerImage(ImageView imageView) {
        Image img = imageView.getImage();

        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);

        }
    }

    public void l_arrow_click(ActionEvent actionEvent){
        System.out.println("l");
        if(currentImageId >0){
            currentImageId--;
        }
        getImage(currentImageId);
    }

    public void r_arrow_click(ActionEvent actionEvent){
        System.out.println("r");
        if(currentImageId < maxImg-1){
            currentImageId++;
        }
        getImage(currentImageId);
    }

    public void onBackPressed(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
//        stage.show();
    }
}
