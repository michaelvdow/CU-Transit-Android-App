package com.apps.michaedow.cutransit.main_activity.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaedow.cutransit.database.Favorites.FavoritesDatabase
import com.apps.michaedow.cutransit.database.Favorites.FavoritesItem

class FavoritesViewModel(application: Application): AndroidViewModel(application) {

    private val database: FavoritesDatabaseProvider

    val favorites: LiveData<List<FavoritesItem>>

    init {
        database = FavoritesDatabaseProvider(FavoritesDatabase.getDatabase(getApplication<Application>().applicationContext).favoritesDao())
        favorites = database.getFavorites()
    }

}