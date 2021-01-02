package ir.mahdi.moradi.messagingapplication.Views;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ir.mahdi.moradi.messagingapplication.Adapters.introAdapter;
import ir.mahdi.moradi.messagingapplication.R;
import ir.mahdi.moradi.messagingapplication.Utils.ConfigData;
import me.relex.circleindicator.CircleIndicator3;


public class intro_screen extends Fragment {

    private ImageView btnSkip;
    private ViewPager2 viewPager2;
    private CircleIndicator3 indicator3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_intro_screen, container, false);

        btnSkip = v.findViewById(R.id.intro_btn_skip);
        viewPager2 = v.findViewById(R.id.into_pager);
        indicator3 = v.findViewById(R.id.intro_indicator);
        SetUpViews();
        return v;
    }

    private void SetUpViews(){
        viewPager2.setAdapter(new introAdapter(getActivity()));
        indicator3.setViewPager(viewPager2);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigData cg = new ConfigData(getActivity());
                cg.setValue("is_intro" , "yes");
                NavHostFragment.findNavController(intro_screen.this).navigate(R.id.action_intro_screen_to_sign_in_screen);
            }
        });
    }
}
