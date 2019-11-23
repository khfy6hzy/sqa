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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientSplashScreen extends Application {

    private Stage primaryStage;
    private GridPane splashScreenGridPane;
    private GridPane usernameScreenGridPane;
    private TextField serverIpInput;
    private TextField serverPortInput;
    private TextField usernameInput;
    private Text actionTarget;
    private Text usernameActionTarget;
    private Button connect;
    private Button login;

    private ClientGUI cgui;
    private ClientSplashScreen ref;
    private String serverIp;
    private int serverPort = -1;
    private String username;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.ref = this;

        primaryStage.setTitle("Chat Client");

        setupSplashScreen();

        Scene scene = new Scene(splashScreenGridPane);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    private void setupSplashScreen(){
        splashScreenGridPane = new GridPane();
        splashScreenGridPane.setPrefSize(500,500);
        splashScreenGridPane.setPadding(new Insets(25));
        splashScreenGridPane.setAlignment(Pos.CENTER);
        splashScreenGridPane.setHgap(10);
        splashScreenGridPane.setVgap(10);

        Label serverIp = new Label("Server IP");
        splashScreenGridPane.add(serverIp,0,0);

        serverIpInput = new TextField();
        splashScreenGridPane.add(serverIpInput, 1,0);
        splashScreenGridPane.getRowConstraints().add(new RowConstraints());

        Label serverPort = new Label("Server Port");
        splashScreenGridPane.add(serverPort,0,1);

        serverPortInput = new TextField();
        splashScreenGridPane.add(serverPortInput,1,1);
        splashScreenGridPane.getRowConstraints().add(new RowConstraints());

        connect = new Button("Connect");
        connect.setOnAction(event -> {
            validateSplashForm();
        });

        HBox container = new HBox(10);
        container.setAlignment(Pos.BOTTOM_RIGHT);
        container.getChildren().add(connect);
        splashScreenGridPane.add(container,1,3);
        splashScreenGridPane.getRowConstraints().add(new RowConstraints());

        actionTarget = new Text();
        actionTarget.maxHeight(0);
        splashScreenGridPane.add(actionTarget,1,2);
        splashScreenGridPane.getRowConstraints().add(new RowConstraints());
        splashScreenGridPane.getRowConstraints().get(3).setMaxHeight(0); // hide the actionTarget when not in use , refer https://stackoverflow.com/questions/38301244/creating-a-dynamic-gridpane-add-and-remove-rows


    }

    private void setupUsernameScreen() {
        usernameScreenGridPane = new GridPane();
        usernameScreenGridPane.setPrefSize(500,500);
        usernameScreenGridPane.setPadding(new Insets(25));
        usernameScreenGridPane.setAlignment(Pos.CENTER);
        usernameScreenGridPane.setHgap(10);
        usernameScreenGridPane.setVgap(10);

        Label username = new Label("Username");
        usernameScreenGridPane.add(username,0,0);

        usernameInput = new TextField();
        usernameScreenGridPane.add(usernameInput, 1,0);
        usernameScreenGridPane.getRowConstraints().add(new RowConstraints());

        login = new Button("Login");
        login.setOnAction(event -> {
            validateLoginForm();
        });

        HBox container = new HBox(10);
        container.setAlignment(Pos.BOTTOM_RIGHT);
        container.getChildren().add(login);
        usernameScreenGridPane.add(container,1,3);
        usernameScreenGridPane.getRowConstraints().add(new RowConstraints());

        usernameActionTarget = new Text();
        usernameActionTarget.maxHeight(0);
        usernameScreenGridPane.add(actionTarget,1,2);
        usernameScreenGridPane.getRowConstraints().add(new RowConstraints());
        usernameScreenGridPane.getRowConstraints().get(2).setMaxHeight(0); // hide the actionTarget when not in use , refer https://stackoverflow.com/questions/38301244/creating-a-dynamic-gridpane-add-and-remove-rows

    }

    private void validateSplashForm() {
        // reset control
        splashScreenGridPane.getRowConstraints().get(3).setMaxHeight(0);
        actionTarget.setText("");


        serverIp = serverIpInput.getText();

        if(serverIp.length() == 0){
            splashScreenGridPane.getRowConstraints().get(3).setMaxHeight(20);
            actionTarget.setText("Server IP cannot be left empty!");
            actionTarget.setFill(Color.FIREBRICK);
            return;
        }

        try {
            serverPort = Integer.parseInt(serverPortInput.getText());
        }
        catch(NumberFormatException e) {

            splashScreenGridPane.getRowConstraints().get(3).setMaxHeight(20);
            actionTarget.setText("Invalid port number");
            actionTarget.setFill(Color.FIREBRICK);
            return;
        }

        Platform.runLater(()-> connect.setDisable(true)); //disable button in the application thread , else will hang

        transitionToUsernameWindow();

    }

    private void validateLoginForm() {
        // reset control
        usernameScreenGridPane.getRowConstraints().get(2).setMaxHeight(0);
        actionTarget.setText("");

        username = usernameInput.getText();

        if(username.length() == 0) {
            usernameScreenGridPane.getRowConstraints().get(2).setMaxHeight(20);
            actionTarget.setText("Username cannot be empty");
            actionTarget.setFill(Color.FIREBRICK);
            return;
        }

        cgui.validateUsername(username,ref);
    }

    public void loginError(){
        Platform.runLater(()->{
            usernameScreenGridPane.getRowConstraints().get(2).setMaxHeight(20);
            actionTarget.setText("Username is taken!");
            actionTarget.setFill(Color.FIREBRICK);
        });
    }

    private void transitionToUsernameWindow(){
        // reset the validation message
        splashScreenGridPane.getRowConstraints().get(3).setMaxHeight(0);
        actionTarget.setText("");

        cgui = new ClientGUI(serverIp,serverPort);
        cgui.init(primaryStage);

        if(cgui.getConnectionStatus()){
            setupUsernameScreen();
            Platform.runLater(()->{
                Scene scene = new Scene(usernameScreenGridPane);
                primaryStage.setScene(scene);
            });
        }else{
            Platform.runLater(()->{
                splashScreenGridPane.getRowConstraints().get(3).setMaxHeight(20);
                actionTarget.setText("Failed to connect");
                actionTarget.setFill(Color.FIREBRICK);
                connect.setDisable(false);
            });
            cgui.cleanExit();
        }

    }

    public void transitionToChatWindow(){
        Platform.runLater(()->{
            // reset the validation message
            splashScreenGridPane.getRowConstraints().get(2).setMaxHeight(0);
            actionTarget.setText("");

            Scene scene = cgui.getScene();
            primaryStage.setScene(scene);
        });
    }
}
