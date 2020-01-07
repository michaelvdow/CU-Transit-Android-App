package com.apps.michaeldow.cutransitcompanion.departures

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.apps.michaeldow.cutransitcompanion.API.responses.departureResponse.Departure
import com.apps.michaeldow.cutransitcompanion.R


class DeparturesListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<DeparturesListAdapter.DeparturesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var departures: List<Departure> = emptyList()

    inner class DeparturesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val routeName: TextView = itemView.findViewById(R.id.departure_route_name)
        val destination: TextView = itemView.findViewById(R.id.destination)
        val expectedTime: TextView = itemView.findViewById(R.id.expected_time)
        val number: TextView = itemView.findViewById(R.id.departure_number)
        val iStop: ImageView = itemView.findViewById(R.id.departure_i_stop)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View?) {
            val action = DeparturesFragmentDirections.actionDeparturesFragmentToRouteFragment(departures[adapterPosition])
            view?.findNavController()?.navigate(action)
        }

        override fun onLongClick(view: View?): Boolean {
            longClickListener?.onLongClick(departures[adapterPosition])
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeparturesViewHolder {
        val itemView = inflater.inflate(R.layout.list_row_departure_new, parent, false)
        return DeparturesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeparturesViewHolder, position: Int) {
        // Set text for row
        val departure = departures.get(position)
        holder.destination.text = holder.itemView.context.getString(R.string.to) + " " + departure.trip.trip_headsign
        holder.expectedTime.text = departure.expected_mins.toString()

        // I stop
        if (!departure.is_istop) {
            holder.iStop.visibility = View.GONE
        }

        // Set color for row
        val rowColor = Color.parseColor("#" + departure.route.route_color)
        val textColor = Color.parseColor("#" + departure.route.route_text_color)

        holder.routeName.text = departure.headsign

        holder.number.text = departure.route.route_short_name
        holder.number.setTextColor(textColor)
        val drawable = holder.number.background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(rowColor, BlendMode.SRC_ATOP)
        } else {
            drawable.setColorFilter(rowColor, PorterDuff.Mode.SRC_ATOP)
        }
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