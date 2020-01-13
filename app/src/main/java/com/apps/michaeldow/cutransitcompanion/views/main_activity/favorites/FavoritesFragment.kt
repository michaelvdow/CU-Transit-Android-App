package com.apps.michaeldow.cutransitcompanion.views.main_activity.favorites

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.Utils.SharedPreferenceKeys
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesItem
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentFavoritesBinding
import com.apps.michaeldow.cutransitcompanion.views.main_activity.MainActivity


class FavoritesFragment: Fragment(), FavoritesListAdapter.ReorderListener {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesListAdapter
    private val observer = Observer<List<FavoritesItem>> { favorites ->
        adapter.setFavorites(favorites as ArrayList<FavoritesItem>)
        updateShortcuts(favorites)
        if (favorites.size == 0) {
            binding.favoritesEmptyText.visibility = View.VISIBLE
            binding.favoritesList.visibility = View.GONE
        } else {
            binding.favoritesEmptyText.visibility = View.GONE
            binding.favoritesList.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
        observeViewModel(viewModel)

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (!prefs.getBoolean(SharedPreferenceKeys.OLD_FAVORITES, false)) {
            viewModel.getOldFavorites()
            val editor = prefs.edit()
            editor.putBoolean(SharedPreferenceKeys.OLD_FAVORITES, true)
            editor.apply()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup recycler view
        val recyclerView = binding.favoritesList
        adapter = FavoritesListAdapter(this.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        adapter.itemTouchHelper.attachToRecyclerView(recyclerView)
        adapter.setReorderListener(this)

    }

    private fun observeViewModel(viewModel: FavoritesViewModel) {
        viewModel.favorites.observe(viewLifecycleOwner, observer)

        viewModel.updating.observe(viewLifecycleOwner, Observer { updating ->
            if (updating) {
                viewModel.favorites.removeObserver(observer)
            } else {
                viewModel.favorites.observe(viewLifecycleOwner, observer)
            }
        })
    }

    override fun onReorder() {
        val favorites = adapter.favorites
        viewModel.updateFavoritesOrder(favorites)
        updateShortcuts(favorites)
    }

    private fun updateShortcuts(favorites: ArrayList<FavoritesItem>) {
        val context = context
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortcutManager = getSystemService<ShortcutManager>(context, ShortcutManager::class.java)
                val shortcuts = ArrayList<ShortcutInfo>()
                for (i in 0 until min(4, favorites.size)) {

                    val intent = Intent().apply {
                        setAction(Intent.ACTION_VIEW)
                        setComponent(ComponentName(context, MainActivity::class.java))
                        setData(Uri.parse("http://www.cutransit.com/departures/${favorites[i].stopId}"))
                    }

                    val shortcut = ShortcutInfo.Builder(context, "id$i")
                        .setShortLabel(favorites[i].stopName)
                        .setLongLabel(favorites[i].stopName)
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_shortcut))
                        .setIntent(intent)
                        .build()

                    shortcuts.add(shortcut)
                }
                shortcutManager?.dynamicShortcuts = shortcuts
            }
        }
    }

    private fun min(a: Int, b: Int): Int {
        if (a < b) {
            return a
        }
        return b
    }
}