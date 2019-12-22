package cz.mendelu.pef.locationbasedservices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.friends_cardview_item.view.*

class FriendsCardviewAdapter(listener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var friends : List<Friend> = ArrayList()
    private var mListener: ClickListener

    init {
        this.mListener = listener
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FriendsViewHolder ->{
                holder.bind(friends.get(position))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FriendsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.friends_cardview_item, parent, false),
            mListener
        )
    }

    fun submitList(friendsList: List<Friend>){
        friends = friendsList
    }

    class FriendsViewHolder(
        itemView: View,
        var mListener: ClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var friendImage = itemView.user_image_card
        var nameAndSurname = itemView.name_surname_card
        var email = itemView.user_mail

        fun bind(friend: Friend){
            Glide.with(friendImage.context).load(friend.image).into(friendImage);
            nameAndSurname.setText(friend.name + " " + friend.surname)
            email.setText(friend.email)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener.onPositionClicked(v, getAdapterPosition());
        }
    }
}