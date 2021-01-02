package ir.mahdi.moradi.messagingapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;

import ir.mahdi.moradi.messagingapplication.R;

public class introAdapter extends RecyclerView.Adapter<introAdapter.MyViewHolder> {

    private Context context;

    public introAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pager_item , parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        switch (position){
            case 0:
                holder.animationView.setAnimation(R.raw.fast);
                holder.animationView.playAnimation();
                holder.textView.setText("پشتیبانی از پیام رسانی در لحظه");
                break;
            case 1:
                holder.animationView.setAnimation(R.raw.communication);
                holder.animationView.playAnimation();
                holder.textView.setText("ایجا اتاق های گفتگو و ارتباط با دوستان");
                break;
            case 2:
                holder.animationView.setAnimation(R.raw.easy);
                holder.animationView.playAnimation();
                holder.textView.setText("استفاده ساده و بدون دردسر");
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public LottieAnimationView animationView;
        public TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animationView = itemView.findViewById(R.id.pager_animation);
            textView = itemView.findViewById(R.id.pager_txt_text);
        }
    }
}
