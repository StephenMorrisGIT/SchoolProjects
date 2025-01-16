package com.example.androidnews

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ArticlesAdapter(val newsArticles: List<NewsArticles>) : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>()  {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val articleTitle: TextView = itemView.findViewById(R.id.articleTitle)
        val websiteTitle: TextView = itemView.findViewById(R.id.websiteTitle)
        val articleContent: TextView = itemView.findViewById(R.id.articleDescription)
        val icon: ImageView = itemView.findViewById(R.id.icon)

        val articleCard: CardView = itemView.findViewById(R.id.articleCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.row_articles, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsArticles.size
    }

    // The Recycler is ready to display a new row - fill it with content
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = newsArticles[position]
        holder.articleTitle.text = currentArticle.articleTitle
        holder.websiteTitle.text = currentArticle.websiteName
        holder.articleContent.text = currentArticle.content

        // IDE wants me to make an openUrl function but I definitely don't believe that
        holder.articleCard.setOnClickListener{ view ->
            val context = holder.articleCard.context
            val openUrl = Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle.url))
            context.startActivity(openUrl)
        }

        if(currentArticle.iconUrl.isNotEmpty()){
            Picasso.get().setIndicatorsEnabled((true))
            Picasso.get().load(currentArticle.iconUrl).into(holder.icon)
        }
    }

}