package ir.mahdi.moradi.messagingapplication.Views;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.tapadoo.alerter.Alerter;

import org.json.JSONObject;

import java.io.IOException;

import ir.mahdi.moradi.messagingapplication.APIController.APIClient;
import ir.mahdi.moradi.messagingapplication.APIController.APIInterface;
import ir.mahdi.moradi.messagingapplication.R;
import ir.mahdi.moradi.messagingapplication.Utils.ConfigData;
import ir.mahdi.moradi.messagingapplication.Utils.ServerConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddCreateChat extends Fragment {

    private TextInputEditText textName , textChatID;
    private Button btnRegister , btnJoin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_create_chat, container, false);
        textName = v.findViewById(R.id.addchat_text_room_name);
        textChatID = v.findViewById(R.id.addchat_text_room_join);
        btnRegister = v.findViewById(R.id.addchat_btn_add);
        btnJoin = v.findViewById(R.id.adchat_btn_join);
        SetUpViews();
        return v;
    }

    private void SetUpViews(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                apiInterface.addChat(ServerConfig.getApiKey() , textName.getText().toString(),"nothing").enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String res = response.body().string();
                            JSONObject json = new JSONObject(res);
                            if(json.getString("status").equals("ok")){
                                generateAndShowAlterer("موفق" , "شماره چت:" + json.getString("chatid") , Color.BLUE);
                            }else
                                generateAndShowAlterer("خطا" , "خطای برقراری ارتباط با سرور" , Color.RED);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                            generateAndShowAlterer("خطا" , "خطای برقراری ارتباط با سرور" , Color.RED);
                    }

                });
            }
        });
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigData cg = new ConfigData(getActivity());
                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                apiInterface.addUserChat(ServerConfig.getApiKey() , cg.getValue("userid" , "notDefined") ,
                        textChatID.getText().toString()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            String res = response.body().string();
                            JSONObject jsonObject = new JSONObject(res);
                            if(jsonObject.getString("status").equals("ok")){
                                generateAndShowAlterer("موفق" , "با موفقیت به چت اضافه شده اید" , Color.BLUE);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();

                            generateAndShowAlterer("خطا" , "خطایی در برقراری ارتباط با سرور" , Color.RED);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                apiInterface.addMessage(ServerConfig.getApiKey() ,"چت آغاز شده است" ,
                        textChatID.getText().toString() , cg.getValue("userid","notDefined")).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                textChatID.setText("");
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

}
