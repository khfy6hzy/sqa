package g53sqm.chat.client;

import javafx.application.Platform;

public class ClientPrivateChatController {

    private ClientPrivateChatGUI privateChat;
    private Client client;
    private ClientDriverGUI driver;

    public void setView(ClientPrivateChatGUI privateChat){ this.privateChat = privateChat; }

    public void setClient(Client client) { this.client = client; }

    public void setDriver(ClientDriverGUI driver) { this.driver = driver; }

    public void doSend(String pmTarget){
        String msg = privateChat.input.getText();

        Platform.runLater(()-> privateChat.input.clear()); // clear the text area;

        if(msg.trim().length() == 0){
            return ;
        }

        client.sendMessage("MESG " + pmTarget + " " + msg);
        appendChat(msg);
    }

    public void appendChat(String pmMsg){
        Platform.runLater(()->privateChat.chat.appendText(pmMsg + '\n'));
    }
}
