package ir.mahdi.moradi.messagingapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ir.mahdi.moradi.messagingapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.mahdi.moradi.messagingapplication.Models.ChatDialog;

public class ChatDialogAdapter extends RecyclerView.Adapter<ChatDialogAdapter.MyViewHolder> {

    private List<ChatDialog> items;
    private Context context;
    private ClickListener mListener;
    private SparseBooleanArray selectedItems;
    public boolean isActionUp = false;

    public ChatDialogAdapter(List<ChatDialog> items , Context context , ClickListener mListener){
        this.items = items;
        this.context = context;
        this.mListener = mListener;
        this.selectedItems = new SparseBooleanArray();
    }

    public boolean isSelected(int position){
        return getSelectedItems().contains(position);
    }

    public void toggleSelection(int position){
        if(selectedItems.get(position,false))
            selectedItems.delete(position);
        else
            selectedItems.put(position, true);
        notifyItemChanged(position);
    }

    public void selectItem(int position){
        if(!selectedItems.get(position ,false))
            selectedItems.put(position,true);
        notifyItemChanged(position);

    }
    public void deSlecteItem(int position){
        if( selectedItems.get(position , false))
            selectedItems.delete(position);
        notifyItemChanged(position);
    }
    public void clearSelection(){
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for(Integer i : selection){
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemCount(){
        return selectedItems.size();
    }
    public List<Integer> getSelectedItems(){
        List<Integer> items_t = new ArrayList<>(selectedItems.size());
        for(int i = 0 ; i < selectedItems.size(); i++)
            items_t.add(selectedItems.keyAt(i));
        return items_t;
    }




    public void removeItem(int position){
        items.remove(position);
        notifyItemRemoved(position);
    }
    public void removeRange(int positionStart, int itemCount){
        for(int i=0 ; i< itemCount ; i++)
            items.remove(positionStart);
        notifyItemRangeRemoved(positionStart , itemCount);
    }

    public void removeItems(List<Integer> positions){
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }
        });
        while(!positions.isEmpty()){
            if(positions.size() == 1){
                removeItem(positions.get(0));
                positions.remove(0);
            }else{
                int count = 1;
                while(positions.size() > count && positions.get(count).equals(positions.get(count-1)-1))
                    ++count;
                if(count==1){
                    removeItem(positions.get(0));
                }else{
                    removeRange(positions.get(count-1) , count);
                }
                for(int i =0 ; i < count ; ++i)
                    positions.remove(0);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rec_chatdialog_item , parent , false);
        return new MyViewHolder(v , mListener ,context);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if(isSelected(position)) holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.selectedChat));
        else holder.root.setBackgroundColor(Color.WHITE);
        holder.txtName.setText(items.get(position).getChatName());
        holder.txtName.setSelected(true);
        holder.txtMessage.setText(items.get(position).getLastMessage());
        if(! items.get(position).getAvatarURL().equals("nothing"))
            Picasso.get().load(items.get(position).getAvatarURL()).into(holder.circleImageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(! isActionUp) {
                        Bundle bundle = new Bundle();
                        bundle.putString("chat_id", items.get(position).getId());
                        bundle.putString("name", items.get(position).getChatName());
                        Navigation.findNavController(v).navigate(R.id.action_main_Page_to_chat, bundle);
                    }else{
                        holder.mListener.onItemClicked(position);
                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView circleImageView;
        public TextView txtName , txtMessage;
        public RelativeLayout root;
        public ClickListener mListener;


        public MyViewHolder(@NonNull View itemView , final ClickListener listener , Context context) {
            super(itemView);
            this.mListener = listener;
            circleImageView = itemView.findViewById(R.id.rec_chatdialog_item_image);
            txtName = itemView.findViewById(R.id.rec_chatdialog_item_txt_name);
            txtMessage = itemView.findViewById(R.id.rec_chatdialog_item_txt_last_message);
            root = itemView.findViewById(R.id.rec_chatdialogs_root);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) mListener.onItemClicked(getLayoutPosition());
                }
            });
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mListener != null) return mListener.onItemLongClicked(getLayoutPosition());
                    return false;
                }
            });
        }
    }
}
