package g53sqm.chat.client;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ClientGUI{

    private Stage stage;
    private GridPane gridPane;
    private TextArea chat;
    private TextArea input;
    private Client client;
    private ObservableList onlineUsers;
    private ListView online;
    private HBox btmContainer;
    private Button send;
    private Scene scene;

    private ClientGUI ref;
    private ClientSplashScreen splashScreenRef;
    private String ip;
    private int port;

    public ClientGUI(String serverIp, int serverPort){
        ip = serverIp;
        port = serverPort;
    }

    public void init(Stage s) {

        stage = s;

        // setup all components of the application
        setupGUI();

        //finally set up the scene
        scene = new Scene(gridPane);

        ref = this;
        connectServer(ref);

    }

    // initialize the GUI
    private void setupGUI(){

        // base layout of the the chat window
        gridPane = new GridPane();
        gridPane.setPrefSize(525,525);
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(8);
        gridPane.setHgap(8);

        //main text display
        chat = new TextArea();
        chat.setWrapText(true);
        chat.setEditable(false);
        chat.setMouseTransparent(false);
        chat.setFocusTraversable(false);

        //list of online users
        online = new ListView();
        online.setMouseTransparent(true);
        online.setFocusTraversable(false);

        // container for the input text area and send button
        btmContainer = new HBox();

        // input text area
        input = new TextArea();
        input.setPrefRowCount(5);
        input.setWrapText(true);
        //set false so that we can wait until connection is successful
        input.setEditable(false);
        input.setDisable(true);

        //send button
        send = new Button();
        send.setText("Send");
        send.setOnAction(event -> {
            doSend();
        });
        send.setDisable(true);

        //set margins and combine all components into the container
        HBox.setMargin(input,new Insets(5));
        HBox.setMargin(send,new Insets(5));
        HBox.setHgrow(input, Priority.ALWAYS); // so that the input area is responsive
        HBox.setHgrow(send,Priority.NEVER); // the send button does not increase in size
        btmContainer.getChildren().addAll(input,send);

        //set horizontal resizing for columns
        //refer to http://zetcode.com/gui/javafx/layoutpanes/
        ColumnConstraints colCons1 = new ColumnConstraints();
        ColumnConstraints colCons2 = new ColumnConstraints();
        colCons1.setHgrow(Priority.ALWAYS); // allow the main chat area to be responsive
        colCons2.setHgrow(Priority.NEVER); // the online user list does not need to be responsive
        gridPane.getColumnConstraints().addAll(colCons1,colCons2);

        //set vertical resizing for rows
        RowConstraints rowCons1 = new RowConstraints();
        rowCons1.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().add(rowCons1);

        //add in all the elements the the base layout
        gridPane.add(chat, 0,0,1,1);
        gridPane.add(online,1,0,1,1);
        gridPane.add(btmContainer,0,1,2,1);

    }

    private void connectServer(ClientGUI ref){

        client = new Client(ip,port,ref);

        // after successful connection
        Platform.runLater(()->{
            input.setEditable(true);
            input.setDisable(false);
            send.setDisable(false);

            // handle graceful exit when user close window directly
            // TODO what happens if connection fail and I try to close ?
            if(client.getConnectionStatus()){
                stage.setOnCloseRequest(event -> {
                    client.sendMessage("QUIT");
                    client.closeConnection();
                });
            }
            else{
                stage.setOnCloseRequest(event -> {
                    client.closeConnection(); // still need to cleanup here
                });
            }
        });

    }

    private void doSend(){

        String msg = input.getText();

        if(msg.trim().length() == 0){
            return ;
        }

        client.sendMessage(msg);

        if(msg.equals("QUIT")){
            stage.close();
            client.closeConnection();
        }

    }

    // add server response to the chat area
    public void appendChat(String msg){
        Platform.runLater(()->chat.appendText(msg + '\n'));
    }

    public boolean getConnectionStatus(){
        return client.getConnectionStatus();
    }

    public void validateUsername(String username,ClientSplashScreen ref){
        splashScreenRef = ref;
        client.validateUsername(username);
    }

    public void transitionToChatWindow(boolean flag){
        if(flag){
            splashScreenRef.transitionToChatWindow();
        }
        else{
            splashScreenRef.loginError();
        }
    }



    public Scene getScene(){
        return scene;
    }

    public void cleanExit(){
        client.closeConnection();
    }

}
