package g53sqm.chat.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientPrivateChatView {

    GridPane gridPane;
    TextArea chat;
    TextArea input;
    HBox btmContainer;
    Button send;
    Stage stage;

    private ClientPrivateChatController controller;
    private String pmTarget;

    public ClientPrivateChatView(ClientPrivateChatController controller, String pmTarget){
        this.controller = controller;
        this.pmTarget = pmTarget;
        init();
    }

    private void init(){
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
        chat.setId("privateChat");

        // container for the input text area and send button
        btmContainer = new HBox();

        // input text area
        input = new TextArea();
        input.setPrefRowCount(5);
        input.setWrapText(true);
        input.setId("privateInput");

        //send button
        send = new Button();
        send.setText("Send");
        send.setOnAction(event -> {
            controller.doSend(pmTarget);
        });
        send.prefWidth(100);
        send.setId("privateSend");

        //set margins and combine all components into the container
        HBox.setMargin(input,new Insets(5));
        HBox.setMargin(send,new Insets(5));
        HBox.setHgrow(input, Priority.ALWAYS); // so that the input area is responsive
        HBox.setHgrow(send,Priority.NEVER); // the send button does not increase in size
        btmContainer.getChildren().addAll(input,send);

        //set horizontal resizing for columns
        //refer to http://zetcode.com/gui/javafx/layoutpanes/
        ColumnConstraints colCons1 = new ColumnConstraints();
        colCons1.setHgrow(Priority.ALWAYS); // allow the main chat area to be responsive
        gridPane.getColumnConstraints().addAll(colCons1);

        //set vertical resizing for rows
        RowConstraints rowCons1 = new RowConstraints();
        rowCons1.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().add(rowCons1);

        //add in all the elements the the base layout
        gridPane.add(chat, 0,0,1,1);
        gridPane.add(btmContainer,0,1,1,1);

        stage = new Stage();

        Platform.runLater(() -> {
            stage.setTitle("Private chat with " + pmTarget);
            stage.setScene(new Scene(gridPane));
            stage.initModality(Modality.NONE);
            stage.initStyle(StageStyle.DECORATED);
            stage.show();
        });
    }

    public Stage getStage(){
        return stage;
    }
}