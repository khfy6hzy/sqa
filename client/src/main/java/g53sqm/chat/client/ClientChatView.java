package g53sqm.chat.client;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class ClientChatView {

    GridPane gridPane;
    TextArea chat;
    TextArea input;
    Label numberOnline;
    ListView online;
    HBox btmContainer;
    VBox rightContainer;
    Button send;

    private ClientChatController controller;

    public ClientChatView(ClientChatController controller){
        this.controller = controller;
        init();
    }

    private void init(){

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
        chat.setId("chat");

        // container for the number of online users and list of online users
        rightContainer = new VBox();

        // number of online users
        numberOnline = new Label();
        numberOnline.setLabelFor(online);
        numberOnline.setText("Online user(s): ");
        numberOnline.setId("numberOnline");

        //list of online users
        online = new ListView();
        online.setMouseTransparent(false);
        online.setFocusTraversable(false);
        online.setId("online");
        online.setOnMouseClicked((event)-> {
            if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount()==2){
                controller.openPrivateChatWindow();
            }
        });

        VBox.setVgrow(numberOnline,Priority.NEVER);
        VBox.setVgrow(online,Priority.ALWAYS);
        rightContainer.getChildren().addAll(numberOnline,online);

        // container for the input text area and send button
        btmContainer = new HBox();

        // input text area
        input = new TextArea();
        input.setPrefRowCount(5);
        input.setWrapText(true);
        input.setId("input");

        //send button
        send = new Button();
        send.setText("Send");
        send.setOnAction(event -> {
            controller.doSend();
        });
        send.setId("send");

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
        gridPane.add(rightContainer,1,0,1,1);
        gridPane.add(btmContainer,0,1,2,1);
    }

    public Scene getScene(){
        return new Scene(gridPane);
    }

}
