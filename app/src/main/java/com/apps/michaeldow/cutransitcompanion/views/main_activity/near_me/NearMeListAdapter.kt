package com.apps.michaeldow.cutransitcompanion.views.main_activity.near_me

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.Utils.Distance
import com.apps.michaeldow.cutransitcompanion.Utils.SharedPreferenceKeys
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopItem
import com.apps.michaeldow.cutransitcompanion.views.main_activity.TabFragmentDirections

class NearMeListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<NearMeListAdapter.NearMeViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var prefs: SharedPreferences? = null
    private var stops = emptyList<StopItem>()
    private var location: Location? = null

    inner class NearMeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val stopName: TextView = itemView.findViewById(R.id.near_me_stop_name)
        val distance: TextView = itemView.findViewById(R.id.near_me_distance)
        var stopId: String = ""

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            if (view?.findNavController()?.currentDestination?.id== R.id.tabFragment) {
                val action = TabFragmentDirections.actionTabFragmentToDeparturesFragment(stopId)
                view?.findNavController()?.navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearMeViewHolder {
        val itemView = inflater.inflate(R.layout.list_row_near_me, parent, false)
        prefs = PreferenceManager.getDefaultSharedPreferences(parent.context)
        return NearMeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NearMeViewHolder, position: Int) {
        val current = stops[holder.adapterPosition]
        holder.stopName.text = current.stopName
        val isMetric: Boolean = prefs?.getBoolean(SharedPreferenceKeys.METRIC, false) ?: false
        val lat = current.stopLat.toDouble()
        val lon = current.stopLon.toDouble()
        holder.distance.text = Distance.calculateDistance(location, lat, lon, isMetric)
        holder.stopId = current.stopId
    }

    internal fun setStops(stops: List<StopItem>) {
        this.stops = stops
        notifyDataSetChanged()
    }

    internal fun setLocation(location: Location) {
        this.location = location
    }


    override fun getItemCount() = stops.size
}