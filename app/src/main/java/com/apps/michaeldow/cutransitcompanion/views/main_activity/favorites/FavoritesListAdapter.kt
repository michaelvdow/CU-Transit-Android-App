package com.apps.michaeldow.cutransitcompanion.views.main_activity.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesItem
import com.apps.michaeldow.cutransitcompanion.views.main_activity.TabFragmentDirections
import kotlinx.android.synthetic.main.list_row_favorites.view.*

class FavoritesListAdapter internal constructor(
    val context: Context
) : RecyclerView.Adapter<FavoritesListAdapter.FavoritesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var favorites = ArrayList<FavoritesItem>()

    inner class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val stopName: TextView = itemView.findViewById(R.id.favorite_stop_name)
        var stopId = ""

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val action = TabFragmentDirections.actionTabFragmentToDeparturesFragment(stopId)
            view?.findNavController()?.navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val itemView = inflater.inflate(R.layout.list_row_favorites, parent, false)
        val viewHolder = FavoritesViewHolder(itemView)


        viewHolder.itemView.handle.setOnTouchListener { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                itemTouchHelper.startDrag(viewHolder)
            }
            return@setOnTouchListener true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val current = favorites[holder.adapterPosition]
        holder.stopName.text = current.stopName
        holder.stopId = current.stopId
    }

    internal fun setFavorites(favorites: ArrayList<FavoritesItem>) {
        this.favorites = favorites
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        val fromFavorites = favorites[from]
        favorites.removeAt(from)
        favorites.add(to, fromFavorites)
    }

    // https://github.com/yfujiki/Android-DragReorderSample/blob/master/app/src/main/java/com/yfujiki/android_dragreordersample/MainActivity.kt
    // Used to reorder recycler view
    val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {

            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                val adapter = recyclerView.adapter as FavoritesListAdapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                adapter.moveItem(from, to)
                adapter.notifyItemMoved(from, to)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                viewHolder?.itemView?.alpha = 1.0f
                reorderListener.onReorder()
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun getItemCount() = favorites.size

    // Interface for saving reordered favorites to database
    interface ReorderListener {
        fun onReorder()
    }

    private lateinit var reorderListener: ReorderListener

    fun setReorderListener(listener: ReorderListener) {
        reorderListener = listener
    }
}