package cz.mendelu.pef.locationbasedservices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.chat_cardview_received_message_item.view.*
import kotlinx.android.synthetic.main.chat_cardview_sent_message_item.view.*
import kotlinx.android.synthetic.main.chat_cardview_sharedlocation.view.*


class ChatCardViewAdapter(listener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages : List<ChatMessage> = ArrayList()
    private var mListener: ClickListener
    init {
        this.mListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==0){
            return ChatMessagesViewHolder_MessageTo(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_cardview_sent_message_item, parent, false), mListener
            )
        }
        else{
            return ChatMessagesViewHolder_MessageFrom(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_cardview_received_message_item, parent, false), mListener
            )
        }
    }

    fun submitList(messageList: List<ChatMessage>){
        messages = messageList
    }

    override fun getItemViewType(position: Int): Int {
        return messages.get(position).viewType
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)==0){
            when(holder){
                is ChatMessagesViewHolder_MessageTo ->{
                    holder.bind(messages.get(position))
                }
            }
        }
        else{
            when(holder){
                is ChatMessagesViewHolder_MessageFrom ->{
                    holder.bind(messages.get(position))
                }
            }
        }
    }

    class ChatMessagesViewHolder_MessageFrom constructor(
        itemView: View,
        var mListener: ClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var senderImage = itemView.senderImage_ReceivedMessage
        var receivedMessage = itemView.receivedMessage
        var dateAndTime = itemView.receivedMessageDate

        fun bind(message: ChatMessage){
            Glide.with(senderImage.context).load(message.senderImage).into(senderImage);
            receivedMessage.setText(message.text)
            dateAndTime.setText(message.time)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onPositionClicked(v, getAdapterPosition());
        }
    }

    class ChatMessagesViewHolder_MessageTo constructor(
        itemView: View,
        var mListener: ClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var senderImage = itemView.senderImage_sentMessage
        var sentMessage = itemView.sentMessage
        var dateAndTime = itemView.sentMessageDate

        fun bind(message: ChatMessage){
            Glide.with(senderImage.context).load(message.senderImage).into(senderImage);
            sentMessage.setText(message.text)
            dateAndTime.setText(message.time)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onPositionClicked(v, getAdapterPosition());
        }
    }
}