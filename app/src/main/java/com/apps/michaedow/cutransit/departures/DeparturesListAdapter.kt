package com.apps.michaedow.cutransit.departures

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apps.michaedow.cutransit.API.Departure
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.database.Stops.StopItem

class DeparturesListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<DeparturesListAdapter.DeparturesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var departures: List<Departure> = emptyList()

    inner class DeparturesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val routeName: TextView = itemView.findViewById(R.id.route_name)
        val destination: TextView = itemView.findViewById(R.id.destination)
        val expectedTime: TextView = itemView.findViewById(R.id.expected_time)
        val minutes: TextView = itemView.findViewById(R.id.min)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View?) {

        }

        override fun onLongClick(view: View?): Boolean {
            longClickListener?.onLongClick(departures[adapterPosition])
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeparturesViewHolder {
        val itemView = inflater.inflate(R.layout.list_row_departure, parent, false)
        return DeparturesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeparturesViewHolder, position: Int) {
        // Set text for row
        val departure = departures.get(position)
        holder.routeName.text = departure.headsign
        holder.destination.text = holder.itemView.context.getString(R.string.to) + " " + departure.trip.trip_headsign
        holder.expectedTime.text = departure.expected_mins.toString()

        // Set color for row
        val rowColor = Color.parseColor("#" + departure.route.route_color)
        val textColor = Color.parseColor("#" + departure.route.route_text_color)
        holder.itemView.setBackgroundColor(rowColor)
        holder.routeName.setTextColor(textColor)
        holder.destination.setTextColor(textColor)
        holder.expectedTime.setTextColor(textColor)
        holder.minutes.setTextColor(textColor)
    }

    internal fun setDepartures(departures: List<Departure>) {
        this.departures = departures
        notifyDataSetChanged()
    }

    override fun getItemCount() = departures.size


    // Listener for long click
    interface OnDepartureLongClickListener {
        fun onLongClick(departure: Departure)
    }

    private var longClickListener: OnDepartureLongClickListener? = null

    fun setOnLongClickListener(longClickListener: OnDepartureLongClickListener) {
        this.longClickListener = longClickListener
    }
}