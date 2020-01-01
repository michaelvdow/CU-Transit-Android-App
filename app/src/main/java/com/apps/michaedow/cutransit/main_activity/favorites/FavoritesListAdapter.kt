package com.apps.michaedow.cutransit.main_activity.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.database.Favorites.FavoritesItem
import com.apps.michaedow.cutransit.main_activity.TabFragmentDirections

class FavoritesListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<FavoritesListAdapter.FavoritesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var favorites = emptyList<FavoritesItem>()

    inner class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val stopName: TextView = itemView.findViewById(R.id.favorite_stop_name)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val action = TabFragmentDirections.actionTabFragmentToDeparturesFragment(view?.findViewById<TextView>(
                R.id.favorite_stop_name)?.text.toString())
            view?.findNavController()?.navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val itemView = inflater.inflate(R.layout.list_row_favorites, parent, false)
        return FavoritesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val current = favorites[position]
        holder.stopName.text = current.stopName
    }

    internal fun setFavorites(favorites: List<FavoritesItem>) {
        this.favorites = favorites
        notifyDataSetChanged()
    }

    override fun getItemCount() = favorites.size
}