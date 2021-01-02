package ir.mahdi.moradi.messagingapplication;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MessagingApp extends Application {

    private Socket socket;

    @Override
    public void onCreate() {
        super.onCreate();
        try{
            socket = IO.socket("https://node-mahdi-moradi19.fandogh.cloud");
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket(){
        return socket;
    }
}
