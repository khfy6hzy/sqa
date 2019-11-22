package g53sqm.chat.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ClientGUI extends Application {

    private Stage stage;
    private GridPane gridPane;
    private TextArea chat;
    private TextArea input;
    private Client client;
    private ClientGUI gui;
    private ObservableList onlineUsers;
    private ListView online;
    private HBox btmContainer;
    private Button send;


    @Override
    public void start(Stage s){

        stage = s;

        // setup all components of the application
        setupGUI();

        //finally set up the scene
        Scene scene = new Scene(gridPane);
        stage.setScene(scene);
        stage.setMinHeight(500);
        stage.setMinWidth(500);
        stage.show();

        connectServer();
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

    private void connectServer(){

        client = new Client("localhost",9000);

        // after successful connection
        Platform.runLater(()->{
            input.setEditable(true);
            input.setDisable(false);
            send.setDisable(false);
            stage.setOnCloseRequest(event -> {
            });
        });

    }

    private void doSend(){

        String msg = input.getText();

        if(msg.trim().length() == 0){
            return ;
        }

    }

    public static void main(String[] args){
        launch(args);
    }
}
