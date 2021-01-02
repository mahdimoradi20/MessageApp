package ir.mahdi.moradi.messagingapplication.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;
import java.util.List;

import ir.mahdi.moradi.messagingapplication.Adapters.ChatDialogAdapter;
import ir.mahdi.moradi.messagingapplication.MessagingApp;
import ir.mahdi.moradi.messagingapplication.Models.ChatDialog;
import ir.mahdi.moradi.messagingapplication.R;

public class MainActivity extends AppCompatActivity {

    private Socket socket;

    @Override
    protected void onStart() {
        super.onStart();
        MessagingApp app = (MessagingApp)getApplication();
        socket = app.getSocket();
        socket.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        socket.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

}
