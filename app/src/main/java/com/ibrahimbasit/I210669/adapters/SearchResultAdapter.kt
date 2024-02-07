package com.ibrahimbasit.I210669.adapters

import com.ibrahimbasit.I210669.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ibrahimbasit.I210669.data.SearchResultItem


class SearchResultAdapter(private val searchResultItems: List<SearchResultItem>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (title, subtitle, isAval, price, isFav, avalText) = searchResultItems[position]
        holder.title.text = title
        holder.subtitle.text = subtitle
        holder.price.text = price
        holder.availableText.text = avalText

        if (isAval) {
            holder.availableIcon.setBackgroundResource(R.drawable.availability_indicator)
        } else {
            holder.availableIcon.setBackgroundResource(R.drawable.not_available_indicator)
            holder.availableText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.unavailableTextColor))
        }

        if (isFav) {
            holder.favoriteIcon.setBackgroundResource(R.drawable.favorite_icon)
        } else {
            holder.favoriteIcon.setBackgroundResource(R.drawable.not_favorite_icon)
        }


    }

    override fun getItemCount(): Int {
        return searchResultItems.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var subtitle: TextView
        var price: TextView
        var availableText: TextView
        var availableIcon : View
        var favoriteIcon : View

        init {
            title = itemView.findViewById(R.id.title)
            subtitle = itemView.findViewById<TextView>(R.id.subtitle)
            price = itemView.findViewById<TextView>(R.id.price)
            availableText = itemView.findViewById<TextView>(R.id.availability_text)
            availableIcon = itemView.findViewById<View>(R.id.availabilityIndicator)
            favoriteIcon = itemView.findViewById<View>(R.id.favoriteIcon)
        }
    }
}

