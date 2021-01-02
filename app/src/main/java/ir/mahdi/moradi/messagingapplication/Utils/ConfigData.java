package ir.mahdi.moradi.messagingapplication.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigData {
    private Context context;
    public ConfigData(Context context){
        this.context = context;
    }
    public void setValue(String key , String value){
        SharedPreferences shared = context.getSharedPreferences("config" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key , value);
        editor.commit();
    }
    public String getValue(String key , String def){
        SharedPreferences shared = context.getSharedPreferences("config" , Context.MODE_PRIVATE);
        return  shared.getString(key ,def);
    }
}
