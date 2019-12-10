package g53sqm.chat.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientDriverGUI extends Application {

    private Client client;
    private ClientSplashGUI splash;
    private ClientSplashController splashController;
    private ClientChatGUI chat;
    private ClientChatController chatController;
    private Stage primaryStage;

    private ObservableList<String> onlineUsers;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        splashController = new ClientSplashController();
        splashController.setDriver(this);
        splash = new ClientSplashGUI(splashController);
        splashController.setView(splash);

        primaryStage.setScene(splash.getScene());
        primaryStage.setMinWidth(525);
        primaryStage.setMinHeight(525);
        primaryStage.show();

        chatController = new ClientChatController();
        chatController.setDriver(this);
        chat = new ClientChatGUI(chatController);
        chatController.setView(chat);

    }


    public static void main(String[] args){
        launch(args);
    }

    public void transition(boolean transition){
        if(transition){

            // start thread to start pinging list of online users
            Thread thread = new Thread(()->{
                while(true){
                    try {
                        splashController.getClient().sendMessage("LIST");
                        Thread.sleep(10);
                    } catch(InterruptedException ie){
                        System.out.println("Online User List Thread error: " + ie.getMessage());
                    }
                }
            });

            thread.setDaemon(true);
            thread.start();

            Platform.runLater(()->{
                chatController.setClient(splashController.getClient());

                primaryStage.setScene(chat.getScene());
            });
        }
        else{
            chatController.clean(); // clean failed login attempts from the chat area

            splashController.getClient().sendMessage("QUIT");
            splashController.getClient().closeConnection();

        }

    }

    public void appendChat(String msg){
        chatController.appendChat(msg);
    }


    public void setSplashErrorMessage(String msg){
        splashController.setErrorMessage(msg);
    }

    public void updateOnlineUsers(String list){
        List<String> userList = new ArrayList<>(Arrays.asList(list.split(", ")));
        onlineUsers = FXCollections.observableArrayList();
        onlineUsers.clear();
        onlineUsers.addAll(userList);
        chatController.updateOnlineUsers(onlineUsers);
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public String getUsername(){
        return splashController.getUsername();
    }

}
