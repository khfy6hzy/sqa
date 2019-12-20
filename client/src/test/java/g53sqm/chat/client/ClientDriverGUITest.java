package g53sqm.chat.client;

import g53sqm.chat.Server;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

import static java.lang.String.valueOf;
import static org.junit.Assert.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class ClientDriverGUITest extends ApplicationTest {

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
        Runnable runnable = new test_runnable();
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
        stage.setAlwaysOnTop(true);
        new ClientDriverGUI().start(stage);
        this.primaryStage = stage;
    }

//    @Test(expected = FxRobotException.class)
//    public void click_doesNotExist(){
//        clickOn("no_such_element");
//    }
//
    @Test
    public void successful_Login() {
            assertEquals("Connect to Chat Room",primaryStage.getTitle());
            clickOn("#serverIpField").write("localhost");
            clickOn("#serverPortField").write(valueOf(test_port_no));
            clickOn("#usernameField").write("test_user");
            clickOn("#connect");
            assertEquals("Public Chat Room",primaryStage.getTitle());

    }
//
//    @Test
//    public void unsuccessful_login_emptyServerIpField(){
//        Text lookup = lookup("#actionTarget").query();
//        clickOn("#connect");
//        assertEquals(lookup.getText(),"Server IP cannot be empty!");
//    }
//
//    @Test
//    public void unsuccessful_login_emptyServerPortField(){
//        Text lookup = lookup("#actionTarget").query();
//        clickOn("#serverIpField").write("localhost");
//        clickOn("#connect");
//        assertEquals(lookup.getText(),"Invalid server port number!");
//    }
//
//    @Test
//    public void unsuccessful_login_emptyUsernameField(){
//        Text lookup = lookup("#actionTarget").query();
//        clickOn("#serverIpField").write("localhost");
//        clickOn("#serverPortField").write(valueOf(test_port_no));
//        clickOn("#connect");
//        assertEquals(lookup.getText(),"Username cannot be empty");
//    }
//
    @Test
    public void unsuccesful_login_wrongConnectionCredentials() {
        Text lookup = lookup("#actionTarget").query();
        clickOn("#serverIpField").write("blabla");
        clickOn("#serverPortField").write("1234");
        clickOn("#usernameField").write("test_user");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(lookup.getText(),"Cannot connect to server!");
    }

    @Test
    public void unsuccesful_login_usernameTaken() {
        Socket user = createMockUsers("test_user_1");
        Text lookup = lookup("#actionTarget").query();
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_1");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Username already exists!", lookup.getText());
        userEnterCommandAndText(user,"QUIT ");
    }

    @Test
    public void succesful_login_usernameNotTaken() {
        Socket user1 = createMockUsers("test_user_1");
        Socket user2 = createMockUsers("test_user_2");
        Text lookup = lookup("#actionTarget").query();
        clickOn("#serverIpField").write("localhost");
        clickOn("#serverPortField").write(valueOf(test_port_no));
        clickOn("#usernameField").write("test_user_3");
        clickOn("#connect");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Public Chat Room",primaryStage.getTitle());
        userEnterCommandAndText(user1,"QUIT ");
        userEnterCommandAndText(user2,"QUIT ");
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

}