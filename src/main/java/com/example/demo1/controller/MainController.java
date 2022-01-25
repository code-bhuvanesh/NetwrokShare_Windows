package com.example.demo1.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import com.example.demo1.model.ThumbModel;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    static int port = 8845;
    public ImageView statusImage;
    public Label statusLabel;
    public Label deviceNameLabel;
    public StackPane mainBox;

    public static List<ThumbModel> thumbImages = new ArrayList<>();

    boolean isConnected = false;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private FXMLLoader photos_tab_loader;
    private Parent photos_tab;
    private FXMLLoader notifications_tab_loader;
    private Parent notifications_tab;
    private FXMLLoader messages_tab_loader;
    private Parent messages_tab;
    private FXMLLoader sendFiles_tab_loader;
    private Parent sendFiles_tab;
    private FXMLLoader receivedFiles_tab_loader;
    private Parent receivedFiles_tab;
    private DataInputStream dataInputStream3;
    private DataOutputStream dataOutputStream3;
    private String mobileName;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        onConnect_click();
        try {
            photos_tab_loader = new FXMLLoader(getClass().getResource("Photos-tab.fxml"));
            photos_tab = photos_tab_loader.load();
            notifications_tab_loader = new FXMLLoader(getClass().getResource("Notifications-tab.fxml"));
            notifications_tab = notifications_tab_loader.load();
            messages_tab_loader = new FXMLLoader(getClass().getResource("Messages-tab.fxml"));
            messages_tab = messages_tab_loader.load();
            sendFiles_tab_loader = new FXMLLoader(getClass().getResource("SendFiles-tab.fxml"));
            sendFiles_tab = sendFiles_tab_loader.load();
            receivedFiles_tab_loader = new FXMLLoader(getClass().getResource("ReceivedFiles-tab.fxml"));
            receivedFiles_tab = receivedFiles_tab_loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void updateText(Label textView, String text) {
        textView.setText(text);
    }


    public void onConnect_click() {
//        statusLabel.setText("not connected");
        Thread connectThread = new Thread(new Runnable() {
            int count = 0;

            @Override
            public void run() {
                try {

                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            updateText(statusLabel, "Connected");
                        }
                    };
                    //connecting to device
                    ServerSocket serverSocket;
                    serverSocket = new ServerSocket(port);
                    System.out.println("waiting");
                    Socket client = serverSocket.accept();
                    isConnected = false;
                    System.out.println("connected");
                    Platform.runLater(updater);
                    InputStream inputStream = client.getInputStream();
                    OutputStream outputStream= client.getOutputStream();
                    dataOutputStream = new DataOutputStream(outputStream);
                    dataInputStream = new DataInputStream(inputStream);
                    int mobileNameSize = dataInputStream.readInt();
                    byte[] mobileNameArray = new byte[mobileNameSize];
                    dataInputStream.readFully(mobileNameArray,0,mobileNameSize);
                    mobileName = new String(mobileNameArray);
                    String myDeviceName = InetAddress.getLocalHost().getHostName();
                    byte [] myDeviceNameArray = myDeviceName.getBytes();
                    dataOutputStream.writeInt(myDeviceNameArray.length);
                    dataOutputStream.write(myDeviceNameArray,0,myDeviceNameArray.length);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Image connectedImage = new Image("D:\\Projects\\Java projects\\demo1\\src\\images\\status_connected_icon.png");
                            statusLabel.setText("Connected : ");
                            statusImage.setImage(connectedImage);
                            deviceNameLabel.setText(mobileName);
                            mainBox.getChildren().removeAll();
                            mainBox.getChildren().setAll(photos_tab);
                        }
                    });

                    PhotosTabController photosTabController = photos_tab_loader.getController();
                    photosTabController.setData(dataOutputStream,dataInputStream);
                    //received_msg(dataInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            void changeText(Label label, String text){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        label.setText(text);
                    }
                });
            }

            private void received_msg(DataInputStream dataInputStream) {
                Thread receiveThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            while(true){
                                count++;
//                                Platform.runLater(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        receive_check.setText("receiving" + count);
//                                    }
//                                });

                                //receiving file
                                int fileNameSize = dataInputStream.readInt();
                                System.out.println("file name size received");
                                byte[] filenameInBytes = new byte[fileNameSize];
                                dataInputStream.readFully(filenameInBytes,0,fileNameSize);
                                String fileName = new String(filenameInBytes);
                                System.out.println("file name received : " + fileName);
                                int fileSize = dataInputStream.readInt();
                                System.out.println("file size received " + fileSize);
                                byte[] fileInBytes = new byte[fileSize];
                                dataInputStream.readFully(fileInBytes,0,fileSize);
                                System.out.println("file received");

                                //converting byteArray to file and saving
                                File file = new File("D:\\New folder\\"+fileName);
                                System.out.println("received 1 : " + fileName);
                                FileOutputStream fos = new FileOutputStream(file);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                DataOutputStream dos = new DataOutputStream(bos);
                                System.out.println("received 2 : " + fileName);
                                dos.write(fileInBytes);
                                System.out.println("received 3  : " + fileName);
                                fos.close();
                                System.out.println("received 4: " + fileName);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
                receiveThread.setDaemon(true);
                receiveThread.start();
            }
        });
        connectThread.setDaemon(true);
        connectThread.start();

    }

    public void connectPortForFiles(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(5467);
                    Socket client = serverSocket.accept();
                    dataInputStream3 = new DataInputStream(client.getInputStream());
                    dataOutputStream3 = new DataOutputStream(client.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void photos_tab(ActionEvent event){
        System.out.println("photos tab");
        mainBox.getChildren().removeAll();
        mainBox.getChildren().setAll(photos_tab);
    }
    public void notifications_tab(ActionEvent event){
        System.out.println("notification tab");
        mainBox.getChildren().removeAll();
        mainBox.getChildren().setAll(notifications_tab);
        NotificationsTab notificationsTab = notifications_tab_loader.getController();
    }
    public void messages_tab(ActionEvent event){
        System.out.println("messages tab");
        mainBox.getChildren().removeAll();
        mainBox.getChildren().setAll(messages_tab);
        MessagesTab messagesTab = messages_tab_loader.getController();
    }
    public void sendFiles_tab(ActionEvent event){
        System.out.println("Send Files tab");
        mainBox.getChildren().removeAll();
        mainBox.getChildren().setAll(sendFiles_tab);
        SendFilesTab sendFilesTab = sendFiles_tab_loader.getController();
        sendFilesTab.setData(dataInputStream3,dataOutputStream3);
    }
    public void receivedFiles_tab(ActionEvent event){
        System.out.println("Received Files tab");
        mainBox.getChildren().removeAll();
        mainBox.getChildren().setAll(receivedFiles_tab);
        ReceivedFilesTab receivedFilesTab = receivedFiles_tab_loader.getController();
    }



}

