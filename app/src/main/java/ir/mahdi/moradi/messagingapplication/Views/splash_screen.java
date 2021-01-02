package ir.mahdi.moradi.messagingapplication.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;

import ir.mahdi.moradi.messagingapplication.R;
import ir.mahdi.moradi.messagingapplication.Utils.ConfigData;


public class splash_screen extends Fragment {

    private LottieAnimationView animationView;
    private String nexrDestination;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_splash_screen, container, false);
        animationView = v.findViewById(R.id.splash_anim);
        SetUpViews();
        MainJob();
        return v;
    }

    private void SetUpViews(){
        animationView.playAnimation();
        nexrDestination = "intro";
    }
    private void MainJob(){

        ConfigData cg = new ConfigData(getActivity());
        if(cg.getValue("is_intro" , "no").equals("yes")){

            if(cg.getValue("username" , "0").equals("0") &&
                    cg.getValue("userid" , "notDefined").equals("notDefined"))
                nexrDestination = "login";
            else nexrDestination = "main";
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(nexrDestination.equals("intro")){

                   NavHostFragment.findNavController(splash_screen.this).navigate(R.id.action_splash_screen_to_intro_screen);
                }else if(nexrDestination.equals("main")){
                    NavHostFragment.findNavController(splash_screen.this).navigate(R.id.action_splash_screen_to_main_Page);
                }
                else{
                    NavHostFragment.findNavController(splash_screen.this).navigate(R.id.action_splash_screen_to_sign_in_screen);
                }
            }
        } , 2500);
    }
}
