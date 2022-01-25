package com.example.demo1.model;

import javafx.scene.image.Image;

import java.io.File;

public class ThumbModel {
    private File imageFile;
    private String imageName;
    private int imageId;

    public ThumbModel(String imageName, File imageFile, int imageId) {
        this.imageName = imageName;
        this.imageFile = imageFile;
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public Image getImage() {
       Image image = new Image(imageFile.toURI().toString());
        return image;
    }

    public int getImageId() {
        return imageId;
    }


}
