package edu.gwu.androidtweets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class TweetsAdapter(val tweets: List<Tweet>) : RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val username: TextView = itemView.findViewById(R.id.username)
        val handle: TextView = itemView.findViewById(R.id.handle)
        val content: TextView = itemView.findViewById(R.id.tweet_content)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.row_tweets, parent, false)
        return ViewHolder(view)

    }

    // How many rows in total do you want to render
    override fun getItemCount(): Int {
        return tweets.size
    }

    // The Recycler is ready to display a new row - fill it with content
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTweet = tweets[position]
        holder.username.text = currentTweet.username
        holder.handle.text = currentTweet.handle
        holder.handle.text = currentTweet.content

        if(currentTweet.iconUrl.isNotEmpty()){
            Picasso.get().setIndicatorsEnabled((true))

            Picasso.get().load(currentTweet.iconUrl).into(holder.icon)
        }

    }
}