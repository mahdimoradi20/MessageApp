package ir.mahdi.moradi.messagingapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.mahdi.moradi.messagingapplication.Models.MessageModel;

import ir.mahdi.moradi.messagingapplication.R;

public class MessagesAdapeter extends RecyclerView.Adapter<MessagesAdapeter.MessageViewHolder> {

    private Context context;
    private List<MessageModel> messages;

    public MessagesAdapeter(Context context, List<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rec_messages_item , parent , false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        if(messages.get(position).getType().equals("sended")){

            holder.messageRoot.setGravity(Gravity.RIGHT);
            holder.messageRoot.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.messageCard.setBackgroundColor(Color.parseColor("#ECECEC"));
        }else{
            holder.messageRoot.setGravity(Gravity.LEFT);
            holder.messageRoot.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            holder.messageCard.setBackgroundColor(Color.parseColor("#8DB8E3"));
        }
        holder.textContent.setText(messages.get(position).getContent());
        holder.textName.setText(messages.get(position).getName());
        holder.textDate.setText(messages.get(position).getDateTime());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(MessageModel messageModel){
        messages.add(messageModel);
        notifyDataSetChanged();
    }

    public void addMessages(List<MessageModel> messageModelTemp){
        messages.addAll(messageModelTemp);
        notifyDataSetChanged();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView textName , textContent , textDate;
        public CircleImageView imgProfile;
        public ImageView imgState;
        public LinearLayout messageRoot;
        public CardView messageCard;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.rec_message_name);
            textContent = itemView.findViewById(R.id.rec_message_text);
            textDate = itemView.findViewById(R.id.rec_message_date);
            imgProfile = itemView.findViewById(R.id.rec_message_img_profile);
            imgState = itemView.findViewById(R.id.rec_message_state);
            messageRoot = itemView.findViewById(R.id.rec_message_root);
            messageCard = itemView.findViewById(R.id.rec_message_card);
        }
    }
}
