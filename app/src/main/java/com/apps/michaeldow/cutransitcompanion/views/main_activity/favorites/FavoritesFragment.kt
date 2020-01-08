package com.apps.michaeldow.cutransitcompanion.views.main_activity.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.michaeldow.cutransitcompanion.R
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesItem
import com.apps.michaeldow.cutransitcompanion.databinding.FragmentFavoritesBinding


class FavoritesFragment: Fragment(), FavoritesListAdapter.ReorderListener {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesListAdapter
    private val observer = Observer<List<FavoritesItem>> { favorites ->
        adapter.setFavorites(favorites as ArrayList<FavoritesItem>)
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
    }
}