package ir.mahdi.moradi.messagingapplication.Views;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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
import ir.mahdi.moradi.messagingapplication.Utils.ServerConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class sign_up_screen extends Fragment {

    private TextInputEditText textName,textPhoneNumber,textUserName,textPassword,textConfirmPassword;
    private Button btnRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_up_screen, container, false);
        textName = v.findViewById(R.id.register_text_name);
        textUserName = v.findViewById(R.id.register_text_username);
        textPhoneNumber = v.findViewById(R.id.register_text_phonenumber);
        textPassword = v.findViewById(R.id.register_text_password);
        textConfirmPassword = v.findViewById(R.id.register_text_confirm_password);
        btnRegister = v.findViewById(R.id.register_btn_register);
        SetUpViews();
        return v;
    }

    private void SetUpViews(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = CheckInformation(textName.getText().toString() , textUserName.getText().toString()
                                                ,textPhoneNumber.getText().toString() , textPassword.getText().toString()
                                                ,textConfirmPassword.getText().toString());
                if(state.equals("fine")){
                    APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
                    apiInterface.addUser(ServerConfig.getApiKey(),textName.getText().toString(),
                                         textUserName.getText().toString(),textPassword.getText().toString(),
                                          "nothing" , textPhoneNumber.getText().toString(),
                                           "nothing" , "nothing").enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String res = response.body().string();

                                JSONObject jsonObject = new JSONObject(res);
                                if(jsonObject.getString("status").equals("ok"))
                                {
                                    Toast.makeText(getActivity(), "با موفیت ثبت نام شد. حال وارد شوید", Toast.LENGTH_SHORT).show();
                                    NavHostFragment.findNavController(sign_up_screen.this).navigate(R.id.action_sign_up_screen_to_sign_in_screen);
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
                }else{
                    generateAndShowAlterer("خطا" , state , Color.RED);
                }
            }
        });
    }

    private String CheckInformation(String name , String username , String phoneNumber , String password  ,String confirmedPassword){

        if(username.equals("") || phoneNumber.equals("") || password.equals("") || confirmedPassword.equals("") || name.equals("")){
            return "لطفا همه موارد گفته شد را پر کنید";
        }

        if(phoneNumber.length() != 11){
            return "شماره تلفن باید 11 رقم باشد";
        }
        if(!phoneNumber.startsWith("09")){
            return "شماره تلفن وارد شده معتبر نیست";
        }

        if( !password.equals(confirmedPassword)){
            return "رمز عبور با تکرار آن مطابق نیست";
        }
        if(Character.isDigit(username.charAt(0)))
        {
            return "نام کاربری نمیتواند با عدد شروع شود";
        }
        return "fine";
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
