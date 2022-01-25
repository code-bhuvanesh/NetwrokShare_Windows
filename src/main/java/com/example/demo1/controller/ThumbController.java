package com.example.demo1.controller;

import com.example.demo1.MainApplication;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import com.example.demo1.model.ThumbModel;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;

public class ThumbController {
    public VBox box;
    public ImageView imageView;
    int imageId;
    int maxImg;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void setData(ThumbModel thumbModel,DataOutputStream dataOutputStream, DataInputStream dataInputStream, int maxImg){
        this.maxImg = maxImg;
        Image image = thumbModel.getImage();
        imageId = thumbModel.getImageId();
        imageView.setImage(image);
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
//        System.out.println("image loaded");

        //image  context menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("open");
        contextMenu.setPrefWidth(10);
        MenuItem item2 = new MenuItem("save");
        contextMenu.getItems().addAll(item1,item2);
        item1.setOnAction(actionEvent -> {
            openScene(actionEvent,imageId);
        });
        item2.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg", "*.jpg*"));
            File documentsDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());
            if(!documentsDir.exists()){
                documentsDir.mkdir();
            }
            fileChooser.setInitialFileName(MainController.thumbImages.get(imageId).getImageName());
            fileChooser.setInitialDirectory(documentsDir);
            File selectedFile = fileChooser.showSaveDialog(new Stage());
            if(selectedFile != null){

                saveImage(selectedFile,imageId,dataOutputStream,dataInputStream);
            }
        });

        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                //left click
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    openScene(mouseEvent,imageId);
                }else{
                    item1.setOnAction(actionEvent -> openScene(mouseEvent,imageId));
                    contextMenu.show(imageView,mouseEvent.getScreenX(),mouseEvent.getScreenY());
                }
            }

        });


    }

    private void openScene(Event event, int imageId){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("full-image-view.fxml"));
            BorderPane root = fxmlLoader.load();
            Scene mainScene =  ((Node)event.getSource()).getScene();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            FullImageViewController fullImageViewController = fxmlLoader.getController();
            fullImageViewController.setData(dataInputStream,dataOutputStream,imageId,maxImg,mainScene);
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImage(File saveFile, int imageId, DataOutputStream dataOutputStream, DataInputStream dataInputStream){
        System.out.println("image id" + imageId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataOutputStream.writeInt(imageId);
                    int imageSize = dataInputStream.readInt();
                    String imageName = MainController.thumbImages.get(imageId).getImageName();
                    byte[] imageBytearray = new byte[imageSize];
                    dataInputStream.readFully(imageBytearray, 0, imageSize);
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    DataOutputStream dos = new DataOutputStream(bos);
                    dos.write(imageBytearray);
                    fos.close();
                    System.out.println("image saved");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(saveFile.getParent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
