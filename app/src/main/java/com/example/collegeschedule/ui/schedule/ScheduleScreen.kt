package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.dto.StudentGroupDto
import com.example.collegeschedule.data.local.FavoritesManager
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.ui.components.GroupSelector
import com.example.collegeschedule.utils.getWeekDateRange
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    favoritesManager: FavoritesManager,
    currentGroup: String,
    onGroupChange: (String) -> Unit // Сообщаем наверх об изменении
) {

    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var allGroups by remember { mutableStateOf<List<StudentGroupDto>>(emptyList()) }


    val favorites by favoritesManager.favoritesFlow.collectAsState(initial = emptySet())
    val isFavorite = favorites.contains(currentGroup)

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        try { allGroups = RetrofitInstance.api.getGroups() } catch (_: Exception) {}
    }

    // Загрузка расписания при изменении
    LaunchedEffect(currentGroup) {
        if (currentGroup.isNotEmpty()) {
            loading = true
            error = null
            val (start, end) = getWeekDateRange()
            try {
                schedule = RetrofitInstance.api.getSchedule(currentGroup, start, end)
            } catch (e: Exception) {
                error = e.message
                schedule = emptyList()
            } finally {
                loading = false
            }
        }
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Выпадающий список
            Box(modifier = Modifier.weight(1f)) {
                GroupSelector(
                    groups = allGroups,
                    selectedGroup = currentGroup,
                    onGroupSelected = onGroupChange
                )
            }

            // Кнопка лайка
            if (currentGroup.isNotEmpty()) {
                IconButton(
                    onClick = {
                        scope.launch { favoritesManager.toggleFavorite(currentGroup) }
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            error != null -> Text("Ошибка: $error", Modifier.padding(16.dp))
            schedule.isNotEmpty() -> ScheduleList(schedule)
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Выберите группу") }
        }
    }
}