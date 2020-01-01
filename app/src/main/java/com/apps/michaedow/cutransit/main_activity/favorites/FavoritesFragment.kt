package com.apps.michaedow.cutransit.main_activity.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.michaedow.cutransit.R
import com.apps.michaedow.cutransit.databinding.FragmentFavoritesBinding

class FavoritesFragment: Fragment() {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesListAdapter

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

    }

    private fun observeViewModel(viewModel: FavoritesViewModel) {
        viewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->
            adapter.setFavorites(favorites)
            if (favorites.size == 0) {
                binding.favoritesEmptyText.visibility = View.VISIBLE
                binding.favoritesList.visibility = View.GONE
            } else {
                binding.favoritesEmptyText.visibility = View.GONE
                binding.favoritesList.visibility = View.VISIBLE
            }
        })

    }
}