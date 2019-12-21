package g53sqm.chat.client;

import javafx.application.Platform;

public class ClientPrivateChatController {

    private ClientPrivateChatView privateChat;
    private Client client;
    private ClientDriver driver;

    public void setView(ClientPrivateChatView privateChat){ this.privateChat = privateChat; }

    public void setClient(Client client) { this.client = client; }

    public void setDriver(ClientDriver driver) { this.driver = driver; }

    public void doSend(String pmTarget){
        String msg = privateChat.input.getText();

        Platform.runLater(()-> privateChat.input.clear()); // clear the text area;

        if(msg.trim().length() == 0){
            return ;
        }

        boolean isUserOnline = driver.getOnlineUsers().contains(pmTarget);
        if(isUserOnline){
            client.sendMessage("MESG " + pmTarget + " " + msg);
            appendChat(driver.getUsername() + ": " + msg);
        }
        else {
            appendChat(pmTarget + " is offline.");
            Platform.runLater(()->{
                privateChat.input.setDisable(true);
            });
        }

    }

    public void appendChat(String pmMsg){
        Platform.runLater(()->privateChat.chat.appendText(pmMsg + '\n'));
    }
}