package g53sqm.chat.client;

import g53sqm.chat.Server;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.NodeQuery;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

import static java.lang.String.valueOf;
import static org.junit.Assert.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class ChatScreenTest extends ApplicationTest {
    private Stage primaryStage;
    private Server test_server;
    private int test_port_no;
    private Thread test_server_thread;

    public class test_runnable implements Runnable{
        public void run() {

            System.out.println("Test server thread started");
            test_server.listen();
        }
    }

    @Before
    public void startServer(){

        test_server = new Server(0);
        test_port_no = test_server.getPortNo();
        Runnable runnable = new ChatScreenTest.test_runnable();
        test_server_thread = new Thread(runnable);

        // Set thread as Daemon to automatically terminate when JVM terminates
        test_server_thread.setDaemon(true);
        test_server_thread.start();


        // Sleep for 1 second for thread execution
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        new ClientDriver().start(stage);
        this.primaryStage = stage;
    }

    @Test
    public void multipleUsers_broadcastMsgExists(){
        Socket user1 = createMockUsers("test_user_1");
        Socket user2 = createMockUsers("test_user_2");

        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        TextArea chat = lookup("#chat").query();
        userEnterCommandAndText(user1,"HAIL Hello world!");
        assertTrue(chat.getText().contains("Broadcast from test_user_1: Hello world!"));
        userEnterCommandAndText(user2,"HAIL Hi guys!");
        assertTrue(chat.getText().contains("Broadcast from test_user_2: Hi guys!"));
        userEnterCommandAndText(user1,"QUIT");
        userEnterCommandAndText(user2,"QUIT");
    }

    @Test
    public void multipleUsersOnline_correctNumberInUserList() {
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        Label numberOnline = lookup("#numberOnline").query();
        assertEquals("Online user(s):1", numberOnline.getText());
        ListView online = lookup("#online").query();
        assertTrue(online.getItems().contains("test_user_3"));

        Socket user1 = createMockUsers("test_user_1");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Online user(s):2", numberOnline.getText());
        assertTrue(online.getItems().contains("test_user_3") && online.getItems().contains("test_user_1"));
        userEnterCommandAndText(user1,"QUIT");

    }

    @Test
    public void singleUser_ableToSendBroadcast() {
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        TextArea input = lookup("#input").query();
        TextArea chat = lookup("#chat").query();
        input.setText("Hello world");
        clickOn("#send");
        assertTrue(chat.getText().contains("Broadcast from test_user_3: Hello world"));

    }

    @Test
    public void ableToSendPrivateMsg() {
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        Socket user1 = createMockUsers("test_user_10");
        WaitForAsyncUtils.waitForFxEvents();
        doubleClickOn(((Node) lookup(hasText("test_user_10")).query()));
        WaitForAsyncUtils.waitForFxEvents();
        interact(()->window("Private chat with test_user_10").requestFocus());
        clickOn("#privateInput").write("Hello world");
        clickOn("#privateSend");
        TextArea privateChat = lookup("#privateChat").query();
        assertTrue(privateChat.getText().contains("test_user_3: Hello world"));

        userEnterCommandAndText(user1,"QUIT");

    }

    @Test
    public void ableToReceivePrivateMsg_fromSingleUser() {
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        Socket user1 = createMockUsers("test_user_10");
        userEnterCommandAndText(user1,"MESG test_user_3 Hello world");
        interact(()->window("Private chat with test_user_10").requestFocus());
        TextArea privateChat = lookup("#privateChat").query();
        System.out.println(privateChat.getText());
        assertTrue(privateChat.getText().contains("test_user_10: Hello world"));

        userEnterCommandAndText(user1,"QUIT");
    }

    @Test
    public void ableToReceivePrivateMsg_fromMultipleUser() {
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        Socket user1 = createMockUsers("test_user_10");
        userEnterCommandAndText(user1,"MESG test_user_3 Hello world");
        interact(()->window("Private chat with test_user_10").requestFocus());
        TextArea privateChat = lookup("#privateChat").query();
        assertTrue(privateChat.getText().contains("test_user_10: Hello world"));

        Socket user2 = createMockUsers("test_user_20");
        userEnterCommandAndText(user2,"MESG test_user_3 Hello world 2");
        interact(()->window("Private chat with test_user_20").requestFocus());
        privateChat = from("Private chat with test_user_20").lookup("#privateChat").query();
        System.out.println(privateChat.getText());
        assertTrue(privateChat.getText().contains("test_user_20: Hello world 2"));

        userEnterCommandAndText(user1,"QUIT");
        userEnterCommandAndText(user2, "QUIT");
    }

    @Test
    public void unableToSendPrivateMsg_userOffline() {
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        Socket user1 = createMockUsers("test_user_10");
        WaitForAsyncUtils.waitForFxEvents();
        doubleClickOn(((Node) lookup(hasText("test_user_10")).query()));
        WaitForAsyncUtils.waitForFxEvents();
        interact(()->window("Private chat with test_user_10").requestFocus());
        clickOn("#privateInput").write("Hello world");
        clickOn("#privateSend");
        TextArea privateChat = lookup("#privateChat").query();
        assertTrue(privateChat.getText().contains("test_user_3: Hello world"));

        userEnterCommandAndText(user1,"QUIT");
        clickOn("#privateInput").write("Hello world");
        clickOn("#privateSend");
        lookup("#privateChat").query();
        assertTrue(privateChat.getText().contains("test_user_10 is offline."));
        TextArea privateInput = lookup("#privateInput").query();
        assertTrue(privateInput.isDisabled());
    }

    @After
    public void afterEachTest() throws TimeoutException {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        test_server.stopListening();

    }

    private Socket createMockUsers(String username){
        Socket user = null;
        try{
            user = new Socket("localhost",test_port_no);
            userEnterCommandAndText(user, "IDEN " + username);
        }catch (IOException e) {
            Assert.fail("Mock user setup failed");
        }
        return user;
    }

    private void userEnterCommandAndText(Socket user, String text){
        try{
            PrintWriter user_out = new PrintWriter(user.getOutputStream(), true);
            user_out.println(text);
            Thread.sleep(1000);
        }catch(IOException|InterruptedException ie) {
            Assert.fail("Failed to send command and text");
        }
    }

    //utility functions to work with multiple windows
    //referenced from https://github.com/TestFX/TestFX-Playground/blob/master/samples/issues/src/test/java/org/testfx/playground/issues/mailinglist_stage_focus_textfield.java
    private NodeQuery from(String stageTitleRegex) {
        return from(rootNodeOf(stageTitleRegex));
    }

    private Node rootNodeOf(String stageTitleRegex) {
        return rootNode((Window) window(stageTitleRegex));
    }

}