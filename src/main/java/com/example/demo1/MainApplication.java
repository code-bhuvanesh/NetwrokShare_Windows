package com.example.demo1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    public static Class mainClass;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        mainClass = getClass();
        System.out.println("location" + getClass().getResource("Main-view.fxml"));
        fxmlLoader.setLocation(getClass().getResource("Main-view.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        Scene scene = new Scene(borderPane);
        stage.setTitle("Network Share");
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }


}
