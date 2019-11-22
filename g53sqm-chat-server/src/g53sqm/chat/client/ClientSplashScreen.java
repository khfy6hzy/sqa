package g53sqm.chat.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientSplashScreen extends Application {

    private Stage primaryStage;
    private GridPane gridPane;
    private TextField serverIpInput;
    private TextField serverPortInput;
    private Text actionTarget;
    private Button connect;

    private String serverIp;
    private int serverPort = -1;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Chat Client");

        setupGUI();

        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    private void setupGUI(){
        gridPane = new GridPane();
        gridPane.setPrefSize(500,500);
        gridPane.setPadding(new Insets(25));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label serverIp = new Label("Server IP");
        gridPane.add(serverIp,0,0);

        serverIpInput = new TextField();
        gridPane.add(serverIpInput, 1,0);
        gridPane.getRowConstraints().add(new RowConstraints());

        Label serverPort = new Label("Server Port");
        gridPane.add(serverPort,0,1);

        serverPortInput = new TextField();
        gridPane.add(serverPortInput,1,1);
        gridPane.getRowConstraints().add(new RowConstraints());

        connect = new Button("Connect");
        connect.setOnAction(event -> {
            validateForm();
        });

        HBox container = new HBox(10);
        container.setAlignment(Pos.BOTTOM_RIGHT);
        container.getChildren().add(connect);
        gridPane.add(container,1,3);
        gridPane.getRowConstraints().add(new RowConstraints());

        actionTarget = new Text();
        actionTarget.maxHeight(0);
        gridPane.add(actionTarget,1,2);
        gridPane.getRowConstraints().add(new RowConstraints());
        gridPane.getRowConstraints().get(3).setMaxHeight(0); // hide the actionTarget when not in use , refer https://stackoverflow.com/questions/38301244/creating-a-dynamic-gridpane-add-and-remove-rows


    }

    private void transition(){
        // reset the validation message
        gridPane.getRowConstraints().get(3).setMaxHeight(0);
        actionTarget.setText("");

        ClientGUI cgui = new ClientGUI(serverIp,serverPort);
        Scene scene = cgui.getScene(primaryStage);

        if(cgui.getConnectionStatus()){
            primaryStage.setScene(scene);
        }else{
            gridPane.getRowConstraints().get(3).setMaxHeight(20);
            actionTarget.setText("Failed to connect");
            actionTarget.setFill(Color.FIREBRICK);
            cgui.cleanExit();
            Platform.runLater(()-> connect.setDisable(false));
        }

    }

    private void validateForm() {
        // reset control
        gridPane.getRowConstraints().get(3).setMaxHeight(0);
        actionTarget.setText("");


        serverIp = serverIpInput.getText();

        if(serverIp.length() == 0){
            gridPane.getRowConstraints().get(3).setMaxHeight(20);
            actionTarget.setText("Server IP cannot be left empty!");
            actionTarget.setFill(Color.FIREBRICK);
            return;
        }

        try {
            serverPort = Integer.parseInt(serverPortInput.getText());
        }
        catch(NumberFormatException e) {

            gridPane.getRowConstraints().get(3).setMaxHeight(20);
            actionTarget.setText("Invalid port number");
            actionTarget.setFill(Color.FIREBRICK);
            return;
        }

        Platform.runLater(()-> connect.setDisable(true)); //disable button in the application thread , else will hang

        transition();


    }
}
