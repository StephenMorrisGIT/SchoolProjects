package com.example.androidnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SourcesAdapter(val sources: List<NewsSources>) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sourceName: TextView = itemView.findViewById(R.id.sourceName)
        val sourceDescription: TextView = itemView.findViewById(R.id.sourceDescription)
        val checkBox: CheckBox = itemView.findViewById(R.id.sourceCheckBox)

        val sourceCard: CardView = itemView.findViewById(R.id.sourceCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesAdapter.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.row_sources, parent, false)
        return SourcesAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sources.size
    }

    override fun onBindViewHolder(holder: SourcesAdapter.ViewHolder, position: Int) {
        val currentSource = sources[position]
        holder.sourceName.text = currentSource.websiteName
        holder.sourceDescription.text = currentSource.websiteDescription
        // holder.articleContent.text = currentSource.content

        /*
        holder.checkBox.setOnCheckedChangeListener { CheckBox, isChecked ->
            val context = holder.sourceCard.context

        }
         */
    }
    interface MyCustomListener {
        fun somethingHappened(someData: String)
    }



}