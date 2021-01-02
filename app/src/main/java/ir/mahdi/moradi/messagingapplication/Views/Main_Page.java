package ir.mahdi.moradi.messagingapplication.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.tapadoo.alerter.Alerter;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ir.mahdi.moradi.messagingapplication.APIController.APIClient;
import ir.mahdi.moradi.messagingapplication.APIController.APIInterface;
import ir.mahdi.moradi.messagingapplication.Adapters.ChatDialogAdapter;
import ir.mahdi.moradi.messagingapplication.Adapters.ClickListener;
import ir.mahdi.moradi.messagingapplication.Models.ChatDialog;
import ir.mahdi.moradi.messagingapplication.R;
import ir.mahdi.moradi.messagingapplication.Utils.ConfigData;
import ir.mahdi.moradi.messagingapplication.Utils.ServerConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Main_Page extends Fragment implements ClickListener, NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private ChatDialogAdapter chatDialogAdapter;
    private LottieAnimationView animationView;
    private TextView txtEmpty,txtToolbar;
    private List<ChatDialog> dialogs;
    private RotateLoading rotateLoading;
    private FloatingActionButton fab;
    private ActionMode actionMode;
    private ActionModeCallBack modeCallBack = new ActionModeCallBack();
    private ConectivityReciver reciver;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView navName,navPhoneNumber;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main__page, container, false);
        recyclerView = v.findViewById(R.id.main_rec_chat_dialogs);
        animationView = v.findViewById(R.id.main_anim_empty);
        animationView.playAnimation();
        txtToolbar = v.findViewById(R.id.main_toolbar_text);
        txtEmpty = v.findViewById(R.id.main_txt_empty);
        toolbar = v.findViewById(R.id.main_toolbar_body);
        rotateLoading = v.findViewById(R.id.main_progress_loading);
        drawerLayout = v.findViewById(R.id.main_drawer);
        fab = v.findViewById(R.id.main_btn_add);
        navigationView = v.findViewById(R.id.main_navigation_view);
        reciver = new ConectivityReciver();
        getActivity().registerReceiver(reciver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        DoTheMainThing();

        return v;
    }


    private void CheckEmptyState(){
        if(chatDialogAdapter.getItemCount() != 0){
            animationView.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.GONE);
        }
        else{
            animationView.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void DoTheMainThing(){
        getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        toggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                toolbar,
                R.string.create_account,
                R.string.create_account
        );
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.toggle_icon);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(Gravity.RIGHT)){
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }else{
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });
        drawerLayout.addDrawerListener(toggle);
        View header  = navigationView.getHeaderView(0);
        navName = header.findViewById(R.id.nav_text_name);
        navPhoneNumber = header.findViewById(R.id.nav_text_phonenumber);
        setNavigationHeadrItems();
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Main_Page.this).navigate(R.id.action_main_Page_to_addCreateChat);
            }
        });
        dialogs = new ArrayList<>();
        chatDialogAdapter =new ChatDialogAdapter(dialogs , getActivity() , Main_Page.this);
        recyclerView.setAdapter(chatDialogAdapter);
        /*LoadDataFromServer();*/
    }

    private void LoadDataFromServer() {
        txtToolbar.setText("درحال اتصال...");
        animationView.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.GONE);
        rotateLoading.start();
        dialogs.clear();
        ConfigData cg = new ConfigData(getActivity());
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        apiInterface.getAllChats(ServerConfig.getApiKey() ,cg.getValue("userid" , "0")).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String res = response.body().string();
                    JSONArray chats = new JSONArray(res);
                    List<ChatDialog> tdialogs = new ArrayList<>();
                    for(int i=0 ; i < chats.length() ; i++){
                        ChatDialog chatDialog = new ChatDialog();
                        chatDialog.setId(chats.getJSONObject(i).getString("chatid"));
                        chatDialog.setChatName(chats.getJSONObject(i).getString("chat_name"));
                        chatDialog.setLastMessage(chats.getJSONObject(i).getString("msg"));
                        chatDialog.setAvatarURL("nothing");
                        tdialogs.add(chatDialog);
                    }
                    dialogs.addAll(tdialogs);
                    chatDialogAdapter.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , RecyclerView.VERTICAL , false));
                    CheckEmptyState();
                    txtToolbar.setText("متصل");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                rotateLoading.stop();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                rotateLoading.stop();
                generateAndShowAlterer("خطا" ,  "خطای برقراری ارتباط با سرور" , Color.RED);
                CheckEmptyState();
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


    private void toggleSelection(int position){
        chatDialogAdapter.toggleSelection(position);
        int count = chatDialogAdapter.getSelectedItemCount();
        if(count==0){
            actionMode.finish();
        }else{
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onItemClicked(int position) {
        if(actionMode != null){
            toggleSelection(position);
        }
        else {

        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if(actionMode == null){
            actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(modeCallBack);
            chatDialogAdapter.isActionUp = true;
        }
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.change_account){

            Toast.makeText(getActivity(), "Test for changing account", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.exit_account){
            ConfigData cg = new ConfigData(getActivity());
            cg.setValue("username" , "0");
            cg.setValue("phonenumber" , "notDefined");
            cg.setValue("userid" , "notDefined");
            cg.setValue("name" , "0");
            NavHostFragment.findNavController(Main_Page.this).navigate(R.id.action_main_Page_to_sign_in_screen);
        }
        drawerLayout.closeDrawer(Gravity.RIGHT);
        return true;
    }

    private class ActionModeCallBack implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.main_contex_menu , menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.main_delete_chat:
                    DeleteUserChats();
                    chatDialogAdapter.removeItems(chatDialogAdapter.getSelectedItems());
                    CheckEmptyState();
                    chatDialogAdapter.clearSelection();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            chatDialogAdapter.clearSelection();
            actionMode = null;
            chatDialogAdapter.isActionUp = false;
        }
    }
    private class ConectivityReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager =(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null && networkInfo.isConnected();

            if(isConnected)
            {
                txtToolbar.setText("متصل");
                if(chatDialogAdapter.getItemCount()==0)
                    LoadDataFromServer();
            }else{
                txtToolbar.setText("در انتظار شبکه...");
                CheckEmptyState();
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(reciver);
    }

    private void DeleteUserChats(){
        ConfigData cg = new ConfigData(getActivity());
        StringBuilder chatids = new StringBuilder();
        for(int i = 0 ; i < chatDialogAdapter.getSelectedItemCount() ; i++)
            if(i == chatDialogAdapter.getSelectedItemCount()-1)
                chatids.append(dialogs.get(chatDialogAdapter.getSelectedItems().get(i)).getId());
            else
            chatids.append(dialogs.get(chatDialogAdapter.getSelectedItems().get(i)).getId() + ",");
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);

        apiInterface.deleteUserChats(ServerConfig.getApiKey() ,cg.getValue("userid","0"),chatids.toString()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                generateAndShowAlterer("خطا" ,  "خطای برقراری ارتباط با سرور" , Color.RED);
            }
        });

    }


    private void setNavigationHeadrItems(){
        ConfigData cg = new ConfigData(getActivity());
        navName.setText(cg.getValue("name" , "نام"));
        navPhoneNumber.setText(cg.getValue("phonenumber" , "شماره تلفن"));
    }
}
