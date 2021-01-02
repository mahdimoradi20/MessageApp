package ir.mahdi.moradi.messagingapplication.Views;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
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


public class sign_in_screen extends Fragment {

    private TextView btnRegister;
    private Button btnSignIn;
    private TextInputEditText textUserName , textPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in_screen, container, false);
        btnRegister = v.findViewById(R.id.login_btn_register);
        textUserName = v.findViewById(R.id.login_text_username);
        textPassword = v.findViewById(R.id.login_text_password);
        btnSignIn = v.findViewById(R.id.login_btn_login);
        SetUpViews();
        return v;
    }

    private void SetUpViews(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(sign_in_screen.this).navigate(R.id.action_sign_in_screen_to_sign_up_screen);
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                apiInterface.authUser(ServerConfig.getApiKey() , textUserName.getText().toString() ,
                        textPassword.getText().toString()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String res = response.body().string();
                            JSONObject object = new JSONObject(res);
                            if(object.getString("status").equals("ok")){
                                JSONObject userInfo = object.getJSONObject("user");
                                ConfigData cg = new ConfigData(getActivity());
                                cg.setValue("username" , userInfo.getString("username"));
                                cg.setValue("phonenumber" , userInfo.getString("phonenumber"));
                                cg.setValue("userid" , userInfo.getString("userid"));
                                cg.setValue("name" , userInfo.getString("name"));

                                NavHostFragment.findNavController(sign_in_screen.this).navigate(R.id.action_sign_in_screen_to_main_Page);
                            }else{
                                generateAndShowAlterer("خطا" , "کابری با مشخصات گفته شده یافت نشد"  , Color.RED);
                            }
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
