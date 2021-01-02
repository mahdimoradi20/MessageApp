package ir.mahdi.moradi.messagingapplication.Views;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.tapadoo.alerter.Alerter;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.mahdi.moradi.messagingapplication.APIController.APIClient;
import ir.mahdi.moradi.messagingapplication.APIController.APIInterface;
import ir.mahdi.moradi.messagingapplication.Adapters.MessagesAdapeter;
import ir.mahdi.moradi.messagingapplication.MessagingApp;
import ir.mahdi.moradi.messagingapplication.Models.MessageModel;
import ir.mahdi.moradi.messagingapplication.R;
import ir.mahdi.moradi.messagingapplication.Utils.ConfigData;
import ir.mahdi.moradi.messagingapplication.Utils.ServerConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Chat extends Fragment {

    private String chatID;
    private ArrayList<MessageModel> messages;
    private RotateLoading rotateLoading;
    private TextView txtName ,textMessage;
    private Socket socket;
    private TextView textDescription;
    private RecyclerView chatView;
    private MessagesAdapeter chatAdapter;
    private CircleImageView btnSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        chatView = v.findViewById(R.id.chat_main_chatview);
        rotateLoading = v.findViewById(R.id.chat_progress_loading);
        txtName = v.findViewById(R.id.chat_txt_name);
        textMessage = v.findViewById(R.id.chat_text_message);
        textDescription = v.findViewById(R.id.chat_txt_description);
        btnSend = v.findViewById(R.id.chat_btn_send);
        MessagingApp app = (MessagingApp) getActivity().getApplication();
        socket = app.getSocket();
        SetUpViews();
        return v;
    }

    private void SetUpViews(){
        messages = new ArrayList<>();
        chatAdapter = new MessagesAdapeter(getActivity() , messages);
        chatView.setAdapter(chatAdapter);
        chatView.setLayoutManager(new LinearLayoutManager(getActivity() , RecyclerView.VERTICAL , false));
        chatID = getArguments().getString("chat_id");
        textDescription.setText("شماره چت:" + chatID);
        txtName.setText(getArguments().getString("name"));
        socket.emit("joinchat" , chatID);
        Log.d("TAG", "onCreateView: " + "join the chat" + chatID);
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject object = (JSONObject)args[0];
                    final MessageModel message = new MessageModel();
                    if(object.has("name"))
                        message.setName(object.getString("name"));
                    message.setContent(object.getString("text"));
                    message.setDateTime("17:03");
                    message.setType("recived");
                    if(getActivity() !=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatAdapter.addMessage(message);
                                    chatView.scrollToPosition(messages.size() - 1);
                                }
                            });
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

      btnSend.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if(textMessage.getText().toString().equals("")){
                  return;
              }
              ConfigData cg = new ConfigData(getActivity());
              String str = "{ 'chatid' : 'chat" + chatID + "', 'message' : {'text' : '" + textMessage.getText().toString() +"' , 'name' : '" +  cg.getValue("name" , "def") +"'}}";
              try {
                  JSONObject jsonObject = new JSONObject(str);
                  socket.emit("sendmessage" , jsonObject);
              } catch (JSONException e) {
                  e.printStackTrace();
              }

              APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
              apiInterface.addMessage(ServerConfig.getApiKey() , textMessage.getText().toString(),chatID ,cg.getValue("userid" , "notDefined")).enqueue(new Callback<ResponseBody>() {
                  @Override
                  public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                      try {
                          String res = response.body().string();
                          JSONObject json = new JSONObject(res);
                          if(json.getString("status").equals("ok")){

                          }

                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }

                  @Override
                  public void onFailure(Call<ResponseBody> call, Throwable t) {

                  }
              });

              MessageModel msgTemp = new MessageModel();
              msgTemp.setDateTime("17:50");
              msgTemp.setName(cg.getValue("name" , "def"));
              msgTemp.setContent(textMessage.getText().toString());
              msgTemp.setType("sended");
              chatAdapter.addMessage(msgTemp);
              chatView.scrollToPosition(messages.size() - 1);
              textMessage.setText("");
          }
      });
        LoadDataFromServer();
    }

    private void LoadDataFromServer(){

        messages.clear();
        rotateLoading.start();
        final ConfigData cg = new ConfigData(getActivity());
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        apiInterface.getAllMessages(ServerConfig.getApiKey() , chatID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String res = response.body().string();
                    JSONArray msgs = new JSONArray(res);
                    List<MessageModel> messagesTemp = new ArrayList<>();
                    for(int i = 0 ; i < msgs.length() ; i++){

                        MessageModel msg = new MessageModel();
                        msg.setType(msgs.getJSONObject(i).getString("userid")
                                .equals(cg.getValue("userid" , "0"))? "sended": "reciver");
                        msg.setName(msgs.getJSONObject(i).getString("name"));
                        msg.setContent(msgs.getJSONObject(i).getString("content"));
                        msg.setDateTime("17:50");
                        messagesTemp.add(msg);

                    }
                    chatAdapter.addMessages(messagesTemp);
                    chatView.scrollToPosition(messagesTemp.size()-1);
                    rotateLoading.stop();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                generateAndShowAlterer("خطا" , "خطای برقراری ارتیاط با سرور" , Color.RED);
                rotateLoading.stop();
            }
        });
    }
    private void generateAndShowAlterer(String title , String textBody , int cl){
        Typeface titleFace = Typeface.create("vazirbold.ttf" , Typeface.BOLD);
        Typeface bodyFace  = Typeface.create("vazir.ttf" , Typeface.NORMAL);

        Alerter.create(getActivity()).setTitle(title)
                .setText(textBody)
                .setTitleTypeface(titleFace)
                .setTextTypeface(bodyFace)
                .setBackgroundColorInt(cl)
                .enableSwipeToDismiss()
                .setDuration(3000)
                .setContentGravity(Gravity.RIGHT)
                .enableProgress(true)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        socket.emit("leftchat" , chatID);
        Log.d("TAG", "onDestroyView: " + "left the chat" + chatID);
    }
}
