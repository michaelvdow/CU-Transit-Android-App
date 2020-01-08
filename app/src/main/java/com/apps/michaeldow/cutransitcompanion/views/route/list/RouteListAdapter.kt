package com.apps.michaeldow.cutransitcompanion.views.route.list

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.apps.michaeldow.cutransitcompanion.API.responses.stopTimesResponse.StopTime
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.Utils.Utils
import com.apps.michaeldow.cutransitcompanion.views.route.RouteFragmentDirections

class RouteListAdapter  internal constructor(
    context: Context
) : RecyclerView.Adapter<RouteListAdapter.RouteListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var stops = emptyList<StopTime>()
    lateinit var color: String
    lateinit var textColor: String

    inner class RouteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val time: TextView = itemView.findViewById(R.id.route_time)
        val stopName: TextView = itemView.findViewById(R.id.route_stop_name)
        val line: View = itemView.findViewById(R.id.vertical_line)
        val circle: ImageView = itemView.findViewById(R.id.circle)
        var stopId: String = ""

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val action = RouteFragmentDirections.actionRouteFragmentToDeparturesFragment(stopId)
            view?.findNavController()?.navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteListViewHolder {
        val itemView = inflater.inflate(R.layout.list_row_route, parent, false)
        return RouteListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RouteListViewHolder, position: Int) {
        val current = stops[holder.adapterPosition]
        holder.stopName.text = current.stop_point.stop_name
        holder.time.text = Utils.fixStopTime(current.arrival_time)

        // Set colors
        val rowColor = Color.parseColor("#" + color)

        holder.line.setBackgroundColor(rowColor)
        val drawable = holder.circle.drawable as GradientDrawable
        drawable.setColor(Color.WHITE)
        drawable.setStroke(16, rowColor)

        holder.stopId = current.stop_point.stop_id
    }

    internal fun setStops(stops: List<StopTime>) {
        this.stops = stops
        notifyDataSetChanged()
    }

    fun setRouteColor(color: String) {
        this.color = color
    }

    fun setRouteTextColor(textColor: String) {
        this.textColor = textColor
    }

    override fun getItemCount() = stops.size


}