package com.example.collegeschedule.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Расширение для создания DataStore
private val Context.dataStore by preferencesDataStore("favorites_settings")

class FavoritesManager(private val context: Context) {

    // Ключ для хранения набора строк
    private val FAVORITE_GROUPS_KEY = stringSetPreferencesKey("favorite_groups")

    // Поток данных, который автоматически обновляется при изменениях
    val favoritesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
        }

    // добавить,удалить
    suspend fun toggleFavorite(groupName: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
            if (currentFavorites.contains(groupName)) {
                preferences[FAVORITE_GROUPS_KEY] = currentFavorites - groupName
            } else {
                preferences[FAVORITE_GROUPS_KEY] = currentFavorites + groupName
            }
        }
    }
}