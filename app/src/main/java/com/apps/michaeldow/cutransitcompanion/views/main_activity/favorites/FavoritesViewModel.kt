package com.apps.michaeldow.cutransitcompanion.views.main_activity.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesDatabase
import com.apps.michaeldow.cutransitcompanion.database.Favorites.FavoritesItem
import com.apps.michaeldow.cutransitcompanion.database.Favorites.OldFavorites.OldFavoritesDatabase
import com.apps.michaeldow.cutransitcompanion.database.Stops.StopDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class FavoritesViewModel(application: Application): AndroidViewModel(application) {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val database: FavoritesDatabaseProvider

    val favorites: LiveData<List<FavoritesItem>>
    private val mutableUpdating: MutableLiveData<Boolean> = MutableLiveData(false)
    val updating: LiveData<Boolean>
        get() = mutableUpdating

    init {
        database = FavoritesDatabaseProvider(FavoritesDatabase.getDatabase(getApplication<Application>().applicationContext).favoritesDao())
        favorites = database.getFavorites()
    }

    fun updateFavoritesOrder(favorites: ArrayList<FavoritesItem>) {
        mutableUpdating.postValue(true)
        scope.launch {
            for (i in 0 until favorites.size) {
                database.updateFavorite(favorites[i].stopId, i)
            }
            mutableUpdating.postValue(false)
        }
    }

    fun getOldFavorites() {
        scope.launch {
            try {
                val oldDao = OldFavoritesDatabase.getDatabase(getApplication<Application>().applicationContext).oldFavoritesDao()
                val stopDao = StopDatabase.getDatabase(getApplication<Application>().applicationContext).stopDao()
                database.getOldFavorites(oldDao, stopDao)
            } catch (e: Exception) {
                println(e)
            }
        }
    }

}